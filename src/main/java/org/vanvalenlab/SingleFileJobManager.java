package org.vanvalenlab;

import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.util.Map;

public class SingleFileJobManager extends KioskJobManager implements PlugIn {

    public void run(String arg) {
        try {
            // Select the image file.
            final OpenDialog od = new OpenDialog(Constants.SELECT_FILE_MESSAGE);
            final String filePath = od.getPath();
            if (filePath == null) {
                return;
            }

            // show options menu (including hostname)
            Map<String, Object> options = this.configureOptions();
            if (null == options) {
                return;
            }

            final String host = (String)options.get(Constants.KIOSK_HOST);

            final String jobType = ImageJobManager.selectJobType(host);
            if (null == jobType) {
                return;
            }

            // Run the job
            ImageJobManager.runJob(jobType, filePath, options);
        }
        catch (Exception e) {
            IJ.handleException(e);
        }
    }
}
