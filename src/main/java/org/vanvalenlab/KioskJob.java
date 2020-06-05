package org.vanvalenlab;

import ij.IJ;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class KioskJob {
    private final KioskHttpClient kioskClient;
    private String jobHash;
    private final String jobType;
    private String status;
    private boolean isExpired;

    public KioskJob(String host, String jobType) {
        this.kioskClient = new KioskHttpClient(host);
        this.status = null;
        this.isExpired = false;
        this.jobHash = null;
        this.jobType = jobType;
    }

    // START: Getters
    public String getStatus() { return status; }
    public String getJobHash() { return jobHash; }
    public String getJobType() { return jobType; }
    public boolean isExpired() { return this.isExpired; }
    // END: Getters & Setters

    // START: helper methods
    /**
     * Check whether the job has a final status.
     * @return true if status is a final status, otherwise false
     */
    public boolean hasFinalStatus() {
        if (this.status == null) {
            return false;
        }
        return (this.status.equals(Constants.SUCCESS_STATUS) ||
                this.status.equals(Constants.FAILED_STATUS));
    }

    /**
     * Updates the status of the job periodically until it has a final status.
     * @return The final status found.
     * @throws IOException
     */
    public String waitForFinalStatus(int updateInterval) throws IOException {
        String previousStatus;
        String currentStatus = null;
        while (!this.hasFinalStatus()) {
            // update statuses
            this.updateStatus();
            previousStatus = currentStatus;
            currentStatus = this.getStatus();

            // log the new status
            boolean isNewStatus = false;
            if (null != currentStatus) {  // status has updated
                if (previousStatus instanceof String) {
                    isNewStatus = !currentStatus.equals(previousStatus);
                } else {
                    isNewStatus = true;
                }
            }
            if (isNewStatus) {
                double progress = Constants.JOB_STATUSES.getOrDefault(currentStatus, 0.0);
                IJ.showProgress(progress);
                IJ.showStatus(String.format("DeepCell Kiosk Job Status: %s", currentStatus));
            }
            // wait to prevent excessive requests
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException ex) {
                IJ.handleException(ex);
            }
        }
        return this.getStatus();
    }
    // END: helper methods

    // START: HTTP API wrapper methods
    /**
     * Create the DeepCell Kiosk job.
     * @param imageFilePath The path of the image to process
     * @throws IOException
     */
    public void create(String imageFilePath) throws IOException {
        String uploadPath = this.kioskClient.uploadFile(imageFilePath);
        if (null == uploadPath) {
            throw new IOException(String.format("Failed to upload %s", imageFilePath));
        }
        String imageName = (new File(imageFilePath)).getName();
        String jobHash = this.kioskClient.createJob(imageName, uploadPath, this.jobType);
        this.jobHash = jobHash;
    }

    /**
     * Expire the DeepCell Kiosk Job.
     * @param expireTime Seconds until the job expires.
     * @throws IOException
     */
    public void expire(int expireTime) throws IOException {
        // TODO: handle jobHash is null
        int value = this.kioskClient.expireRedisHash(this.jobHash, expireTime);
        if (value == 0) {
            String err = String.format("Could not find Redis hash '%s'", this.jobHash);
            throw new IOException(err);
        } else {
            this.isExpired = true;
        }
    }

    /**
     * Get and set the current status of the job.
     * @throws IOException
     */
    public void updateStatus() throws IOException {
        // TODO: handle jobHash is null
        String status = this.kioskClient.getStatus(this.jobHash);
        this.status = status;
    }

    /**
     * Get the output URL of the job results.
     * @return output URL of the job results.
     * @throws IOException
     */
    public String getOutputPath() throws IOException {
        // TODO: handle jobHash is null
        String outputPath = this.kioskClient.getRedisValue(this.jobHash, "output_url");
        return outputPath;
    }

    /**
     * Get the reason the job failed from Redis.
     * @return the reason for job failure.
     * @throws IOException
     */
    public String getErrorReason() throws IOException {
        // TODO: handle jobHash is null
        String reason = this.kioskClient.getRedisValue(this.jobHash, "reason");
        return reason;
    }
    // END: HTTP API wrapper methods
}
