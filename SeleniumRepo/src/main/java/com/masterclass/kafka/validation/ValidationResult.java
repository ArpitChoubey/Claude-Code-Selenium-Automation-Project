package com.masterclass.kafka.validation;

import java.util.List;

/** Outcome of validating one JSON message: {@code valid} plus a readable error per violation. */
public record ValidationResult(boolean valid, List<String> errors) {

	public ValidationResult {
		errors = List.copyOf(errors);
	}

	public static ValidationResult success() {
		return new ValidationResult(true, List.of());
	}

	public static ValidationResult failure(List<String> errors) {
		return new ValidationResult(false, errors);
	}
}
