package org.vanvalenlab;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.plugin.PlugIn;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ImageJobManager extends KioskJobManager implements PlugIn {

    public void run(String arg) {
        try {
            // get active image path
            final ImagePlus imp = IJ.getImage();
            if (null == imp) {
                IJ.noImage();
                return;
            }

            String filePath;
            FileInfo fileInfo = imp.getOriginalFileInfo();

            if (null == fileInfo) {
                Path tmpDir = Files.createTempDirectory("DeepCell_Kiosk");
                // image is in memory. save file as temporary tiff file.

                filePath = Paths.get(tmpDir.toString(), imp.getTitle()).toString();
                boolean success = IJ.saveAsTiff(imp, filePath);
                if (!success) {
                    IJ.showMessage("Could not save active image as tiff file for upload.");
                    return;
                }
            } else {
                filePath = String.format("%s%s", fileInfo.directory, fileInfo.fileName);
            }
            // show options menu (including hostname)
            Map<String, Object> options = this.configureOptions();
            if (null == options) return;

            final String host = (String) options.get(Constants.KIOSK_HOST);

            // select job type
            final String jobType = ImageJobManager.selectJobType(host);
            if (null == jobType) return;

            // Run the job
            ImageJobManager.runJob(jobType, filePath, options);
        } catch (Exception e) {
            IJ.showStatus(Constants.FAIL_MESSAGE);
            IJ.showProgress(1.0);
            IJ.handleException(e);
        }
    }
}
