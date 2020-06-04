package org.vanvalenlab;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.plugin.PlugIn;

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

            FileInfo fileInfo = imp.getOriginalFileInfo();
            String filePath = String.format("%s%s", fileInfo.directory, fileInfo.fileName);

            // show options menu (including hostname)
            Map<String, Object> options = this.configureOptions();
            if (null == options) {
                return;
            }

            final String host = (String) options.get(Constants.KIOSK_HOST);

            // select job type
            final String jobType = ImageJobManager.selectJobType(host);
            if (null == jobType) {
                return;
            }

            // Run the job
            ImageJobManager.runJob(jobType, filePath, options);
        } catch (Exception e) {
            IJ.handleException(e);
        }
    }
}
