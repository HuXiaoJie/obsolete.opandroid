package com.openpeer.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * {"callStatus":{"$id":"adf","status":"placed","mediaType":"audio",
 * "callee":"peer://opp.me/kadjfadkfj","error":{"$id":404}}
 */
public class CallSystemMessage {
    public static final String STATUS_PLACED ="placed";
    public static final String STATUS_ANSWERED ="answered";
    public static final String STATUS_HUNGUP ="hungup";

    public static final String MEDIATYPE_AUDIO="audio";
    public static final String MEDIATYPE_VIDEO="video";

    CallStatus callStatus;

    public CallSystemMessage() {
    }

    public CallSystemMessage(String id,
                             String status,
                             String mediaType,
                             String callee) {
        callStatus = new CallStatus();
        callStatus.id = id;
        callStatus.status = status;
        callStatus.mediaType = mediaType;
        callStatus.callee = callee;
    }

    public String getCallId() {
        return callStatus.id;
    }

    public String getStatus() {
        return callStatus.status;
    }

    public String getMediaType() {
        return callStatus.mediaType;
    }

    public String getCalleePeerUri() {
        return callStatus.callee;
    }

    public static class CallStatus {
        @SerializedName("$id")
        String id;
        String status;
        String mediaType;
        String callee;
    }
}

