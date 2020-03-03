package actualPlugin;

// ImageInfo is used to send in image
// information for the predict API
// requests, in order to load in the
// job.
public class ImageInfo {
	String jobType;
	String imageName;
	public ImageInfo(String j, String i) {
		jobType = j;
		imageName = i;
	}
}
