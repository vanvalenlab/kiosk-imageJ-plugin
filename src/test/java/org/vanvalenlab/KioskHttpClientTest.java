package org.vanvalenlab;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class KioskHttpClientTest {

    @Test
    public void testSendHttpRequest() throws IOException {
        MockWebServer server = new MockWebServer();

        String expectedResponse = "Success!";

        // server.enqueue(new MockResponse().setBody("{\"value\": \"success\"}"));
        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/api");

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        try {
            String response = kioskHttpClient.sendHttpRequest(request);
            assertEquals(response, expectedResponse);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test(expected=IOException.class)
    public void testSendHttpRequestFailure() throws IOException {
        MockWebServer server = new MockWebServer();

        String expectedResponse = "Success!";

        // server.enqueue(new MockResponse().setBody("{\"value\": \"success\"}"));
        server.enqueue(new MockResponse().setResponseCode(404).setBody("failed"));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/api");

        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        try {
            String response = kioskHttpClient.sendHttpRequest(request);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test
    public void testGetRedisValue() throws IOException {
        MockWebServer server = new MockWebServer();

        String expectedValue = "success!";
        String expectedResponse = String.format("{\"value\": \"%s\"}", expectedValue);

        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/");

        System.out.println(baseUrl.toString());

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        try {
            String response = kioskHttpClient.getRedisValue("hash", "field");
            assertEquals(response, expectedValue);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test
    public void testExpireRedisHash() throws IOException {
        MockWebServer server = new MockWebServer();

        int expectedValue = 1;
        int expireTime = 3;
        String expectedResponse = String.format("{\"value\": \"%s\"}", expectedValue);

        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/");

        System.out.println(baseUrl.toString());

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        try {
            int response = kioskHttpClient.expireRedisHash("hash", expireTime);
            assertEquals(response, expectedValue);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test
    public void testExpireRedisHashNotFound() throws IOException {
        MockWebServer server = new MockWebServer();

        int expectedValue = 0;
        int expireTime = 3;
        String expectedResponse = String.format("{\"value\": \"%s\"}", expectedValue);

        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/");

        System.out.println(baseUrl.toString());

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        try {
            int response = kioskHttpClient.expireRedisHash("hash", expireTime);
            assertEquals(response, expectedValue);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test
    public void testGetJobTypes() throws IOException {
        MockWebServer server = new MockWebServer();

        String[] expectedJobTypes = {"exampleType"};
        String expectedValue = String.format("[\"%s\"]", expectedJobTypes[0]);
        String expectedResponse = String.format("{\"jobTypes\": %s}", expectedValue);

        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/");

        System.out.println(baseUrl.toString());

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        try {
            String[] response = kioskHttpClient.getJobTypes();
            assertArrayEquals(response, expectedJobTypes);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test
    public void testGetStatus() throws IOException {
        MockWebServer server = new MockWebServer();

        String expectedValue = "testing";
        String expectedResponse = String.format("{\"status\": \"%s\"}", expectedValue);

        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/");

        System.out.println(baseUrl.toString());

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        try {
            String response = kioskHttpClient.getStatus("hash");
            assertEquals(response, expectedValue);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

    @Test
    public void testCreateJob() throws IOException {
        MockWebServer server = new MockWebServer();

        String expectedValue = "newJobHash";
        String expectedResponse = String.format("{\"hash\": \"%s\"}", expectedValue);

        server.enqueue(new MockResponse().setBody(expectedResponse));
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/");

        System.out.println(baseUrl.toString());

        KioskHttpClient kioskHttpClient = new KioskHttpClient(baseUrl.toString());

        String imageName = "path";
        String uploadedName = "uploadedPath";
        String jobType = "test";

        try {
            String response = kioskHttpClient.createJob(imageName, uploadedName, jobType);
            assertEquals(response, expectedValue);
        } finally {
            // Shut down the server. Instances cannot be reused.
            server.shutdown();
        }
    }

}
