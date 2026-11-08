package com.masterclass.kafka.producer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.masterclass.kafka.KafkaOperationException;
import com.masterclass.kafka.config.KafkaConfig;
import com.masterclass.kafka.json.JsonSupport;
import com.masterclass.kafka.validation.JsonSchemaValidator;
import com.masterclass.kafka.validation.MessageValidationException;

/**
 * Sends JSON messages to Kafka.
 *
 * <p>Payloads can be any object (serialised with Jackson) or an already-built JSON string. When a
 * {@link JsonSchemaValidator} is supplied, every message is validated <b>before</b> it is sent, so
 * invalid data never reaches the topic. Each record gets a {@code content-type: application/json}
 * header.
 *
 * <p>Thread-safe: share one instance per application/test class and close it at the end.
 */
public class KafkaMessageProducer implements AutoCloseable {

	public static final String CONTENT_TYPE_HEADER = "content-type";
	public static final String JSON_CONTENT_TYPE = "application/json";

	private static final Logger log = LoggerFactory.getLogger(KafkaMessageProducer.class);

	private final Producer<String, String> producer;
	private final JsonSchemaValidator validator;
	private final Duration timeout;

	/** Producer without schema validation. */
	public KafkaMessageProducer(KafkaConfig config) {
		this(config, null);
	}

	/** Producer that validates every message against {@code validator} (may be null). */
	public KafkaMessageProducer(KafkaConfig config, JsonSchemaValidator validator) {
		this(new KafkaProducer<>(config.producerProperties()), validator, config.getOperationTimeout());
	}

	/** For injecting any {@link Producer}, e.g. Kafka's MockProducer in unit tests. */
	public KafkaMessageProducer(Producer<String, String> producer, JsonSchemaValidator validator, Duration timeout) {
		this.producer = Objects.requireNonNull(producer, "producer");
		this.validator = validator;
		this.timeout = Objects.requireNonNull(timeout, "timeout");
	}

	/** Sends and waits for the broker's acknowledgement. */
	public RecordMetadata send(String topic, String key, Object payload) {
		return send(topic, key, payload, Map.of());
	}

	/** Sends with extra headers and waits for the broker's acknowledgement. */
	public RecordMetadata send(String topic, String key, Object payload, Map<String, String> headers) {
		CompletableFuture<RecordMetadata> future = sendAsync(topic, key, payload, headers);
		try {
			return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new KafkaOperationException("Interrupted while sending to " + topic, e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause() != null ? e.getCause() : e;
			throw new KafkaOperationException("Failed to send to " + topic + ": " + cause.getMessage(), cause);
		} catch (TimeoutException e) {
			throw new KafkaOperationException("Timed out after " + timeout + " sending to " + topic, e);
		}
	}

	/**
	 * Sends without blocking. Validation still happens immediately: an invalid payload throws
	 * {@link MessageValidationException} here, before anything is sent.
	 */
	public CompletableFuture<RecordMetadata> sendAsync(String topic, String key, Object payload,
			Map<String, String> headers) {
		ProducerRecord<String, String> record = toRecord(topic, key, payload, headers);
		CompletableFuture<RecordMetadata> result = new CompletableFuture<>();
		producer.send(record, (metadata, exception) -> {
			if (exception != null) {
				log.error("Send to {} failed for key {}", topic, key, exception);
				result.completeExceptionally(exception);
			} else {
				log.debug("Sent key {} to {}-{}@{}", key, metadata.topic(), metadata.partition(), metadata.offset());
				result.complete(metadata);
			}
		});
		return result;
	}

	public void flush() {
		producer.flush();
	}

	@Override
	public void close() {
		producer.close(Duration.ofSeconds(10));
	}

	private ProducerRecord<String, String> toRecord(String topic, String key, Object payload,
			Map<String, String> headers) {
		Objects.requireNonNull(topic, "topic");
		Objects.requireNonNull(payload, "payload");
		Objects.requireNonNull(headers, "headers");

		String json = payload instanceof String s ? s : JsonSupport.toJson(payload);
		if (validator != null) {
			validator.validateOrThrow(json);
		}

		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, json);
		record.headers().add(CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE.getBytes(StandardCharsets.UTF_8));
		headers.forEach((name, value) -> record.headers().add(name, value.getBytes(StandardCharsets.UTF_8)));
		return record;
	}
}
