package com.masterclass.kafka.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.masterclass.kafka.json.JsonSupport;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 * Validates JSON messages against a JSON Schema (draft-07) loaded from the classpath.
 *
 * <p>Create one instance per schema and reuse it: the schema is parsed once and instances are
 * safe to share between threads.
 *
 * <pre>
 * JsonSchemaValidator validator = JsonSchemaValidator.fromClasspath("schemas/order-event.schema.json");
 * ValidationResult result = validator.validate(json);
 * </pre>
 */
public final class JsonSchemaValidator {

	private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

	private final String schemaName;
	private final JsonSchema schema;

	private JsonSchemaValidator(String schemaName, JsonSchema schema) {
		this.schemaName = schemaName;
		this.schema = schema;
	}

	/** Loads a schema such as {@code "schemas/order-event.schema.json"} from src/main/resources. */
	public static JsonSchemaValidator fromClasspath(String resourcePath) {
		try (InputStream in = JsonSchemaValidator.class.getClassLoader().getResourceAsStream(resourcePath)) {
			if (in == null) {
				throw new IllegalArgumentException("JSON schema not found on classpath: " + resourcePath);
			}
			return new JsonSchemaValidator(resourcePath, FACTORY.getSchema(in));
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot read JSON schema " + resourcePath, e);
		}
	}

	public String getSchemaName() {
		return schemaName;
	}

	/** Never throws for bad input: malformed or empty JSON is reported as a validation error. */
	public ValidationResult validate(String json) {
		if (json == null || json.isBlank()) {
			return ValidationResult.failure(List.of("Message is empty"));
		}

		JsonNode node;
		try {
			node = JsonSupport.mapper().readTree(json);
		} catch (JsonProcessingException e) {
			return ValidationResult.failure(List.of("Malformed JSON: " + e.getOriginalMessage()));
		}

		Set<ValidationMessage> messages = schema.validate(node);
		if (messages.isEmpty()) {
			return ValidationResult.success();
		}
		List<String> errors = messages.stream()
				.map(ValidationMessage::getMessage)
				.sorted()
				.collect(Collectors.toList());
		return ValidationResult.failure(errors);
	}

	/** Like {@link #validate(String)} but throws {@link MessageValidationException} when invalid. */
	public void validateOrThrow(String json) {
		ValidationResult result = validate(json);
		if (!result.valid()) {
			throw new MessageValidationException(schemaName, result.errors());
		}
	}
}
