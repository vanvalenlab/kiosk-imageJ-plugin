package org.vanvalenlab.responses;

/**
 * Response class for /api/predict
 */
public class CreateJobResponse implements KioskResponse {

	String hash;

	public String getJobHash() {
		return this.hash;
	}

	public CreateJobResponse(String hash) {
		this.hash = hash;
	}
}
