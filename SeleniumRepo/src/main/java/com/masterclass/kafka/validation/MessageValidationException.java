package com.masterclass.kafka.validation;

import java.util.List;

/** Thrown when a message does not satisfy its JSON schema. */
public class MessageValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String schemaName;
	private final List<String> errors;

	public MessageValidationException(String schemaName, List<String> errors) {
		super("Message does not match schema '" + schemaName + "': " + errors);
		this.schemaName = schemaName;
		this.errors = List.copyOf(errors);
	}

	public String getSchemaName() {
		return schemaName;
	}

	public List<String> getErrors() {
		return errors;
	}
}
