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

package com.openpeer.sample.push;

import android.app.Notification;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPParticipantInfo;
import com.openpeer.sdk.model.MessageEditState;
import com.openpeer.sdk.utils.HOPModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PushNotificationBuilder implements com.urbanairship.push.PushNotificationBuilder {
    static final String TAG = PushNotificationBuilder.class.getSimpleName();

    static final String KEY_PEER_URI = "peerURI";
    static final String KEY_MESSAGE_TYPE = "messageType";
    static final String KEY_MESSAGE_ID = "messageId";
    static final String KEY_CONVERSATION_TYPE = "conversationType";
    static final String KEY_CONVERSATION_ID = "conversationId";
    static final String KEY_SEND_TIME = "date";

    @Override
    public Notification buildNotification(String alert,
                                          Map<String, String> extras) {
        Log.d("test", "build push notification for " + alert);

        String senderUri = extras.get(KEY_PEER_URI);
        String messageId = extras.get(KEY_MESSAGE_ID);
        String messageType = extras.get(KEY_MESSAGE_TYPE);
        messageType = TextUtils.isEmpty(messageType) ? OPMessage.TYPE_TEXT :
            messageType;
        String conversationType = extras.get(KEY_CONVERSATION_TYPE);
        String conversationId = extras.get(KEY_CONVERSATION_ID);
        // If message is already received, ignore notification
        if (null != HOPDataManager.getInstance().getMessage(messageId)) {
            Log.e(TAG, "received push for message that is already received "
                + messageId);
            return null;
        }
        HOPContact sender = HOPDataManager.getInstance().getUserByPeerUri(senderUri);
        if (sender == null) {
            Log.e("test", "Couldn't find user for peer " + senderUri);
            return null;
        }
        OPMessage message = new OPMessage(sender.getUserId(),
                                          messageType,
                                          alert,
                                          Long.parseLong(extras.get(KEY_SEND_TIME)) * 1000l,
                                          messageId,
                                          MessageEditState.Normal);
        String peerURIsString = extras.get("peerURIs");
        List<HOPContact> users = new ArrayList<>();
        users.add(sender);
        if (!TextUtils.isEmpty(peerURIsString)) {
            String peerURIs[] = TextUtils.split(peerURIsString, ",");
            for (String uri : peerURIs) {
                HOPContact user = HOPDataManager.getInstance().getUserByPeerUri(uri);
                if (user == null) {
                    //TODO: error handling
                    Log.e(TAG, "peerUri user not found " + uri);
                    return null;
                } else {
                    users.add(user);
                }
            }
        }
        HOPParticipantInfo HOPParticipantInfo = new HOPParticipantInfo(HOPModelUtils.getWindowId
            (users),
                                                              users);
        //Make sure conversation is saved in db.
        HOPConversation conversation = HOPConversation.getConversation
            (GroupChatMode.valueOf(conversationType), HOPParticipantInfo, conversationId, true);
        HOPDataManager.getInstance().saveMessage(message,
                                                conversation.getId(), HOPParticipantInfo);
        //For contact based conversation, teh conversation id might be different.
        return OPNotificationBuilder.buildNotificationForMessage(
            new long[]{sender.getUserId()}, message, conversationType, conversation.getConversationId());
    }

    @Override
    public int getNextId(String alert, Map<String, String> extras) {
        String senderUri = extras.get(KEY_PEER_URI);
        HOPContact sender = HOPDataManager.getInstance().getUserByPeerUri(
            senderUri);
        if (sender != null) {
            return (int) HOPModelUtils.getWindowId(new long[]{
                sender.getUserId()});
        }
        return 0;
    }
}
