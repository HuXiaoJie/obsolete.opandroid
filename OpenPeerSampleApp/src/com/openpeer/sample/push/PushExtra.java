package com.openpeer.sample.push;

import com.openpeer.sdk.model.GsonFactory;
public class PushExtra {
    String peerURI;
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

    PushExtra(String peerUri, String peerUris, String messageType, String messageId,
              String replacesMessageId, String conversationType, String conversationId,
              String location, String timeInMillis) {
        this.peerURI = peerUri;
        this.peerURIs = peerUris;
        this.messageType = messageType;
        this.messageId = messageId;
        this.replacesMessageId = replacesMessageId;
        this.conversationType = conversationType;
        this.conversationId = conversationId;
        this.date = timeInMillis;
        this.location = location;
    }
    public String toJsonBlob(){
        return GsonFactory.getGson().toJson(this);
    }
}