package org.vanvalenlab;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Wand;
import ij.process.ImageProcessor;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

public class LabelROIConverterTest {
    private BufferedImage img;
    private ImagePlus imp;

    public ImagePlus getTestImage(int width) {
        BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            if (i > width / 2) {
                img.setRGB(i, i, 1); // set an "object"
            }
        }
        ImagePlus imp = new ImagePlus("test.tiff", img);
        return imp;
    }

    @Test
    public void testWandToRoi() {
        int[] widths = {20, 2000};
        for (int width : widths) {
            ImagePlus imp = getTestImage(width);
            Wand wand = new Wand(imp.getProcessor());
            wand.autoOutline(0, 0, 1.0, Wand.EIGHT_CONNECTED);
            PolygonRoi roi = LabelROIConverter.wandToRoi(wand);
            assert (roi.containsPoint(width / 2 + 1, width / 2 + 1));
        }
    }

    @Test
    public void testCreateLabelOverlay() {
        ImagePlus imp = getTestImage(20);
        LabelROIConverter.createLabelOverlay(imp);
        assert (imp.getOverlay() != null);
    }
}
