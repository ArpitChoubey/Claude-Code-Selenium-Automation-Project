package com.masterclass.kafka.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Example domain event sent through Kafka. Its JSON contract is
 * src/main/resources/schemas/order-event.schema.json; keep the two in sync.
 */
public record OrderEvent(
		String orderId,
		String customerId,
		BigDecimal amount,
		String currency,
		Status status,
		Instant createdAt) {

	public enum Status {
		CREATED, PAID, SHIPPED, CANCELLED
	}

	public static OrderEvent created(String orderId, String customerId, BigDecimal amount, String currency) {
		// millisecond precision keeps timestamps identical after a JSON round trip
		return new OrderEvent(orderId, customerId, amount, currency, Status.CREATED,
				Instant.now().truncatedTo(ChronoUnit.MILLIS));
	}

	public OrderEvent withStatus(Status newStatus) {
		return new OrderEvent(orderId, customerId, amount, currency, newStatus, createdAt);
	}
}
