package com.masterclass.kafka;

/**
 * Unchecked wrapper for failed, timed-out or interrupted Kafka operations, so callers
 * (and tests) don't have to handle ExecutionException / TimeoutException / InterruptedException.
 */
public class KafkaOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public KafkaOperationException(String message) {
		super(message);
	}

	public KafkaOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
