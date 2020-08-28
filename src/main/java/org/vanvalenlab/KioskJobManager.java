package org.vanvalenlab;

import ij.io.FileInfo;
import org.vanvalenlab.exceptions.KioskJobFailedException;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.Opener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * So far, Kiosk_ImageJ_Plugin lets you select a file type,
 * and it zips directories if selected.
 * // TODO:
 * // Next release
 * // a few jobs at once, async http request, keep sending the request
 * // stretch goal, select multiple file
 * // take at least one file, do multiple jobs
 *
 */
public class KioskJobManager {

    /** 
     * selectJobType helps you can pick from the available job types. 
     * A GenericDialog radiobutton aray will prompt.
     * @param host The DeepCell Kiosk host.
     * @return Selected jobType.
     */
    public static String selectJobType(String host) throws IOException {
        final String[] jobTypes;
        jobTypes = (new KioskHttpClient(host)).getJobTypes();
        if (null == jobTypes) {
            String err = "No Job Types found. Check the DeepCell Kiosk status.";
            throw new IOException(err);
        }

        String jobType = null;
        final GenericDialog gd = new GenericDialog(Constants.JOB_SELECT_MENU_TITLE);

        // radio button group for job selection
        gd.addRadioButtonGroup(
                Constants.SELECT_JOB_TITLE,
                jobTypes,
                jobTypes.length,
                1,
                jobTypes[0]);

        System.out.println("About to show dialog");
        gd.showDialog();
        System.out.println("Dialog shown");
        if (!gd.wasCanceled()) {
            jobType = gd.getNextRadioButton();
        }
        return jobType;
    }

    /**
     * Allow users to override the default options.
     * @return a LinkedHashMap of option keys and values.
     */
    public static Map<String, Object> configureOptions() {
        final Map<String, Object> options = new LinkedHashMap<String, Object>();
        final GenericDialog gd = new GenericDialog(Constants.OPTIONS_MENU_TITLE);

        // kiosk hostname
        gd.addStringField(
                Constants.KIOSK_HOST,
                (String)Constants.getDefault(Constants.KIOSK_HOST),
                20);

        // job status update interval
        gd.addNumericField(
                Constants.UPDATE_STATUS_MILLISECONDS,
                (int)Constants.getDefault(Constants.UPDATE_STATUS_MILLISECONDS),
                0);

        // job expiration time
        gd.addNumericField(
                Constants.EXPIRE_TIME_SECONDS,
                (int)Constants.getDefault(Constants.EXPIRE_TIME_SECONDS),
                0);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return null;
        }
        options.put(Constants.KIOSK_HOST, gd.getNextString());
        options.put(Constants.UPDATE_STATUS_MILLISECONDS, (int)gd.getNextNumber());
        options.put(Constants.EXPIRE_TIME_SECONDS, (int)gd.getNextNumber());

        return options;
    }

    /**
     * Get the file path of the image. Saves a temporary file if necessary.
     * @param imp An IJ ImagePlus object that may need to be saved.
     * @return The full file path of the image, as a String.
     */
    public static String getFilePath(ImagePlus imp) throws IOException {
        String filePath;
        FileInfo fileInfo = imp.getOriginalFileInfo();

        if (null == fileInfo) {
            Path tmpDir = Files.createTempDirectory("DeepCell_Kiosk");
            // image is in memory. save file as temporary tiff file.
            filePath = Paths.get(tmpDir.toString(), imp.getTitle()).toString();
            boolean success = IJ.saveAsTiff(imp, filePath);
            if (!success) {
                throw new IOException("Could not save active image as tiff file for upload.");
            }
        } else {
            filePath = String.format("%s%s", fileInfo.directory, fileInfo.fileName);
        }
        return filePath;
    }

    /**
     * Create a job and run it
     * @param jobType The type of job to create.
     * @param file The file to upload for the job.
     * @param options Map of all configuration options.
     */
    public static void runJob(
            String jobType,
            String file,
            Map<String, Object> options) throws IOException, KioskJobFailedException {
        final String host = (String)options.get(Constants.KIOSK_HOST);
        final int updateInterval = (int)options.get(Constants.UPDATE_STATUS_MILLISECONDS);
        final int expireTime = (int)options.get(Constants.EXPIRE_TIME_SECONDS);

        KioskJob job = new KioskJob(host, jobType);
        job.create(file);  // Upload file and start job.

        // Periodically check the job status until it is "done" or "failed".
        String finalStatus = job.waitForFinalStatus(updateInterval);

        // expire the job
        job.expire(expireTime);

        // Job is finished! Get error if failed, otherwise get file URL.
        if (finalStatus.equals(Constants.FAILED_STATUS)) {
            String error = job.getErrorReason();
            throw new KioskJobFailedException(error);
        }
        else if (finalStatus.equals(Constants.SUCCESS_STATUS)) {
            String outputPath = job.getOutputPath();
            Opener opener = new Opener();
            ImagePlus outputImage = opener.openURL(outputPath);
            outputImage.show(Constants.SUCCESS_MESSAGE);
        }
        else throw new RuntimeException("Unknown final status: " + finalStatus);
    }
}
