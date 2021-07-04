package com.serverless;

import java.util.Map;

/**
 * REST response wrapper.
 * 
 * @author kevinhooke
 *
 */
public class Response {

	private final String message;
	private final Map<String, Object> data;

	public Response(String message, Map<String, Object> input) {
		this.message = message;
		this.data = input;
	}

	public String getMessage() {
		return this.message;
	}

	public Map<String, Object> getData() {
		return this.data;
	}
}
