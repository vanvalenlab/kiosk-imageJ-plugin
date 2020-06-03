package org.vanvalenlab.responses;

/**
 * Response class for /api/redis
 */
public class GetRedisValueResponse implements KioskResponse {
    String value;

    public GetRedisValueResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
