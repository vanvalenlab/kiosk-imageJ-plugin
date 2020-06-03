package org.vanvalenlab.responses;

/**
 * Response class for /api/redis
 */
public class GetErrorResponse implements KioskResponse {
	String hash;
	String key;

	public String getHash() {
		return hash;
	}

	public String getKey() {
		return key;
	}

	public GetErrorResponse(String hash1, String key1) {
		this.hash = hash1;
		this.key = key1;
	}
}
