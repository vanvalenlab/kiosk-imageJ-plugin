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

public class LabelROIConverterTest {
    private BufferedImage img;
    private ImagePlus imp;

    @Before
    public void setUp() throws IOException {
        img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 1, 1); // set an "object"
        img.setRGB(0, 2, 1); // set an "object"
        imp = new ImagePlus("test.tiff", img);
    }

    @Test
    public void testWandToRoi() {
        Wand wand = new Wand(imp.getProcessor());
        wand.autoOutline(0, 0, 1.0, Wand.EIGHT_CONNECTED);
        PolygonRoi roi = LabelROIConverter.wandToRoi(wand);
        assert (roi.containsPoint(0, 1));
        assert (roi.containsPoint(0, 2));
    }

    @Test
    public void testCreateLabelOverlay() {
        LabelROIConverter.createLabelOverlay(imp);
        assert (imp.getOverlay() != null);
    }
}
