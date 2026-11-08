package com.masterclass.kafka.admin;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.masterclass.kafka.KafkaOperationException;
import com.masterclass.kafka.config.KafkaConfig;

/**
 * Creates, inspects and deletes topics with the Kafka Admin API.
 *
 * <p>Every method blocks until the operation has finished (or the configured timeout passes), so
 * a topic created here can be used immediately. Thread-safe; close it when done.
 */
public class KafkaTopicManager implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(KafkaTopicManager.class);
	private static final long POLL_INTERVAL_MS = 200;

	private final Admin admin;
	private final Duration timeout;

	public KafkaTopicManager(KafkaConfig config) {
		this(Admin.create(config.adminProperties()), config.getOperationTimeout());
	}

	/** For injecting a pre-built or test Admin client. */
	public KafkaTopicManager(Admin admin, Duration timeout) {
		this.admin = admin;
		this.timeout = timeout;
	}

	/**
	 * Creates the topic and waits until it is visible.
	 *
	 * @return true if it was created, false if it already existed
	 */
	public boolean createTopicIfNotExists(String topic, int partitions, short replicationFactor) {
		try {
			await(admin.createTopics(List.of(new NewTopic(topic, partitions, replicationFactor))).all(),
					"create topic " + topic);
		} catch (KafkaOperationException e) {
			if (e.getCause() instanceof TopicExistsException) {
				log.info("Topic {} already exists", topic);
				return false;
			}
			throw e;
		}
		waitUntil(() -> topicExists(topic), "topic " + topic + " to appear");
		log.info("Created topic {} ({} partitions, replication factor {})", topic, partitions, replicationFactor);
		return true;
	}

	public Set<String> listTopics() {
		return await(admin.listTopics().names(), "list topics");
	}

	public boolean topicExists(String topic) {
		return listTopics().contains(topic);
	}

	public TopicDescription describeTopic(String topic) {
		return await(admin.describeTopics(List.of(topic)).allTopicNames(), "describe topic " + topic).get(topic);
	}

	public int partitionCount(String topic) {
		return describeTopic(topic).partitions().size();
	}

	/** Deletes the topic and waits until it is gone. Deleting a missing topic is a no-op. */
	public void deleteTopic(String topic) {
		try {
			await(admin.deleteTopics(List.of(topic)).all(), "delete topic " + topic);
		} catch (KafkaOperationException e) {
			if (e.getCause() instanceof UnknownTopicOrPartitionException) {
				log.info("Topic {} does not exist; nothing to delete", topic);
				return;
			}
			throw e;
		}
		waitUntil(() -> !topicExists(topic), "topic " + topic + " to be deleted");
		log.info("Deleted topic {}", topic);
	}

	@Override
	public void close() {
		admin.close(Duration.ofSeconds(5));
	}

	private <T> T await(KafkaFuture<T> future, String action) {
		try {
			return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new KafkaOperationException("Interrupted while trying to " + action, e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause() != null ? e.getCause() : e;
			throw new KafkaOperationException("Failed to " + action + ": " + cause.getMessage(), cause);
		} catch (TimeoutException e) {
			throw new KafkaOperationException("Timed out after " + timeout + " trying to " + action, e);
		}
	}

	private void waitUntil(BooleanSupplier condition, String description) {
		long deadline = System.nanoTime() + timeout.toNanos();
		while (System.nanoTime() < deadline) {
			if (condition.getAsBoolean()) {
				return;
			}
			try {
				Thread.sleep(POLL_INTERVAL_MS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new KafkaOperationException("Interrupted while waiting for " + description, e);
			}
		}
		throw new KafkaOperationException("Timed out after " + timeout + " waiting for " + description);
	}
}
