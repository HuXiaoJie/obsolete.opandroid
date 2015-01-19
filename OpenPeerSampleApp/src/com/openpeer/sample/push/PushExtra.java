package com.openpeer.sample.push;

import com.openpeer.sdk.model.GsonFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class PushExtra {
    public String getPeerURI() {
        return peerURI;
    }

    public String getPeerURIs() {
        return peerURIs;
    }

    public String getLocation() {
        return location;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getReplacesMessageId() {
        return replacesMessageId;
    }

    public String getDate() {
        return date;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getConversationType() {
        return conversationType;
    }

    String peerURI;
    String senderName;
    String peerURIs;
    String location;
    String messageType;
    String messageId;
    String replacesMessageId;
    String date;
    String conversationId;
    String conversationType;

    //empty constructor is required for GSON
    PushExtra() {
    }

    public PushExtra(String peerUri, String senderName,String peerUris, String messageType, String messageId,
                     String replacesMessageId, String conversationType, String conversationId,
                     String location, String timeInMillis) {
        this.peerURI = peerUri;
        this.senderName=senderName;
        this.peerURIs = peerUris;
        this.messageType = messageType;
        this.messageId = messageId;
        this.replacesMessageId = replacesMessageId;
        this.conversationType = conversationType;
        this.conversationId = conversationId;
        this.date = timeInMillis;
        this.location = location;
    }

    public static PushExtra fromString(String jsonBlob) {
        return GsonFactory.getGson().fromJson(jsonBlob, PushExtra.class);
    }

    public String toJsonBlob() {
        return GsonFactory.getGson().toJson(this);
    }

    public JSONObject toJsonObject() {
        try {
            return new JSONObject(toJsonBlob());
        } catch(JSONException e) {
            return null;
        }
    }
}