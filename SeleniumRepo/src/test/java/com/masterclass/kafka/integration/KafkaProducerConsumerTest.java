package com.masterclass.kafka.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.masterclass.kafka.consumer.KafkaMessageConsumer;
import com.masterclass.kafka.model.OrderEvent;
import com.masterclass.kafka.producer.KafkaMessageProducer;
import com.masterclass.kafka.support.KafkaTestBase;
import com.masterclass.kafka.support.TestEvents;
import com.masterclass.kafka.validation.MessageValidationException;
import com.masterclass.kafka.validation.ValidationResult;

/**
 * End-to-end tests: produce JSON to a real broker, consume it back, validate it.
 * Uses its own unique topic (3 partitions), deleted afterwards. Needs a running Kafka broker.
 */
public class KafkaProducerConsumerTest extends KafkaTestBase {

	private static final Duration RECEIVE_TIMEOUT = Duration.ofSeconds(20);

	private String topic;
	private KafkaMessageProducer producer;

	@BeforeClass(alwaysRun = true)
	public void createTopicAndProducer() {
		topic = uniqueName("orders");
		topicManager.createTopicIfNotExists(topic, 3, (short) 1);
		producer = new KafkaMessageProducer(config, ORDER_VALIDATOR);
	}

	@AfterClass(alwaysRun = true)
	public void cleanUp() {
		if (producer != null) {
			producer.close();
		}
		if (topicManager != null && topic != null) {
			topicManager.deleteTopic(topic);
		}
	}

	@Test
	public void validOrderEventRoundTripsThroughKafka() {
		OrderEvent sent = TestEvents.validOrder();

		RecordMetadata metadata = producer.send(topic, sent.orderId(), sent, Map.of("source", "testng"));

		try (KafkaMessageConsumer consumer = newConsumer()) {
			consumer.subscribe(topic);
			Optional<ConsumerRecord<String, String>> received = consumer.pollForKey(sent.orderId(), RECEIVE_TIMEOUT);

			assertTrue(received.isPresent(), "Message with key " + sent.orderId() + " was not received");
			ConsumerRecord<String, String> record = received.get();

			ValidationResult validation = ORDER_VALIDATOR.validate(record.value());
			assertTrue(validation.valid(), "Received message failed schema validation: " + validation.errors());

			assertEquals(KafkaMessageConsumer.valueAs(record, OrderEvent.class), sent);
			assertEquals(record.partition(), metadata.partition());
			assertEquals(record.offset(), metadata.offset());
			assertEquals(KafkaMessageConsumer.header(record, KafkaMessageProducer.CONTENT_TYPE_HEADER),
					Optional.of(KafkaMessageProducer.JSON_CONTENT_TYPE));
			assertEquals(KafkaMessageConsumer.header(record, "source"), Optional.of("testng"));
		}
	}

	@Test
	public void invalidOrderEventIsRejectedAndNeverPublished() {
		String key = uniqueName("invalid");
		String invalidJson = "{\"orderId\":\"bad-id\",\"amount\":-1}";

		expectThrows(MessageValidationException.class, () -> producer.send(topic, key, invalidJson));

		try (KafkaMessageConsumer consumer = newConsumer()) {
			consumer.subscribe(topic);
			assertTrue(consumer.pollForKey(key, Duration.ofSeconds(5)).isEmpty(),
					"Invalid message must not reach the topic");
		}
	}

	@Test
	public void eventsWithSameKeyStayInOrderOnOnePartition() {
		String customerKey = uniqueName("customer");
		OrderEvent order = TestEvents.validOrder();
		List<OrderEvent> lifecycle = List.of(
				order,
				order.withStatus(OrderEvent.Status.PAID),
				order.withStatus(OrderEvent.Status.SHIPPED));

		lifecycle.forEach(event -> producer.send(topic, customerKey, event));

		try (KafkaMessageConsumer consumer = newConsumer()) {
			consumer.subscribe(topic);
			List<ConsumerRecord<String, String>> forKey = consumer
					.pollUntil(all -> all.stream().filter(r -> customerKey.equals(r.key())).count() >= lifecycle.size(),
							RECEIVE_TIMEOUT)
					.stream()
					.filter(r -> customerKey.equals(r.key()))
					.collect(Collectors.toList());

			assertEquals(forKey.size(), lifecycle.size(), "Not all events for the key were received");
			assertEquals(forKey.stream().map(ConsumerRecord::partition).distinct().count(), 1L,
					"Same key must always go to the same partition");
			assertEquals(forKey.stream().map(r -> KafkaMessageConsumer.valueAs(r, OrderEvent.class).status())
					.collect(Collectors.toList()),
					List.of(OrderEvent.Status.CREATED, OrderEvent.Status.PAID, OrderEvent.Status.SHIPPED),
					"Events must be consumed in the order they were produced");
		}
	}
}
