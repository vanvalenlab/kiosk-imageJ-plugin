package org.vanvalenlab;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class LabelROIConverter implements PlugIn {
    /**
     * Convert a traced outline from the ImageJ Wand into a PolygonRoi.
     * @param wand
     * @return
     */
    private static PolygonRoi wandToRoi(Wand wand) {
        PolygonRoi roi;

        // The Wand can have far too many points (1000, when fewer are needed)
        // so used trimmed arrays where this is the case
        if (wand.xpoints.length > wand.npoints * 1.25)
            roi = new PolygonRoi(
                    Arrays.copyOf(wand.xpoints, wand.npoints),
                    Arrays.copyOf(wand.ypoints, wand.npoints),
                    wand.npoints,
                    Roi.TRACED_ROI
            );
        else
            roi = new PolygonRoi(wand.xpoints, wand.ypoints, wand.npoints, Roi.TRACED_ROI);

        return roi;
    }

    public void run(String arg) {
        try {
            // get active image
            final ImagePlus imp = IJ.getImage();
            if (null == imp) {
                IJ.noImage();
                return;
            }

            final int wandMode = Wand.EIGHT_CONNECTED;
            final RoiManager roiManager = new RoiManager();
            final ImageProcessor ip = imp.getProcessor();

            final int w = ip.getWidth();
            final int h = ip.getHeight();
            final ByteProcessor bpCompleted = new ByteProcessor(w, h);
            bpCompleted.setValue(255);

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bpCompleted.get(x, y) != 0) {
                        continue; // Already added this to an ROI.
                    }
                    float val = ip.getf(x, y);
                    if (val > 0) {
                        Wand wand = new Wand(ip);
                        wand.autoOutline(x, y, val, val, wandMode);
                        PolygonRoi roi = wandToRoi(wand);
                        roiManager.addRoi(roi);
                        bpCompleted.fill(roi);
                    }
                }
            }
        } catch (Exception e) {
            IJ.showStatus("Could not create ROIs from label image.");
            IJ.handleException(e);
        }
    }
}
