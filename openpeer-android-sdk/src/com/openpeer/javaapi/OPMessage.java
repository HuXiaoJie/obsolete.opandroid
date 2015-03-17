/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.javaapi;

import android.text.format.Time;

import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.MessageEditState;

public class OPMessage {
    public static final int DS_DISCOVERING = 0;
    public static final int DS_USER_NOT_AVAILABLE = 1;
    public static final int DS_DELIVERED = 2;

    public static final String TYPE_TEXT = "text/x-application-hookflash-message-text";
    public static final String TYPE_JSON_SYSTEM_MESSAGE = "text/json-system-message";

    // Used to record/show call record
    public static final String TYPE_INERNAL_CALL_VIDEO = "call/video";
    public static final String TYPE_INERNAL_CALL_AUDIO = "call/audio";
    public static final String TYPE_INERNAL_FILE_PHOTO= "file/photo";

    private OPContact mFrom;
    private String mMessageType;
    private String mMessage;
    private Time mTime;
    private long mSenderId;
    private String mMessageId;
    private boolean mRead;
    private MessageEditState mEditState;// read time in millis
    private MessageDeliveryStates mDeliveryStatus;
    private String mReplacesMessageId = "";
    private boolean mValidated;

    public String getReplacesMessageId() {
        return mReplacesMessageId;
    }

    public void setReplacesMessageId(String mReplacesMessageId) {
        this.mReplacesMessageId = mReplacesMessageId;
    }

    public boolean isValidated() {
        return mValidated;
    }

    public void setValidated(boolean mValidated) {
        this.mValidated = mValidated;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        this.mRead = read;
    }

    public MessageEditState getEditState() {
        if (mEditState == null) {
            mEditState = MessageEditState.Normal;
        }
        return mEditState;
    }

    public void setEditState(MessageEditState state) {
        this.mEditState = state;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        this.mMessageId = messageId;
    }

    public long getSenderId() {
        return mSenderId;
    }

    public void setSenderId(long mSenderId) {
        this.mSenderId = mSenderId;
    }

    public OPMessage() {
    }

    public OPMessage(long senderId, String mMessageType, String message,
            long sendTime, String messageId, MessageEditState state) {
        super();
        this.mSenderId = senderId;
        this.mMessageType = mMessageType;
        this.mMessage = message;
        this.mTime = new Time();
        mTime.set(sendTime);
        this.mMessageId = messageId;
        this.mEditState = state;

    }

    public OPMessage(long senderId, String mMessageType, String message,
            long sendTime, String messageId) {
        this(senderId, mMessageType, message, sendTime, messageId,
                MessageEditState.Normal);
    }

    public OPContact getFrom() {
        return mFrom;
    }

    public void setFrom(OPContact mFrom) {
        this.mFrom = mFrom;
    }

    public String getMessageType() {
        return mMessageType;
    }

    public void setMessageType(String mMessageType) {
        this.mMessageType = mMessageType;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Time getTime() {
        return mTime;
    }

    public void setTime(Time mTime) {
        this.mTime = mTime;
    }

    public MessageDeliveryStates getDeliveryStatus() {
        return mDeliveryStatus;
    }

    public void setDeliveryStatus(MessageDeliveryStates status) {
        mDeliveryStatus = status;
    }

    public String toString() {
        return super.toString() + " from " + mFrom + " messageType "
                + mMessageType + " message " + mMessage + " id " + mMessageId
                + " sender id " + mSenderId;
    }

    /**
     * Helper function. Get from user of received message. Don't call this function for your own message.
     *
     * @return
     */
    public HOPContact getFromUser() {
        return HOPDataManager.getInstance().getUserById(mSenderId);
    }

    /**
     * Generate a unique messageId
     *
     * @return
     */
    public static String generateUniqueId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

}
