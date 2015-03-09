/**
 * Copyright (c) 2014, SMB Phone Inc. / Hookflash Inc.
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p/>
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.openpeer.sdk.model;

import com.openpeer.javaapi.MessageDeliveryStates;

/**
 *
 */
public class MessageEvent {
    long mEventId;// for now this is local database record id
    String messageId;
    EventType mType;
    String description;
    long time;

    public long getEventId() {
        return mEventId;
    }

    public void setEventId(long mEventId) {
        this.mEventId = mEventId;
    }

    /**
     * @param messageId
     * @param mType
     * @param description
     * @param time
     */
    public MessageEvent(String messageId, EventType mType, String description, long time) {
        super();
        this.messageId = messageId;
        this.mType = mType;
        this.description = description;
        this.time = time;
    }

    public enum EventType {
        DeliveryStateChange,
        Edit,
        Delete
        // Favorite --?
    }

    /**
     * Get message event type
     * 
     * @return
     */
    public EventType getEvent() {
        // TODO Auto-generated method stub
        return mType;
    }

    /**
     * @return
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return description;
    }

    /**
     * @returnÒ
     */
    public long getTime() {
        // TODO Auto-generated method stub
        return time;
    }

    /**
     * @return
     */
    public String getMessageId() {
        // TODO Auto-generated method stub
        return messageId;
    }

    /**
     * @param deliveryStatus
     * @return
     */
    public static String getStateChangeJsonBlob(MessageDeliveryStates deliveryStatus) {
        String blobString = String.format("{\"newState\":\"%s\"}", deliveryStatus.name());
        return blobString;
    }
}
