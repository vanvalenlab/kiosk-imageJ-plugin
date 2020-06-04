package org.vanvalenlab;

import ij.IJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.util.Map;

public class FileJobManager extends KioskJobManager implements PlugIn {

    public void run(String arg) {
        try {
            // Select the image file.
            final String filePath = IJ.getFilePath(Constants.SELECT_FILE_MESSAGE);
            if (filePath == null) return;

            // show options menu (including hostname)
            Map<String, Object> options = this.configureOptions();
            if (null == options) return;

            final String host = (String)options.get(Constants.KIOSK_HOST);

            final String jobType = ImageJobManager.selectJobType(host);
            if (null == jobType) return;

            // Run the job
            ImageJobManager.runJob(jobType, filePath, options);
        }
        catch (Exception e) {
            IJ.handleException(e);
            IJ.showProgress(1.0);
            IJ.showStatus("DeepCell Kiosk Job Failed.");
        }
    }
}
