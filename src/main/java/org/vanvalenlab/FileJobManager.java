package org.vanvalenlab;

import ij.IJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        }
    }
}
