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
package com.openpeer.sdk.model;

import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.app.HOPDataManager;
import com.openpeer.sdk.utils.CollectionUtils;
import com.openpeer.sdk.utils.HOPModelUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;

/**
 * A session represents exact state of a conversation thread.
 */
public class HOPConversation extends Observable {
    private static final String TAG = HOPConversation.class.getSimpleName();

    // So if Alice and Bob, Eric in group chat, Alice then added Mike, a new
    // session is created but from Alice point of view,
    // there's only one group chat and when we construct the chat history after
    // restart,
    private OPConversationThread mConvThread;// the active thread being used

    private String lastReadMessageId;
    private OPMessage mLastMessage;
    private Hashtable<String, OPMessage> mMessageDeliveryQueue;
    private HOPConversationEvent mLastEvent;

    //try to keep the data fields correspond to database columns
    private long _id;// database id
    private String conversationId = "";
    private String topic;
    private boolean removed;

    public boolean isQuit() {
        return quit;
    }

    public void quit() {
    }

    private boolean quit;

    //Start: from ios
    String title;

    //end: from ios

    public boolean amIRemoved() {
        return removed;
    }

    public HOPParticipantInfo getParticipantInfo() {
        return HOPParticipantInfo;
    }

    void setParticipantInfo(HOPParticipantInfo HOPParticipantInfo) {
        this.HOPParticipantInfo = HOPParticipantInfo;
    }

    HOPParticipantInfo HOPParticipantInfo;
    private GroupChatMode type;

    public HOPConversation(HOPParticipantInfo HOPParticipantInfo, String conversationId,
                           GroupChatMode mode) {
        this.conversationId = conversationId;
        type = mode;
        this.HOPParticipantInfo = HOPParticipantInfo;
    }

    long save() {
        _id = HOPDataManager.getInstance().saveConversation(this);
        return _id;
    }

    public static void registerDelegate(HOPConversationDelegate listener) {
        HOPConversationManager.getInstance().registerDelegate(listener);
    }

    public static void unregisterDelegate(HOPConversationDelegate listener) {
        HOPConversationManager.getInstance().unregisterDelegate(listener);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
        sendSystemMessage(topic);
    }

    public void setDisabled(boolean disabled){
        removed =disabled;
        HOPDataManager.getInstance().updateConversation(this);
    }

    private Hashtable<String, OPMessage> getMessageDeliveryQueue() {
        if (mMessageDeliveryQueue == null) {
            mMessageDeliveryQueue = new Hashtable<String, OPMessage>();
        }
        return mMessageDeliveryQueue;
    }

    public OPMessage sendMessage(OPMessage message, boolean signMessage) {
        Log.d("test", "sending messge " + message);
        message.setRead(true);
        getThread(true).sendMessage(message.getMessageId(),
                                    message.getReplacesMessageId(),
                                    message.getMessageType(), message.getMessage(), signMessage);
        if(message.getMessageType().equals(OPMessage.TYPE_TEXT)) {
            if (!TextUtils.isEmpty(message.getReplacesMessageId())) {
                HOPDataManager.getInstance().updateMessage(message, this);
            } else {
                HOPDataManager.getInstance().saveMessage(message, conversationId, HOPParticipantInfo);

            }
        }
        return message;
    }

    void sendSystemMessage(String message){

    }

    /**
     * Get the message that's displayed. Used to decide from which message to display
     *
     * @return
     */
    private String getReadMessageId() {
        return lastReadMessageId;
    }

    public void setReadMessageId(String readMessageId) {
        this.lastReadMessageId = readMessageId;
    }

    public OPCall getCurrentCall() {
        return HOPCallManager.getInstance().findCallByCbcId(HOPParticipantInfo.getCbcId());
    }

    /**
     * If an session existed for an incoming message and thread is null, set thread.
     *
     * @param thread
     */
    void setThread(OPConversationThread thread) {
        mConvThread = thread;
        if(conversationId==null){
            conversationId=mConvThread.getConversationId();
        }
    }

    OPConversationThread getThread(boolean createIfNo) {
        if (mConvThread == null && createIfNo) {
            mConvThread = HOPConversationManager.getInstance().getThread(type, conversationId, HOPParticipantInfo,createIfNo);
        }
        return mConvThread;
    }

    private OPConversationThread selectActiveThread(OPConversationThread newThread) {
        // for now just use the new thread
        if (mConvThread == null) {
            mConvThread = newThread;
        } else {
            if (!mConvThread.getThreadID().equals(newThread.getThreadID())) {
                mConvThread = newThread;
            }
        }
        return mConvThread;
    }

    public long getCurrentCbcId() {
        return HOPParticipantInfo.getCbcId();
    }

    public HOPConversationEvent getLastEvent() {
        return mLastEvent;
    }

    public void onNewEvent(HOPConversationEvent event) {
        HOPDataManager.getInstance().saveConversationEvent(event);
        mLastEvent = event;
    }

    private void addContactsToThread(List<HOPContact> users) {
        HOPModelUtils.addParticipantsToThread(mConvThread, users);
    }

    public OPCall placeCall(HOPContact user,
                            boolean includeAudio, boolean includeVideo) {

        OPContact newContact = user.getOPContact();
        OPCall call = OPCall.placeCall(getThread(true), newContact, includeAudio,
                                       includeVideo);
        return call;
    }

    /**
     * return the current participants, excludign yourself.
     *
     * @return
     */
    public List<HOPContact> getParticipants() {
        return HOPParticipantInfo.getParticipants();
    }

    public void setCbcId(long cbcId) {
        HOPParticipantInfo.setCbcId(cbcId);
    }

    public void setParticipants(List<HOPContact> participants) {
        HOPParticipantInfo.setUsers(participants);
    }

    private OPMessage getLastMessage() {
        return mLastMessage;
    }

    private void setLastMessage(OPMessage lastMessage) {
        mLastMessage = lastMessage;
    }

    private void onMessagePushNeeded(String MessageId, OPContact contact) {

    }

    private void onMessageDeliveryStateChanged(String MessageId,
                                               OPContact contact) {

    }

    private void onMessageDeliveryFailed(String MessageId, OPContact contact) {

    }

    public void addParticipants(List<HOPContact> users) {
        if (mConvThread != null) {
            addContactsToThread(users);
        } else {
            long oldCbcId = HOPParticipantInfo.getCbcId();
            HOPParticipantInfo.addUsers(users);
            HOPParticipantInfo.setCbcId(HOPModelUtils.getWindowId(HOPParticipantInfo
                                                                      .getParticipants()));
            HOPConversationManager.getInstance().onConversationParticipantsChange(this, oldCbcId,
                                                                               HOPParticipantInfo
                                                                                   .getCbcId());

            HOPConversationEvent event = HOPConversationEvent.newContactsChangeEvent(
                getConversationId(),
                getCurrentCbcId(),
                HOPModelUtils.getUserIds(users), null);
            onNewEvent(event);
            HOPDataManager.getInstance().updateConversation(this);
            HOPConversationManager.getInstance().notifyContactsChanged(this);
        }
    }

    public void removeParticipants(List<HOPContact> users) {
        if (mConvThread != null) {
            sendMessage(HOPSystemMessage.getContactsRemovedSystemMessage(
                HOPModelUtils.getPeerUris(users)), false);
            HOPModelUtils.removeParticipantsFromThread(mConvThread, users);
        } else {
            long oldCbcId = HOPParticipantInfo.getCbcId();

            HOPParticipantInfo.getParticipants().removeAll(users);
            HOPParticipantInfo.setCbcId(HOPModelUtils.getWindowId(HOPParticipantInfo.getParticipants()));
            HOPConversationManager.getInstance().onConversationParticipantsChange(this, oldCbcId,
                                                                               HOPParticipantInfo
                                                                                   .getCbcId());
            HOPConversationEvent event = HOPConversationEvent.newContactsChangeEvent(
                getConversationId(),
                getCurrentCbcId(),
                null,
                HOPModelUtils.getUserIds(users));
            onNewEvent(event);
            HOPDataManager.getInstance().updateConversation(this);

            HOPConversationManager.getInstance().notifyContactsChanged(this);
        }
    }

    void onMessageReceived(OPConversationThread thread, OPMessage message) {
            OPContact opContact = message.getFrom();
            HOPContact sender = HOPDataManager.getInstance().
                getUserByPeerUri(opContact.getPeerURI());
        if (message.getMessageType().equals(OPMessage.TYPE_TEXT)) {
            if (sender == null) {
                List<OPIdentityContact> contacts = thread.getIdentityContactList(opContact);
                sender = HOPDataManager.getInstance().getUser(opContact, contacts);
            }
            message.setSenderId(sender.getUserId());
            if (!TextUtils.isEmpty(message.getReplacesMessageId())) {
                HOPDataManager.getInstance().updateMessage(message, this);
            } else {
                HOPDataManager.getInstance().saveMessage(message, conversationId,
                                                        HOPParticipantInfo);
            }
            selectActiveThread(thread);
        }
    }

    /**
     * @return ID array of the participants, excluding yourself
     */
    public long[] getParticipantIDs() {
        return HOPModelUtils.getUserIds(HOPParticipantInfo.getParticipants());
    }

    /**
     *  This function should be called when particpants is added/removed from UI.
     *
     * @param users new users list
     */
    public void onContactsChanged(List<HOPContact> users) {
        switch (type){
        case thread:{
            long cbcId = HOPModelUtils.getWindowId(users);
            if (cbcId == getCurrentCbcId()) {
                return;
            }

            List<HOPContact> addedUsers = new ArrayList<>();
            List<HOPContact> removedUsers = new ArrayList<>();
            HOPModelUtils.findChangedUsers(HOPParticipantInfo.getParticipants(), users,
                                           addedUsers,
                                           removedUsers);
            if (!addedUsers.isEmpty()) {
                addParticipants(addedUsers);
            }
            if (!removedUsers.isEmpty()) {
                removeParticipants(removedUsers);
            }
        }
        break;
        }
    }

    /**
     * Find the added/deleted contacts and inform listener.
     *
     * @param conversationThread
     */
    public boolean onContactsChanged(OPConversationThread conversationThread) {

        List<HOPContact> currentUsers = HOPParticipantInfo.getParticipants();
        List<HOPContact> newUsers = conversationThread.getParticipantInfo().getParticipants();
        List<HOPContact> addedUsers = new ArrayList<HOPContact>();
        List<HOPContact> deletedUsers = new ArrayList<HOPContact>();

        CollectionUtils.diff(currentUsers, newUsers, addedUsers, deletedUsers);
        if (addedUsers.isEmpty() && deletedUsers.isEmpty()) {
            Log.e(TAG, "onContactsChanged called when no contacts change");
            return false;
        }

        long oldCbcId = HOPParticipantInfo.getCbcId();
        mConvThread = conversationThread;
        HOPParticipantInfo = conversationThread.getParticipantInfo();
        HOPConversationManager.getInstance().onConversationParticipantsChange(this, oldCbcId,
                                                                           HOPParticipantInfo
                                                                               .getCbcId());
        HOPConversationEvent event = HOPConversationEvent.
            newContactsChangeEvent(getConversationId(),
                                   getCurrentCbcId(),
                                   HOPModelUtils.getUserIds(addedUsers),
                                   HOPModelUtils.getUserIds(deletedUsers));
        onNewEvent(event);
        HOPDataManager.getInstance().updateConversation(this);
        return true;
    }

    public static HOPConversation onConversationParticipantsChanged(HOPConversation conversation,
                                                            List<HOPContact> newParticipants) {
        return HOPConversationManager.getInstance().onConversationParticipantsChanged(conversation,newParticipants);
    }


    /**
     * @return
     */
    public GroupChatMode getType() {
        // TODO Auto-generated method stub
        return type;
    }

    /**
     * Set the database record id of this conversation.
     *
     * @param id
     */
    public void setId(long id) {
        _id = id;
    }

    /**
     * This is the conversationId used to identify a unique conversation
     *
     * @return
     */
    public String getConversationId() {
        if(conversationId==null && mConvThread!=null){
            conversationId = mConvThread.getConversationId();
        }
        return conversationId;
    }

    /**
     * THis fucntion calls ConversationThread.markAllMessagesRead and update database. This
     * function should be call on chat view starts and when a new message received when the chat
     * view is open
     */
    public void markAllMessagesRead() {
        HOPDataManager.getInstance().markMessagesRead(this);
        if (mConvThread != null) {
            mConvThread.markAllMessagesRead();
        }
    }

    /**
     * Set the particpants's composing status.This function should be called when particpants start typing,
     * pause typing, view shows, view hides.
     *
     * @param status
     */
    public void setComposingStatus(ComposingStates status) {
        if (mConvThread != null) {
            mConvThread.setStatusInThread(status);
        }
    }

    public void onMessagePushed(String messageId,HOPContact user){

    }
    public void onMessagePushFailure(String messageId,HOPContact user){

    }
}
