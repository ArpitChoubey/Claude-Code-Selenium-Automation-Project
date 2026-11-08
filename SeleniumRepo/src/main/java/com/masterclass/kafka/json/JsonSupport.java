package com.masterclass.kafka.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * One shared, thread-safe Jackson ObjectMapper with consistent settings for every Kafka message.
 */
public final class JsonSupport {

	private static final ObjectMapper MAPPER = JsonMapper.builder()
			.addModule(new JavaTimeModule())
			// Instants as ISO-8601 strings ("2026-11-08T10:15:30Z"), not numbers
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			// Tolerant reader: new fields added by producers don't break existing consumers
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			// "{...} garbage" is malformed, not a valid object
			.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
			.build();

	private JsonSupport() {
	}

	public static ObjectMapper mapper() {
		return MAPPER;
	}

	public static String toJson(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Cannot serialise " + value.getClass().getName() + " to JSON", e);
		}
	}

	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Cannot deserialise JSON to " + type.getName() + ": " + e.getOriginalMessage(), e);
		}
	}
}
