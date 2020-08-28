package org.vanvalenlab;

import com.google.gson.Gson;
import ij.ImagePlus;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.vanvalenlab.exceptions.KioskJobFailedException;
import org.vanvalenlab.responses.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KioskJobManagerTest {
    private MockWebServer server;
    private HttpUrl baseUrl;
    private Gson g;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("/api");
        g = new Gson();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetFilePath() throws IOException {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        ImagePlus imp = new ImagePlus("test.tiff", img);

        // image is not saved, so it will return a temporary file path.
        String filePath = KioskJobManager.getFilePath(imp);

        // load a new image from the same file path.
        ImagePlus newImp = new ImagePlus(filePath);

        String newPath = KioskJobManager.getFilePath(newImp);
        assert (newPath.equals(filePath));
    }

    @Test
    public void testConfigureOptions() {

    }

    @Test
    public void testSelectJobType() throws AWTException, IOException {
        String expectedJobType = "testJobType";
        String responseStr = String.format("{\"jobTypes\": [\"%s\"]}", expectedJobType);
        String jobType;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        final Robot robot = new Robot();
        robot.setAutoDelay(100);
        robot.setAutoWaitForIdle(true);

        // test successful request
//        server.enqueue(new MockResponse().setBody(responseStr));
//        executor.submit(() -> {
//            System.out.println("First robot delay");
//            robot.delay(100);
//            System.out.println("About to push key");
//            robot.keyPress(InputEvent.BUTTON1_DOWN_MASK);
//            System.out.println("key has been pressed");
//        });
//        jobType = KioskJobManager.selectJobType(baseUrl.toString());
//        System.out.println("jobType");
//        assertEquals(expectedJobType, jobType);

        // test failed http request
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        assertThrows(IOException.class, () ->
                KioskJobManager.selectJobType(baseUrl.toString())
        );

        // test empty json body
        server.enqueue(new MockResponse().setBody("{}"));
        assertThrows(IOException.class, () ->
                KioskJobManager.selectJobType(baseUrl.toString())
        );
    }

    @Test
    public void testRunJob() throws IOException, KioskJobFailedException {
        String jobType = "testJobType";

        File temporaryFile = folder.newFile();
        String filePath = temporaryFile.getAbsolutePath();

        int expireTime = 3;

        Map<String, Object> defaults = new LinkedHashMap<String, Object>();
        defaults.put(Constants.KIOSK_HOST, baseUrl.toString());
        defaults.put(Constants.UPDATE_STATUS_MILLISECONDS, 0);
        defaults.put(Constants.EXPIRE_TIME_SECONDS, expireTime);

        String uploadResponse = g.toJson(new UploadFileResponse(
                "uploadedName", "imageURL"));
        String createResponse = g.toJson(new CreateJobResponse("hash"));
        String successStatusResponse = g.toJson(new GetStatusResponse(Constants.SUCCESS_STATUS));
        String failStatusResponse = g.toJson(new GetStatusResponse(Constants.FAILED_STATUS));
        String expireResponse = g.toJson(new ExpireResponse(expireTime));
        String successOutput = "https://imagej.nih.gov/ij/images/2D_Gel.jpg";
        String successResponse = g.toJson(new GetRedisValueResponse(successOutput));
        String failOutput = "this is the reason for failure";
        String failResponse = g.toJson(new GetRedisValueResponse(failOutput));

        // Job completes with successful status
        // upload and create
        server.enqueue(new MockResponse().setBody(uploadResponse));
        server.enqueue(new MockResponse().setBody(createResponse));
        // update status
        server.enqueue(new MockResponse().setBody(successStatusResponse));
        // expire
        server.enqueue(new MockResponse().setBody(expireResponse));
        // get output
        server.enqueue(new MockResponse().setBody(successResponse));

        KioskJobManager.runJob(jobType, filePath, defaults);

        // Job completes with failed status
        // upload and create
        server.enqueue(new MockResponse().setBody(uploadResponse));
        server.enqueue(new MockResponse().setBody(createResponse));
        // update status
        server.enqueue(new MockResponse().setBody(failStatusResponse));
        // expire
        server.enqueue(new MockResponse().setBody(expireResponse));
        // get output
        server.enqueue(new MockResponse().setBody(failResponse));

        // fails due to failed status
        assertThrows(KioskJobFailedException.class, () ->
                KioskJobManager.runJob(jobType, filePath, defaults)
        );

        // job hits errors
        // bad HTTP response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        assertThrows(IOException.class, () ->
                KioskJobManager.runJob(jobType, filePath, defaults)
        );
        // invalid file path.
        String invalidPath = folder.getRoot() + "nofile.jpg";
        assertThrows(IOException.class, () ->
            KioskJobManager.runJob(jobType, invalidPath, defaults)
        );
    }
}
