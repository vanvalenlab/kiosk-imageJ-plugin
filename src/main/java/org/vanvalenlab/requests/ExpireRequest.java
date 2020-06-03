package org.vanvalenlab.requests;

/**
 * Request class for /api/expire
 */
public class ExpireRequest implements KioskRequest {
    String hash;
    int expireIn;

    public ExpireRequest(String h, int e) {
        hash = h;
        expireIn = e;
    }
}
