package org.vanvalenlab;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.vanvalenlab.responses.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KioskHttpClientTest {

    private KioskHttpClient kioskHttpClient;
    private MockWebServer server;
    private HttpUrl baseUrl;
    private Gson g;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("/api");
        kioskHttpClient = new KioskHttpClient(baseUrl.toString());
        g = new Gson();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testSendHttpRequest() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        // successful response
        String expectedResponse = "Success!";
        server.enqueue(new MockResponse().setBody(expectedResponse));
        String response = kioskHttpClient.sendHttpRequest(request);
        assertEquals(response, expectedResponse);

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.sendHttpRequest(request)
        );
    }

    @Test
    public void testGetRedisValue() throws IOException {
        String redisHash = "hash";
        String redisKey = "field";

        // successful response
        String expectedValue = "success!";
        String expectedResponse = g.toJson(new GetRedisValueResponse(expectedValue));
        server.enqueue(new MockResponse().setBody(expectedResponse));
        String response = kioskHttpClient.getRedisValue(redisHash, redisKey);
        assertEquals(expectedValue, response);

        // valid but empty JSON
        server.enqueue(new MockResponse().setBody("{}"));
        response = kioskHttpClient.getRedisValue(redisHash, redisKey);
        assertEquals(null, response);

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.getRedisValue(redisHash, redisKey)
        );

        // invalid JSON but successful response
        server.enqueue(new MockResponse().setBody("failed"));
        assertThrows(JsonSyntaxException.class, () ->
                kioskHttpClient.getRedisValue(redisHash, redisKey)
        );
    }

    @Test
    public void testUploadFile() throws IOException {
        File temporaryFile = folder.newFile();
        String filePath = temporaryFile.getAbsolutePath();

        // successful response
        String expectedValue = "uploadedFilePath.jpg";
        String expectedURL = "http://test.com/uploadedFilePath.jpg";
        String expectedResponse = g.toJson(
                new UploadFileResponse(expectedValue, expectedURL));

        server.enqueue(new MockResponse().setBody(expectedResponse));
        String response = kioskHttpClient.uploadFile(filePath);
        assertEquals(expectedValue, response);

        // valid but empty JSON
        server.enqueue(new MockResponse().setBody("{}"));
        response = kioskHttpClient.uploadFile(filePath);
        assertEquals(null, response);

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.uploadFile(filePath)
        );

        // invalid JSON but successful response
        server.enqueue(new MockResponse().setBody("failed"));
        assertThrows(JsonSyntaxException.class, () ->
                kioskHttpClient.uploadFile(filePath)
        );

        // test local file does not exist.
        String invalidFilePath = String.format(
                "%s%s", folder.getRoot().getAbsolutePath(), "invalid.jpg");
        assertThrows(IOException.class, () ->
                kioskHttpClient.uploadFile(invalidFilePath)
        );
    }

    @Test
    public void testExpireRedisHash() throws IOException {
        String redisHash = "hash";
        int expireTime = 3;

        // successful response
        int expectedValue = 1;
        String expectedResponse = g.toJson(new ExpireResponse(expectedValue));
        server.enqueue(new MockResponse().setBody(expectedResponse));
        int response = kioskHttpClient.expireRedisHash(redisHash, expireTime);
        assertEquals(expectedValue, response);

        // valid but empty JSON
        server.enqueue(new MockResponse().setBody("{}"));
        response = kioskHttpClient.expireRedisHash(redisHash, expireTime);
        assertEquals(0, response); // primitive int defaults to 0.

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.expireRedisHash(redisHash, expireTime)
        );

        // invalid JSON but successful response
        server.enqueue(new MockResponse().setBody("failed"));
        assertThrows(JsonSyntaxException.class, () ->
                kioskHttpClient.expireRedisHash(redisHash, expireTime)
        );
    }

    @Test
    public void testGetJobTypes() throws IOException {
        // successful response
        String[] expectedJobTypes = {"exampleType"};
        String expectedResponse = g.toJson(new JobTypesResponse(expectedJobTypes));

        server.enqueue(new MockResponse().setBody(expectedResponse));
        String[] response = kioskHttpClient.getJobTypes();
        assertArrayEquals(response, expectedJobTypes);

        // valid but empty JSON
        server.enqueue(new MockResponse().setBody("{}"));
        response = kioskHttpClient.getJobTypes();
        assertArrayEquals(response, null);

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.getJobTypes()
        );

        // invalid JSON but successful response
        server.enqueue(new MockResponse().setBody("failed"));
        assertThrows(JsonSyntaxException.class, () ->
                kioskHttpClient.getJobTypes()
        );
    }

    @Test
    public void testGetStatus() throws IOException {
        String redisHash = "hash";

        // successful response
        String expectedValue = "testing";
        String expectedResponse = g.toJson(new GetStatusResponse(expectedValue));
        server.enqueue(new MockResponse().setBody(expectedResponse));
        String response = kioskHttpClient.getStatus(redisHash);
        assertEquals(expectedValue, response);

        // valid but empty JSON
        server.enqueue(new MockResponse().setBody("{}"));
        response = kioskHttpClient.getStatus(redisHash);
        assertEquals(null, response);

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.getStatus(redisHash)
        );

        // invalid JSON but successful response
        server.enqueue(new MockResponse().setBody("failed"));
        assertThrows(JsonSyntaxException.class, () ->
                kioskHttpClient.getStatus(redisHash)
        );
    }

    @Test
    public void testCreateJob() throws IOException {
        String imageName = "path";
        String uploadedName = "uploadedPath";
        String jobType = "test";

        // successful response
        String expectedValue = "newJobHash";
        String expectedResponse = g.toJson(new CreateJobResponse(expectedValue));
        server.enqueue(new MockResponse().setBody(expectedResponse));
        String response = kioskHttpClient.createJob(imageName, uploadedName, jobType);
        assertEquals(expectedValue, response);

        // valid but empty JSON
        server.enqueue(new MockResponse().setBody("{}"));
        response = kioskHttpClient.createJob(imageName, uploadedName, jobType);
        assertEquals(null, response);

        // error response
        server.enqueue(new MockResponse().setResponseCode(500).setBody("failed"));
        assertThrows(IOException.class, () ->
                kioskHttpClient.createJob(imageName, uploadedName, jobType)
        );

        // invalid JSON but successful response
        server.enqueue(new MockResponse().setBody("failed"));
        assertThrows(JsonSyntaxException.class, () ->
                kioskHttpClient.createJob(imageName, uploadedName, jobType)
        );
    }

}
