package com.masterclass.kafka.consumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;

import com.masterclass.kafka.config.KafkaConfig;
import com.masterclass.kafka.json.JsonSupport;

/**
 * Reads JSON messages from Kafka with "wait until" helpers that suit automated tests.
 *
 * <p>Offsets are committed manually after each batch has been collected. Like Kafka's own
 * consumer, an instance is <b>not</b> thread-safe: use one per thread / test and close it.
 */
public class KafkaMessageConsumer implements AutoCloseable {

	private static final Duration POLL_INTERVAL = Duration.ofMillis(250);

	private final Consumer<String, String> consumer;

	public KafkaMessageConsumer(KafkaConfig config, String groupId) {
		this(new KafkaConsumer<>(config.consumerProperties(groupId)));
	}

	/** For injecting any {@link Consumer}, e.g. Kafka's MockConsumer in unit tests. */
	public KafkaMessageConsumer(Consumer<String, String> consumer) {
		this.consumer = Objects.requireNonNull(consumer, "consumer");
	}

	public void subscribe(String... topics) {
		subscribe(List.of(topics));
	}

	public void subscribe(Collection<String> topics) {
		consumer.subscribe(topics);
	}

	/**
	 * Polls until {@code condition} is true for everything received so far, or {@code timeout}
	 * passes. Returns all records received either way, so callers assert on the result.
	 */
	public List<ConsumerRecord<String, String>> pollUntil(Predicate<List<ConsumerRecord<String, String>>> condition,
			Duration timeout) {
		List<ConsumerRecord<String, String>> received = new ArrayList<>();
		long deadline = System.nanoTime() + timeout.toNanos();
		while (System.nanoTime() < deadline) {
			ConsumerRecords<String, String> batch = consumer.poll(POLL_INTERVAL);
			if (!batch.isEmpty()) {
				batch.forEach(received::add);
				consumer.commitSync();
			}
			if (condition.test(received)) {
				break;
			}
		}
		return received;
	}

	/** Polls until at least {@code expectedCount} records arrived, or the timeout passes. */
	public List<ConsumerRecord<String, String>> pollForCount(int expectedCount, Duration timeout) {
		return pollUntil(records -> records.size() >= expectedCount, timeout);
	}

	/** Polls until a record with {@code key} arrives; empty if none arrived within the timeout. */
	public Optional<ConsumerRecord<String, String>> pollForKey(String key, Duration timeout) {
		Predicate<ConsumerRecord<String, String>> hasKey = r -> Objects.equals(r.key(), key);
		return pollUntil(records -> records.stream().anyMatch(hasKey), timeout)
				.stream()
				.filter(hasKey)
				.findFirst();
	}

	/** Deserialises a record's JSON value into {@code type}. */
	public static <T> T valueAs(ConsumerRecord<String, String> record, Class<T> type) {
		return JsonSupport.fromJson(record.value(), type);
	}

	/** Returns the last header with this name as a UTF-8 string. */
	public static Optional<String> header(ConsumerRecord<String, String> record, String name) {
		Header header = record.headers().lastHeader(name);
		return header == null ? Optional.empty() : Optional.of(new String(header.value(), StandardCharsets.UTF_8));
	}

	@Override
	public void close() {
		consumer.close(Duration.ofSeconds(10));
	}
}
