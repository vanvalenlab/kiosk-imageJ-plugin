package org.vanvalenlab;

import ij.IJ;
import ij.plugin.PlugIn;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import java.io.File;
import java.util.Map;

public class FileJobManager extends KioskJobManager implements PlugIn {

    public void run(String arg) {
        try {
            // Select the image file.
            final String filePath = IJ.getFilePath(Constants.SELECT_FILE_MESSAGE);
            if (filePath == null) return;

            // TODO: support uploading directories and zip files.
            // First version of zip file support: https://tinyurl.com/ycxlp29r
            String mimeType = new Tika().detect(new File(filePath));
            if (!mimeType.substring(0, 5).equalsIgnoreCase("image")) {
                final String ext = FilenameUtils.getExtension(filePath);
                IJ.showMessage(String.format("Only image files are valid, got %s file.", ext));
                return;
            }

            // show options menu (including hostname)
            Map<String, Object> options = this.configureOptions();
            if (null == options) return;

            final String host = (String)options.get(Constants.KIOSK_HOST);

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
