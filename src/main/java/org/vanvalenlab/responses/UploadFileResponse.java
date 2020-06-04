package org.vanvalenlab.responses;

/**
 * Response class for /api/upload
 */
public class UploadFileResponse implements KioskResponse {
	String imageURL;
	String uploadedName;

	public UploadFileResponse(String uploadedName, String imageURL) {
		this.uploadedName = uploadedName;
		this.imageURL = imageURL;
	}

//	public String getImageURL() {
//		return imageURL;
//	}

	public String getUploadedName() {
		return uploadedName;
	}
}
