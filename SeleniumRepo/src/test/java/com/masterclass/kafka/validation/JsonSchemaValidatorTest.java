package com.masterclass.kafka.validation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.util.function.Consumer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.masterclass.kafka.json.JsonSupport;
import com.masterclass.kafka.support.TestEvents;

/** Unit tests for JSON schema validation. No Kafka broker needed. */
public class JsonSchemaValidatorTest {

	private final JsonSchemaValidator validator = JsonSchemaValidator.fromClasspath(TestEvents.ORDER_SCHEMA);

	@Test
	public void validOrderEventPassesValidation() {
		String json = JsonSupport.toJson(TestEvents.validOrder());

		ValidationResult result = validator.validate(json);

		assertTrue(result.valid(), "Unexpected errors: " + result.errors());
		assertTrue(result.errors().isEmpty());
	}

	@DataProvider(name = "invalidOrders")
	public Object[][] invalidOrders() {
		return new Object[][] {
				{ "missing required customerId", mutate(o -> o.remove("customerId")) },
				{ "amount is zero", mutate(o -> o.put("amount", 0)) },
				{ "amount is negative", mutate(o -> o.put("amount", -5.50)) },
				{ "amount is a string", mutate(o -> o.put("amount", "49.99")) },
				{ "lowercase currency", mutate(o -> o.put("currency", "inr")) },
				{ "unknown status", mutate(o -> o.put("status", "LOST")) },
				{ "orderId wrong format", mutate(o -> o.put("orderId", "12345")) },
				{ "createdAt not a date-time", mutate(o -> o.put("createdAt", "yesterday")) },
				{ "unexpected extra field", mutate(o -> o.put("discountCode", "SALE10")) },
				{ "not an object", "[1, 2, 3]" },
		};
	}

	@Test(dataProvider = "invalidOrders")
	public void invalidOrderEventFailsValidation(String description, String json) {
		ValidationResult result = validator.validate(json);

		assertFalse(result.valid(), "Expected invalid: " + description);
		assertFalse(result.errors().isEmpty(), "Expected at least one error for: " + description);
	}

	@Test
	public void malformedJsonIsReportedAsErrorNotThrown() {
		ValidationResult result = validator.validate("{\"orderId\": ");

		assertFalse(result.valid());
		assertTrue(result.errors().get(0).startsWith("Malformed JSON"), result.errors().toString());
	}

	@Test
	public void trailingGarbageIsMalformed() {
		String json = JsonSupport.toJson(TestEvents.validOrder()) + " garbage";

		assertFalse(validator.validate(json).valid());
	}

	@Test
	public void emptyMessageIsInvalid() {
		assertEquals(validator.validate("  ").errors(), java.util.List.of("Message is empty"));
		assertFalse(validator.validate(null).valid());
	}

	@Test
	public void validateOrThrowRaisesExceptionWithErrors() {
		MessageValidationException ex = expectThrows(MessageValidationException.class,
				() -> validator.validateOrThrow(mutate(o -> o.remove("orderId"))));

		assertEquals(ex.getSchemaName(), TestEvents.ORDER_SCHEMA);
		assertFalse(ex.getErrors().isEmpty());
	}

	@Test
	public void missingSchemaFailsFast() {
		expectThrows(IllegalArgumentException.class, () -> JsonSchemaValidator.fromClasspath("schemas/does-not-exist.json"));
	}

	/** Takes a valid order, applies one change, returns the JSON. */
	private static String mutate(Consumer<ObjectNode> change) {
		ObjectNode node = JsonSupport.mapper().valueToTree(TestEvents.validOrder());
		change.accept(node);
		return node.toString();
	}
}
