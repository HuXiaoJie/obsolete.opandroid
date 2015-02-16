package com.openpeer.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {"metaData" : { "conversationType" : "contact" } }
 */
public class ThreadMetaData {
    public static final String KEY_CONVERSATION_TYPE = "conversationType";
    public static final String KEY_META_DATA = "metaData";
    JSONObject data;

    public static ThreadMetaData fromJsonBlob(String jsonBlob) {
        try {
            JSONObject data = new JSONObject(jsonBlob);
            ThreadMetaData metaData1 = new ThreadMetaData();
            metaData1.data = data;
            return metaData1;
        } catch(JSONException e) {

        }
        return null;
    }

    public static ThreadMetaData newMetaData(String conversationType) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_CONVERSATION_TYPE, conversationType);
            JSONObject rootObject = new JSONObject();
            rootObject.put(KEY_META_DATA, jsonObject);
            ThreadMetaData threadMetaData = new ThreadMetaData();
            threadMetaData.data = rootObject;
            return threadMetaData;
        } catch(JSONException e) {
            return null;
        }
    }

    public String getConversationType() {
        try {
            return data.getJSONObject(KEY_META_DATA).getString(KEY_CONVERSATION_TYPE);
        } catch(JSONException e) {
            return null;
        }
    }

    public String toJsonBlob() {
        return data.toString();
    }

}
