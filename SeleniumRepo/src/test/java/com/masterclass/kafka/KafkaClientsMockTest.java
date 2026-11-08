package com.masterclass.kafka;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.Test;

import com.masterclass.kafka.consumer.KafkaMessageConsumer;
import com.masterclass.kafka.json.JsonSupport;
import com.masterclass.kafka.model.OrderEvent;
import com.masterclass.kafka.producer.KafkaMessageProducer;
import com.masterclass.kafka.support.TestEvents;
import com.masterclass.kafka.validation.JsonSchemaValidator;
import com.masterclass.kafka.validation.MessageValidationException;

/**
 * Unit tests for the producer and consumer wrappers using Kafka's MockProducer / MockConsumer.
 * No Kafka broker needed.
 */
public class KafkaClientsMockTest {

	private static final String TOPIC = "orders";
	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	private final JsonSchemaValidator validator = JsonSchemaValidator.fromClasspath(TestEvents.ORDER_SCHEMA);

	@Test
	public void producerSerialisesPayloadAndAddsHeaders() {
		MockProducer<String, String> mock = newMockProducer();
		OrderEvent event = TestEvents.validOrder();

		try (KafkaMessageProducer producer = new KafkaMessageProducer(mock, validator, TIMEOUT)) {
			producer.send(TOPIC, event.orderId(), event, Map.of("source", "unit-test"));
		}

		assertEquals(mock.history().size(), 1);
		ProducerRecord<String, String> sent = mock.history().get(0);
		assertEquals(sent.topic(), TOPIC);
		assertEquals(sent.key(), event.orderId());
		assertEquals(JsonSupport.fromJson(sent.value(), OrderEvent.class), event);
		assertEquals(headerValue(sent, KafkaMessageProducer.CONTENT_TYPE_HEADER), KafkaMessageProducer.JSON_CONTENT_TYPE);
		assertEquals(headerValue(sent, "source"), "unit-test");
	}

	@Test
	public void producerRejectsInvalidPayloadBeforeSending() {
		MockProducer<String, String> mock = newMockProducer();

		try (KafkaMessageProducer producer = new KafkaMessageProducer(mock, validator, TIMEOUT)) {
			expectThrows(MessageValidationException.class,
					() -> producer.send(TOPIC, "key-1", "{\"orderId\":\"not-valid\"}"));
		}

		assertTrue(mock.history().isEmpty(), "Invalid message must not be sent");
	}

	@Test
	public void producerWithoutValidatorSendsAnyJson() {
		MockProducer<String, String> mock = newMockProducer();

		try (KafkaMessageProducer producer = new KafkaMessageProducer(mock, null, TIMEOUT)) {
			producer.send(TOPIC, "key-1", "{\"anything\":true}");
		}

		assertEquals(mock.history().get(0).value(), "{\"anything\":true}");
	}

	@Test
	public void producerWrapsBrokerErrors() {
		MockProducer<String, String> mock = new MockProducer<>(false, new StringSerializer(), new StringSerializer());

		try (KafkaMessageProducer producer = new KafkaMessageProducer(mock, null, TIMEOUT)) {
			var future = producer.sendAsync(TOPIC, "key-1", "{}", Map.of());
			mock.errorNext(new RuntimeException("broker down"));

			assertTrue(future.isCompletedExceptionally());
		}
	}

	@Test
	public void consumerCollectsRecordsAndFindsByKey() {
		MockConsumer<String, String> mock = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
		TopicPartition partition = new TopicPartition(TOPIC, 0);
		OrderEvent first = TestEvents.validOrder();
		OrderEvent second = TestEvents.validOrder();

		try (KafkaMessageConsumer consumer = new KafkaMessageConsumer(mock)) {
			consumer.subscribe(TOPIC);
			mock.rebalance(List.of(partition));
			mock.updateBeginningOffsets(Map.of(partition, 0L));
			mock.addRecord(new ConsumerRecord<>(TOPIC, 0, 0L, first.orderId(), JsonSupport.toJson(first)));
			mock.addRecord(new ConsumerRecord<>(TOPIC, 0, 1L, second.orderId(), JsonSupport.toJson(second)));

			List<ConsumerRecord<String, String>> records = consumer.pollForCount(2, TIMEOUT);

			assertEquals(records.stream().map(ConsumerRecord::key).collect(Collectors.toList()),
					List.of(first.orderId(), second.orderId()));
			assertEquals(KafkaMessageConsumer.valueAs(records.get(1), OrderEvent.class), second);
		}
	}

	@Test
	public void consumerReturnsEmptyWhenKeyNeverArrives() {
		MockConsumer<String, String> mock = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
		TopicPartition partition = new TopicPartition(TOPIC, 0);

		try (KafkaMessageConsumer consumer = new KafkaMessageConsumer(mock)) {
			consumer.subscribe(TOPIC);
			mock.rebalance(List.of(partition));
			mock.updateBeginningOffsets(Map.of(partition, 0L));

			assertTrue(consumer.pollForKey("missing-key", Duration.ofMillis(800)).isEmpty());
		}
	}

	private static MockProducer<String, String> newMockProducer() {
		return new MockProducer<>(true, new StringSerializer(), new StringSerializer());
	}

	private static String headerValue(ProducerRecord<String, String> record, String name) {
		return new String(record.headers().lastHeader(name).value(), StandardCharsets.UTF_8);
	}
}
