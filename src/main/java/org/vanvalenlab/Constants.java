package org.vanvalenlab;

import okhttp3.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Constants {

    private Constants() {
        // restrict instantiation
    }

    // Media types for HTTP requests
    static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");
    static final MediaType MEDIA_TYPE_FORM_DATA = MediaType.get("multipart/form-data; charset=utf-8");

    // Retry failed HTTP requests
    static final int MAX_HTTP_RETRIES = 3;

    // GUI prompts / messages
    static final String SELECT_FILE_MESSAGE = "Select the file to submit to the DeepCell Kiosk";
    static final String SELECT_JOB_TITLE = "Select a Job Type";
    static final String SUCCESS_MESSAGE = "DeepCell Kiosk Job Complete!";
    static final String FAIL_MESSAGE = "DeepCell Kiosk Job Failed!";
    static final String OPTIONS_MENU_TITLE = "Configuration";
    static final String JOB_SELECT_MENU_TITLE = "Job Setup";

    // Job statuses
    static final String SUCCESS_STATUS = "done";
    static final String FAILED_STATUS = "failed";
    // Map of job statuses and their progress value.
    static final Map<String, Double> JOB_STATUSES = new LinkedHashMap<String, Double>() {
        {
            put(null, 0.0);
            put("new", 0.0);
            put("started", 0.1);
            put("pre-processing", 0.2);
            put("predicting", 0.33);
            put("post-processing", 0.67);
            put("saving-results", 0.8);
            put("done", 1.0);
            put("failed", 1.0);
        }
    };

    // Configuration keys
    static final String KIOSK_HOST = "DeepCell Kiosk Host";
    static final String UPDATE_STATUS_MILLISECONDS = "Status Update Interval (ms)";
    static final String EXPIRE_TIME_SECONDS = "Job Expires After (s)";

    // TODO: add descriptions for all options

    // Configurable options
    private static final Map<String, Object> DEFAULTS = new LinkedHashMap<String, Object>();
    static {
        DEFAULTS.put(KIOSK_HOST, "https://deepcell.org");
        DEFAULTS.put(UPDATE_STATUS_MILLISECONDS, 5000);
        DEFAULTS.put(EXPIRE_TIME_SECONDS, 3600);
    }

    static Object getDefault(final String key) {
        return DEFAULTS.get(key);
    }
}
