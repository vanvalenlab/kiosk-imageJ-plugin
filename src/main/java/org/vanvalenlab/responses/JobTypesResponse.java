package org.vanvalenlab.responses;

/**
 * Response class for /api/jobtypes
 */
public class JobTypesResponse implements KioskResponse {
	// The job types is stored
	// in a list.
	String [] jobTypes;
	public String [] getJobTypes() {
		return jobTypes;
	}
}
