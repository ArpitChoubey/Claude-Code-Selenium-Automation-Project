package com.masterclass.kafka.support;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.masterclass.kafka.model.OrderEvent;

/** Builders for valid test messages. Each call returns an event with a new, unique orderId. */
public final class TestEvents {

	public static final String ORDER_SCHEMA = "schemas/order-event.schema.json";

	private static final AtomicInteger SEQUENCE = new AtomicInteger(1000);

	private TestEvents() {
	}

	public static OrderEvent validOrder() {
		return validOrder("CUST-" + UUID.randomUUID().toString().substring(0, 8));
	}

	public static OrderEvent validOrder(String customerId) {
		return OrderEvent.created("ORD-" + SEQUENCE.incrementAndGet(), customerId, new BigDecimal("49.99"), "INR");
	}
}
