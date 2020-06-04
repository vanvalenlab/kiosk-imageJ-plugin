package org.vanvalenlab.responses;

/**
 * Response class for /api/redis/status
 */
public class GetStatusResponse implements KioskResponse {
	String status;

	public GetStatusResponse(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
