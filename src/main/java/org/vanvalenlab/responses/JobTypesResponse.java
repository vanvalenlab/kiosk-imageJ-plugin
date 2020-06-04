package org.vanvalenlab.responses;

/**
 * Response class for /api/jobtypes
 */
public class JobTypesResponse implements KioskResponse {
	String [] jobTypes;

	public JobTypesResponse(String[] jobTypes) {
		this.jobTypes = jobTypes;
	}

	public String [] getJobTypes() {
		return jobTypes;
	}
}
