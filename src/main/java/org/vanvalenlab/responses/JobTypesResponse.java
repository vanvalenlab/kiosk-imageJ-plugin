package org.vanvalenlab.responses;

/**
 * Response class for /api/jobtypes
 */
public class JobTypesResponse implements KioskResponse {
	String [] jobTypes;

	public String [] getJobTypes() {
		return jobTypes;
	}
}
