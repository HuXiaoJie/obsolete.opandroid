package com.openpeer.sdk.model;

import android.text.format.Time;

import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPMessage;

/**
 * Created by brucexia on 2015-02-12.
 */
public class HOPMessage {
    OPMessage opMessage;

    public String getReplacesMessageId() {
        return opMessage.getReplacesMessageId();
    }

    public void setTime(Time mTime) {
        opMessage.setTime(mTime);
    }

    public boolean isRead() {
        return opMessage.isRead();
    }

    public static String generateUniqueId() {
        return OPMessage.generateUniqueId();
    }

    public void setValidated(boolean mValidated) {
        opMessage.setValidated(mValidated);
    }

    public void setEditState(MessageEditState state) {
        opMessage.setEditState(state);
    }

    public OPContact getFrom() {
        return opMessage.getFrom();
    }

    public MessageDeliveryStates getDeliveryStatus() {
        return opMessage.getDeliveryStatus();
    }

    public String getMessageId() {
        return opMessage.getMessageId();
    }

    public void setRead(boolean read) {
        opMessage.setRead(read);
    }

    public MessageEditState getEditState() {
        return opMessage.getEditState();
    }

    public long getSenderId() {
        return opMessage.getSenderId();
    }

    public String getMessageType() {
        return opMessage.getMessageType();
    }

    public void setFrom(OPContact mFrom) {
        opMessage.setFrom(mFrom);
    }

    public void setMessageId(String messageId) {
        opMessage.setMessageId(messageId);
    }

    public boolean isValidated() {
        return opMessage.isValidated();
    }

    public void setDeliveryStatus(MessageDeliveryStates status) {
        opMessage.setDeliveryStatus(status);
    }

    public void setMessage(String mMessage) {
        opMessage.setMessage(mMessage);
    }

    public String getMessage() {
        return opMessage.getMessage();
    }

    public void setSenderId(long mSenderId) {
        opMessage.setSenderId(mSenderId);
    }

    public void setMessageType(String mMessageType) {
        opMessage.setMessageType(mMessageType);
    }

    public Time getTime() {
        return opMessage.getTime();
    }

    public void setReplacesMessageId(String mReplacesMessageId) {
        opMessage.setReplacesMessageId(mReplacesMessageId);
    }

    public HOPContact getFromUser() {
        return opMessage.getFromUser();
    }
}
