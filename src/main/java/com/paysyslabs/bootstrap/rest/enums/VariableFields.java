package com.paysyslabs.bootstrap.rest.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VariableFields {
	STAN("stan"), RRN("rrn"), REV_STAN("rev_stan"), REV_RRN("rev_rrn"), CURRENT_DATE("curr_date");

	private final String key;

	private static final Map<String, VariableFields> LOOKUP = new HashMap<>();

	static {
		for (VariableFields t : VariableFields.values()) {
			LOOKUP.put(t.getKey(), t);
		}
	}

	VariableFields(String key) {
		this.key = key;
	}

	@JsonValue
	public String getKey() {
		return key;
	}

	@JsonCreator
	public static VariableFields fromKey(String key) {
		return LOOKUP.get(key);
	}
}
