package org.vanvalenlab;

import ij.IJ;
import ij.plugin.PlugIn;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileManager extends KioskJobManager implements PlugIn {

    /**
     * selectDirectory displays a file explorer, which
     * will let you select a specific
     * directory which hopefully contains the batch of
     * images needed to process.
     *  @return the absolute path of the selected file or null if
     *  a file is not selected
     */
    public static String selectDirectory() {
        final List<String> extensions = Arrays.asList("jpg", "png", "gif", ".tif", ".tiff");
        JFileChooser picker = new JFileChooser();
        picker.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        // Open the file explorer at user.home
        picker.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = picker.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            // If a directory is selected, we want to return its path.
            File selectedFile = picker.getSelectedFile();
            IJ.log(String.format("Selected file: %s", selectedFile.getAbsolutePath()));
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    /**
     *  zipFile recursively zips files, through nested directories.
     *  @param file - a file, fileName - a string of the name of the file, an outputstream
     *  to zip
     *  @return None
     */
    private static void zipFile(
            File file,
            String fileName,
            ZipOutputStream zipped) throws IOException {

        if (file.isDirectory()) {
            zipped.putNextEntry(new ZipEntry(fileName + (fileName.endsWith("/") ? "" : "/")));
            zipped.closeEntry();
            for (File child : file.listFiles()) {
                zipFile(child, String.format("%s/%s", fileName, child.getName()), zipped);
            }
            return;
        }
        FileInputStream input = new FileInputStream(file);
        zipped.putNextEntry(new ZipEntry(fileName));
        byte[] bytes = new byte[1024];
        int length;
        while ((length = input.read(bytes)) >= 0) {
            zipped.write(bytes, 0, length);
        }
        input.close();
    }

    /**
     * 1. Let the person pick if they want images
     * 2. or files only which will be zipped.
     * 3. Upload the image.
     * 4. Queue the job with a hash using Predict.
     * 5. Query job status.
     * 6. Update or retrieve expiration status.
     */
    public void run(String arg) {
        String file = ZipFileManager.selectDirectory();

        // Try to zip the file.
        try {
            int index = file.lastIndexOf("\\");
            if (index != -1) {
                FileOutputStream fos = new FileOutputStream(file + ".zip");
                ZipOutputStream zipped = new ZipOutputStream(fos);
                File needsZipping = new File(file);
                zipFile(needsZipping, needsZipping.getName(), zipped);
                zipped.close();
                fos.close();
                IJ.log(needsZipping.getName());
                file = file + ".zip";
            }
        }
        catch(IOException ex) {
            IJ.log(String.format("Unable to zip due to %s!", ex));
            IJ.handleException(ex);
        }

        if (file == null) {
            IJ.showMessage("Error!", "Something went wrong");
        }
        else {
            // show options menu (including hostname)
            Map<String, Object> options = this.configureOptions();
            if (null == options) {
                return;
            }

            final String host = (String)options.get(Constants.KIOSK_HOST);

            // User chooses job type
            String jobType = ImageJobManager.selectJobType(host);

            // Run the job
            ImageJobManager.runJob(jobType, file, options);
        }
    }
}
