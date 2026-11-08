package com.masterclass.kafka.support;

import java.util.UUID;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.masterclass.kafka.KafkaOperationException;
import com.masterclass.kafka.admin.KafkaTopicManager;
import com.masterclass.kafka.config.KafkaConfig;
import com.masterclass.kafka.consumer.KafkaMessageConsumer;
import com.masterclass.kafka.validation.JsonSchemaValidator;

/**
 * Base class for tests that need a running Kafka broker.
 *
 * <p>Connects once per test class and fails fast with a clear message if the broker is down.
 * Tests isolate themselves with unique topic and consumer-group names, so runs never see each
 * other's data and nothing needs resetting between runs.
 */
public abstract class KafkaTestBase {

	protected static final JsonSchemaValidator ORDER_VALIDATOR = JsonSchemaValidator.fromClasspath(TestEvents.ORDER_SCHEMA);

	protected KafkaConfig config;
	protected KafkaTopicManager topicManager;

	@BeforeClass(alwaysRun = true)
	public void connectToKafka() {
		config = KafkaConfig.fromEnvironment();
		topicManager = new KafkaTopicManager(config);
		try {
			topicManager.listTopics();
		} catch (KafkaOperationException e) {
			topicManager.close();
			topicManager = null;
			throw new IllegalStateException("Kafka broker not reachable at " + config.getBootstrapServers()
					+ ". Start it with `docker compose --profile kafka up -d` from the workspace root,"
					+ " or set KAFKA_BOOTSTRAP_SERVERS / -Dkafka.bootstrap.servers.", e);
		}
	}

	@AfterClass(alwaysRun = true)
	public void disconnectFromKafka() {
		if (topicManager != null) {
			topicManager.close();
		}
	}

	/** e.g. uniqueName("orders") -> "orders-3f9c1a2b". Valid as a topic or group name. */
	protected static String uniqueName(String prefix) {
		return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
	}

	/** A consumer in a brand-new group, so it reads the topic from the beginning. Close it after use. */
	protected KafkaMessageConsumer newConsumer() {
		return new KafkaMessageConsumer(config, uniqueName("test-group"));
	}
}
