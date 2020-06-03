package org.vanvalenlab;

import org.vanvalenlab.requests.*;
import org.vanvalenlab.responses.*;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class KioskHttpClient {

    private final OkHttpClient client;
    private final Gson g;
    private final String host;

    /**
     * KioskHttpClient constructor.
     * @param host The DeepCell Kiosk host.
     */
    public KioskHttpClient(String host) {
        this.client = new OkHttpClient();
        this.g = new Gson();
        this.host = host;
    }

    /**
     * Sends a pre-built okhttp.Request and returns the response body as a String.
     * @param request okhttp.Request to send.
     * @return Response body of request as a String.
     * @throws IOException
     */
    String sendHttpRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(String.format("Unexpected code %s", response));
            }
            String jsonData = response.body().string();
            return jsonData;
        }
    }

    /**
     * Get the value of the field of the given Redis hash.
     * @param redisHash Redis hash which has values.
     * @param redisKey Key of the redis hash to get the value.
     * @return Value of the Redis hash's field.
     * @throws IOException
     */
    public String getRedisValue(String redisHash, String redisKey) throws IOException {
        String value;
        KioskRequest reqBody = new GetRedisValueRequest(redisHash, redisKey);
        String body = this.g.toJson(reqBody);
        Request request = new Request.Builder()
                .url(String.format("%s/api/redis/", this.host))
                .post(RequestBody.create(body, Constants.MEDIA_TYPE_JSON))
                .build();

        String response = this.sendHttpRequest(request);
        GetRedisValueResponse redisValueResponse = this.g.fromJson(response, GetRedisValueResponse.class);
        value = redisValueResponse.getValue();
        return value;
    }

    /**
     * Upload a local file to the DeepCell Kiosk.
     * @param filePath the file to upload.
     * @return Path of uploaded file.
     * @throws IOException
     */
    public String uploadFile(String filePath) throws IOException {
        String uploadedPath;
        File file = new File(filePath);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, Constants.MEDIA_TYPE_FORM_DATA))
                .build();

        Request request = new Request.Builder()
                .url(String.format("%s/api/upload/", this.host))
                .post(requestBody)
                .build();

        String response = this.sendHttpRequest(request);
        UploadFileResponse uploadFileResponse = this.g.fromJson(response, UploadFileResponse.class);
        uploadedPath = uploadFileResponse.getUploadedName();
        return uploadedPath;
    }

    /**
     * Expire the Redis hash in a given number of seconds.
     * @param redisHash Hash to expire.
     * @param expireTime Hash expires in this many seconds.
     * @return Value of the Redis hash's field.
     * @throws IOException
     */
    public int expireRedisHash(String redisHash, int expireTime) throws IOException {
        int responseVal;
        KioskRequest reqBody = new ExpireRequest(redisHash, expireTime);
        String jsonBody = this.g.toJson(reqBody);
        Request request = new Request.Builder()
                .url(String.format("%s/api/redis/expire/", this.host))
                .post(RequestBody.create(jsonBody, Constants.MEDIA_TYPE_JSON))
                .build();

        String response = this.sendHttpRequest(request);
        ExpireResponse expireResponse = this.g.fromJson(response, ExpireResponse.class);
        responseVal = expireResponse.getValue();
        return responseVal;
    }

    /**
     * Get all supported job types from the DeepCell Kiosk.
     * @return Array of supported job types.
     * @throws IOException
     */
    public String[] getJobTypes() throws IOException {
        String[] jobTypes = null;
        Request request = new Request.Builder()
                .url(String.format("%s/api/jobtypes/", this.host))
                .build();

        String response = this.sendHttpRequest(request);
        jobTypes = this.g.fromJson(response, JobTypesResponse.class).getJobTypes();
        return jobTypes;
    }

    /**
     * Get the current status of a Job.
     * @param redisHash the Redis hash of the job.
     * @return Status of the job.
     * @throws IOException
     */
    public String getStatus(String redisHash) throws IOException {
        String status;
        KioskRequest reqBody = new GetStatusRequest(redisHash);
        String jsonBody = this.g.toJson(reqBody);
        Request request = new Request.Builder()
                .url(String.format("%s/api/status/", this.host))
                .post(RequestBody.create(jsonBody, Constants.MEDIA_TYPE_JSON))
                .build();

        String response = this.sendHttpRequest(request);
        status = this.g.fromJson(response, GetStatusResponse.class).getStatus();
        return status;
    }

    /**
     * Create a job in the DeepCell Kiosk.
     * @param imageName the Redis hash of the job.
     * @param uploadedName The uploaded image URL.
     * @param jobType the type of job to create.
     * @return The Redis hash of the job.
     * @throws IOException
     */
    public String createJob(String imageName, String uploadedName, String jobType) throws IOException {
        String jobHash;
        KioskRequest reqBody = new CreateJobRequest(jobType, imageName, uploadedName);
        String jsonBody = this.g.toJson(reqBody);
        Request request = new Request.Builder()
                .url(String.format("%s/api/predict/", this.host))
                .post(RequestBody.create(jsonBody, Constants.MEDIA_TYPE_JSON))
                .build();

        String response = this.sendHttpRequest(request);
        jobHash = this.g.fromJson(response, CreateJobResponse.class).getJobHash();
        return jobHash;
    }
}
