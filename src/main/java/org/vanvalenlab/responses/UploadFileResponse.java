package org.vanvalenlab.responses;

/**
 * Response class for /api/upload
 */
public class UploadFileResponse implements KioskResponse {
	String imageURL;
	String uploadedName;

//	public String getImageURL() {
//		return imageURL;
//	}

	public String getUploadedName() {
		return uploadedName;
	}
}
