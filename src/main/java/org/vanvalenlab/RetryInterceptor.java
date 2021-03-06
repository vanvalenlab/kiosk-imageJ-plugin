package org.vanvalenlab;

import okhttp3.*;

import java.io.IOException;

public class RetryInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
            .method(original.method(), original.body())
            .build();

        Response response = chain.proceed(request);

        int retryCount = 1;

        while (!response.isSuccessful() && retryCount < Constants.MAX_HTTP_RETRIES) {
            response.close();

            request = original.newBuilder()
                .method(original.method(), original.body())
                .build();

            response = chain.proceed(request);
            retryCount++;
        }
        return response;
    }

}