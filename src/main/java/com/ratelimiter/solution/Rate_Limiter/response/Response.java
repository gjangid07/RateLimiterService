package com.ratelimiter.solution.Rate_Limiter.response;

/**
 * @author gjangid
 *
 */
// This class is used to retrieve the response created after processing of Rate
// Limiting of an input request.
public class Response {

	private String message;
	private int timelyLimit;

	public Response() {

	}

	public Response(String message, int timelyLimit) {
		super();
		this.message = message;
		this.timelyLimit = timelyLimit;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTimelyLimit() {
		return timelyLimit;
	}

	public void setTimelyLimit(int timelyLimit) {
		this.timelyLimit = timelyLimit;
	}

}
