package actualPlugin;

// Class storing the response
// from uploading the file.
public class Response {
	// The URL and uploaded
	// name of the file
	// is contained here
	// from the response of the 
	// API request.
	String imageURL;
	String uploadedName;
	
	// Getters should retrieve
	// the imageURL or uploadedName.
	String getImageURL() {
		return imageURL;
	}
	
	String getuploadedName() {
		return uploadedName;
	}
}
