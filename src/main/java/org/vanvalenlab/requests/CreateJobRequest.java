package org.vanvalenlab.requests;

/**
 * Request class for /api/predict
 */
public class CreateJobRequest implements KioskRequest {
    String jobType;
    String imageName;
    String uploadedName;
    String postprocessFunction;
    String preprocessFunction;
    String modelName;
    String modelVersion;
    String dataRescale;
    String dataLabel;

    public CreateJobRequest(String jobType, String imageName, String uploadedName) {
        this.jobType = jobType;
        this.imageName = imageName;
        this.uploadedName = uploadedName;
        this.postprocessFunction = "";
        this.preprocessFunction = "";
        this.modelName = "";
        this.modelVersion = "";
        this.dataRescale = "";
        this.dataLabel = "";
    }
}
