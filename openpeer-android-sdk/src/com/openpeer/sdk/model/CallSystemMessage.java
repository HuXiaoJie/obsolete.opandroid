package com.openpeer.sdk.model;

import com.google.gson.annotations.SerializedName;
import com.openpeer.javaapi.CallClosedReasons;

import org.json.JSONObject;

/**
 * {"callStatus":{"$id":"adf","status":"placed","mediaType":"audio",
 * "callee":"peer://opp.me/kadjfadkfj","error":{"$id":404}}
 */
public class CallSystemMessage {
    public static final String STATUS_PLACED = "placed";
    public static final String STATUS_ANSWERED = "answered";
    public static final String STATUS_HUNGUP = "hungup";

    public static final String MEDIATYPE_AUDIO = "audio";
    public static final String MEDIATYPE_VIDEO = "video";

    public static final String KEY_CALL_STATUS_STATUS = "status";
    public static final String KEY_CALL_STATUS_MEDIA_TYPE = "mediaType";
    public static final String KEY_CALL_STATUS_CALLEE = "callee";
    public static final String KEY_ID = "$id";
    public static final String KEY_ERROR = "error";
    private JSONObject object;

    public CallSystemMessage(JSONObject object) {
        this.object = object;
    }

    public String getStatus() {
        return object.optString(KEY_CALL_STATUS_STATUS);
    }

    public String getCalleeUri() {
        return object.optString(KEY_CALL_STATUS_CALLEE);
    }

    public String getCallId() {
        return object.optString(KEY_ID);
    }

    public JSONObject getError() {
        return object.optJSONObject(KEY_CALL_STATUS_STATUS);
    }

    public JSONObject getMediaType() {
        return object.optJSONObject(KEY_CALL_STATUS_MEDIA_TYPE);
    }

}

