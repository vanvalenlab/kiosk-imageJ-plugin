package org.vanvalenlab.requests;

/**
 * Request class for /api/redis
 */
public class GetRedisValueRequest implements KioskRequest {
    String hash;
    String key;

    public GetRedisValueRequest(String hash, String key) {
        this.hash = hash;
        this.key = key;
    }
}
