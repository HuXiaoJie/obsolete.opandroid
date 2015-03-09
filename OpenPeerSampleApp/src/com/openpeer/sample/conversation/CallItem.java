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
package com.openpeer.sample.conversation;

import com.openpeer.javaapi.CallStates;
import com.openpeer.sdk.datastore.DatabaseContracts.MessageEntry;

import android.database.Cursor;
import android.text.TextUtils;

/**
 *
 */
public class CallItem {
    final static int POS_CALLID = 0;
    final static int POS_EVENT = 3;
    final static int POS_DIRECTION = 2;

    long time;
    long answerTime;
    long endTime;
    long peerId;
    int callClosedReason;
    int direction;// 0-incoming 1-outgoing
    String state;
    String peerName;
    String callId;

    /**
     * @param time
     * @param peerId
     * @param callClosedReason
     * @param direction
     * @param state
     */
    public CallItem(long time, long peerId, String name) {
        super();
        this.time = time;
        this.peerId = peerId;
        peerName = name;
    }

    public static CallItem fromCursor(Cursor cursor) {
        String text = cursor.getString(cursor
                .getColumnIndex(MessageEntry.COLUMN_MESSAGE_TEXT));
        long time = cursor.getLong(cursor
                .getColumnIndex(MessageEntry.COLUMN_MESSAGE_TIME)) ;
        long peerId = cursor.getLong(cursor
                .getColumnIndex(MessageEntry.COLUMN_SENDER_ID));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        // c.call_id||','||c.peer_id||','||c.direction||','||ce.event

        CallItem item = new CallItem(time, peerId, name);
        item.parseText(text);
        return item;
    }

    void parseText(String text) {
        String callInfo[] = text.split(",");
        callId = callInfo[POS_CALLID];
        direction = Integer.parseInt(callInfo[POS_DIRECTION]);
        String event = callInfo[POS_EVENT];
        if (!TextUtils.isEmpty(event)) {
            state = event;
        }
    }

    public boolean isOutgoing() {
        return direction == 0;
    }

    public String getPeerName() {
        return peerName;
    }

    public long getTime() {
        return time;
    }

    public String getState() {
        return state;
    }
}
