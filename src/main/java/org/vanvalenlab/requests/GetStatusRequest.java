package org.vanvalenlab.requests;

/**
 * Request class for /api/redis/status
 */
public class GetStatusRequest implements KioskRequest {
    String hash;

    public GetStatusRequest(String redisHash) {
        this.hash = redisHash;
    }
}
