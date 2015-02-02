package com.openpeer.sdk.model;

import com.google.gson.annotations.SerializedName;
import com.openpeer.javaapi.CallClosedReasons;

import org.json.JSONObject;

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


}

