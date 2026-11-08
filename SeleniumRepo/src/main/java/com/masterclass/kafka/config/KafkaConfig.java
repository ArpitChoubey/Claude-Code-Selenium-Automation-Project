package com.masterclass.kafka.config;

import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Single place that builds client settings for the admin client, producer and consumer.
 *
 * <p>The broker address is read, in order, from the {@code -Dkafka.bootstrap.servers} system property,
 * the {@code KAFKA_BOOTSTRAP_SERVERS} environment variable, and finally {@code localhost:9092}.
 */
public final class KafkaConfig {

	public static final String BOOTSTRAP_PROPERTY = "kafka.bootstrap.servers";
	public static final String BOOTSTRAP_ENV = "KAFKA_BOOTSTRAP_SERVERS";
	public static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";

	private static final Duration DEFAULT_OPERATION_TIMEOUT = Duration.ofSeconds(30);
	private static final int REQUEST_TIMEOUT_MS = 15_000;

	private final String bootstrapServers;
	private final String clientIdPrefix;
	private final Duration operationTimeout;

	public KafkaConfig(String bootstrapServers, String clientIdPrefix, Duration operationTimeout) {
		if (bootstrapServers == null || bootstrapServers.isBlank()) {
			throw new IllegalArgumentException("bootstrapServers must not be blank");
		}
		this.bootstrapServers = bootstrapServers.trim();
		this.clientIdPrefix = Objects.requireNonNull(clientIdPrefix, "clientIdPrefix");
		this.operationTimeout = Objects.requireNonNull(operationTimeout, "operationTimeout");
	}

	/** Builds a config from system property / environment variable / default. */
	public static KafkaConfig fromEnvironment() {
		String servers = System.getProperty(BOOTSTRAP_PROPERTY);
		if (servers == null || servers.isBlank()) {
			servers = System.getenv(BOOTSTRAP_ENV);
		}
		if (servers == null || servers.isBlank()) {
			servers = DEFAULT_BOOTSTRAP_SERVERS;
		}
		return new KafkaConfig(servers, "masterclass", DEFAULT_OPERATION_TIMEOUT);
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	/** Upper bound for any single blocking operation (send, topic create/delete, ...). */
	public Duration getOperationTimeout() {
		return operationTimeout;
	}

	public Properties adminProperties() {
		Properties props = baseProperties("admin");
		props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);
		props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, (int) operationTimeout.toMillis());
		return props;
	}

	/**
	 * Producer tuned for reliability over raw throughput: every write is acknowledged by all
	 * in-sync replicas, and idempotence prevents duplicates and reordering on retry.
	 */
	public Properties producerProperties() {
		Properties props = baseProperties("producer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // max allowed with idempotence
		props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
		props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS);
		// must be >= linger.ms + request.timeout.ms
		props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) Math.max(operationTimeout.toMillis(), REQUEST_TIMEOUT_MS + 5));
		return props;
	}

	/**
	 * Consumer that reads a topic from the beginning for a new group and commits offsets
	 * manually, only after records have been handed to the caller.
	 */
	public Properties consumerProperties(String groupId) {
		if (groupId == null || groupId.isBlank()) {
			throw new IllegalArgumentException("groupId must not be blank");
		}
		Properties props = baseProperties("consumer");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
		return props;
	}

	private Properties baseProperties(String role) {
		Properties props = new Properties();
		props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(CommonClientConfigs.CLIENT_ID_CONFIG,
				clientIdPrefix + "-" + role + "-" + UUID.randomUUID().toString().substring(0, 8));
		return props;
	}

	@Override
	public String toString() {
		return "KafkaConfig{bootstrapServers='" + bootstrapServers + "', operationTimeout=" + operationTimeout + "}";
	}
}
