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

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.utils.CollectionUtils;
import com.openpeer.sdk.utils.OPModelUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;

/**
 * A session represents exact state of a conversation thread.
 */
public class OPConversation extends Observable {
    private static final String TAG = OPConversation.class.getSimpleName();

    // So if Alice and Bob, Eric in group chat, Alice then added Mike, a new
    // session is created but from Alice point of view,
    // there's only one group chat and when we construct the chat history after
    // restart,
    private OPConversationThread mConvThread;// the active thread being used

    private String lastReadMessageId;
    private OPMessage mLastMessage;
    private Hashtable<String, OPMessage> mMessageDeliveryQueue;
    private OPConversationEvent mLastEvent;

    private List<SessionListener> mSessionListeners = new ArrayList<SessionListener>();
    //try to keep the data fields correspond to database columns
    private long _id;// database id
    private String conversationId = "";
    private String topic;
    private boolean mDisabled;

    public boolean isDisabled() {
        return mDisabled;
    }

    public ParticipantInfo getParticipantInfo() {
        return participantInfo;
    }

    public void setParticipantInfo(ParticipantInfo participantInfo) {
        this.participantInfo = participantInfo;
    }

    ParticipantInfo participantInfo;
    private GroupChatMode type;

    public OPConversation(ParticipantInfo participantInfo, String conversationId, GroupChatMode mode) {
        this.conversationId = conversationId;
        type = mode;
        this.participantInfo = participantInfo;
    }

    public long save() {
        _id = OPDataManager.getInstance().saveConversation(this);
        return _id;
    }

    public void registerListener(SessionListener listener) {
        synchronized (mSessionListeners) {
            Log.d(TAG, "registerListener " + listener);
            mSessionListeners.add(listener);
        }
    }

    public void unregisterListener(SessionListener listener) {
        synchronized (mSessionListeners) {
            Log.d(TAG, "unregisterListener " + listener);
            mSessionListeners.remove(listener);
        }
    }

    public void setType(GroupChatMode type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
        sendSystemMessage(topic);
    }

    public void setDisabled(boolean disabled){
        mDisabled=disabled;
        OPDataManager.getInstance().updateConversation(this);
    }

    public long getId() {
        return _id;
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
                OPDataManager.getInstance().updateMessage(message, this);
            } else {
                OPDataManager.getInstance().saveMessage(message, conversationId, participantInfo);

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
        return CallManager.getInstance().findCallByCbcId(participantInfo.getCbcId());
    }

    /**
     * If an session existed for an incoming message and thread is null, set thread.
     *
     * @param thread
     */
    public void setThread(OPConversationThread thread) {
        mConvThread = thread;
        if(conversationId==null){
            conversationId=mConvThread.getConversationId();
        }
    }

    public OPConversationThread getThread(boolean createIfNo) {
        if (mConvThread == null && createIfNo) {
            mConvThread = ConversationManager.getInstance().getThread(type, conversationId, participantInfo,createIfNo);
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

    public void getSessionId() {
    }

    public long getCurrentWindowId() {
        return participantInfo.getCbcId();
    }

    public OPConversationEvent getLastEvent() {
        return mLastEvent;
    }

    public void onNewEvent(OPConversationEvent event) {
        OPDataManager.getInstance().saveConversationEvent(event);
        mLastEvent = event;
    }

    private void addContactsToThread(List<OPUser> users) {
        OPModelUtils.addParticipantsToThread(mConvThread,users);
    }

    public OPCall placeCall(OPUser user,
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
    public List<OPUser> getParticipants() {
        return participantInfo.getParticipants();
    }

    public void setCbcId(long cbcId) {
        participantInfo.setCbcId(cbcId);
    }

    public void setParticipants(List<OPUser> participants) {
        participantInfo.setUsers(participants);
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

    private interface MessageDeliveryCallback {
        public void onMessageDeliveryStateChange(String messageId,
                                                 MessageDeliveryStates state);
    }

    public boolean isForUsers(List<OPUser> users) {
        return getCurrentWindowId() == OPModelUtils.getWindowId(users);
    }

    public void addParticipants(List<OPUser> users) {
        if (mConvThread != null) {
            addContactsToThread(users);
        } else {
            long oldCbcId = participantInfo.getCbcId();
            participantInfo.addUsers(users);
            participantInfo.setCbcId(OPModelUtils.getWindowId(participantInfo.getParticipants()));
            ConversationManager.getInstance().onConversationParticipantsChange(this, oldCbcId,
                                                                               participantInfo
                                                                                   .getCbcId());

            OPConversationEvent event = OPConversationEvent.newContactsChangeEvent(
                getConversationId(),
                getCurrentWindowId(),
                OPModelUtils.getUserIds(users), null);
            onNewEvent(event);
            OPDataManager.getInstance().updateConversation(this);
            notifyContactsChanged();
        }
    }

    public void removeParticipants(List<OPUser> users) {
        if (mConvThread != null) {
            sendMessage(SystemMessage.getContactsRemovedSystemMessage(
                OPModelUtils.getPeerUris(users)), false);
            OPModelUtils.removeParticipantsFromThread(mConvThread, users);
        } else {
            long oldCbcId = participantInfo.getCbcId();

            participantInfo.getParticipants().removeAll(users);
            participantInfo.setCbcId(OPModelUtils.getWindowId(participantInfo.getParticipants()));
            ConversationManager.getInstance().onConversationParticipantsChange(this, oldCbcId,
                                                                               participantInfo
                                                                                   .getCbcId());
            OPConversationEvent event = OPConversationEvent.newContactsChangeEvent(
                getConversationId(),
                getCurrentWindowId(),
                null,
                OPModelUtils.getUserIds(users));
            onNewEvent(event);
            OPDataManager.getInstance().updateConversation(this);

            notifyContactsChanged();
        }
    }

    public void onMessageReceived(OPConversationThread thread, OPMessage message) {
        if (message.getMessageType().equals(OPMessage.TYPE_TEXT)) {
            OPContact opContact = message.getFrom();
            OPUser user = OPDataManager.getInstance().
                getUserByPeerUri(opContact.getPeerURI());
            if (user == null) {
                List<OPIdentityContact> contacts = thread.getIdentityContactList(opContact);
                user = OPDataManager.getInstance().getUser(opContact, contacts);
            }
            message.setSenderId(user.getUserId());
            if (!TextUtils.isEmpty(message.getReplacesMessageId())) {
                OPDataManager.getInstance().updateMessage(message, this);
            } else {
                if (!mSessionListeners.isEmpty()) {
                    message.setRead(true);
                    //TODO: decide if this shoudl be done in listener
                }

                OPDataManager.getInstance().saveMessage(message, conversationId,
                                                        participantInfo);
                // TODO: Now notify observer

            }
            selectActiveThread(thread);
        } else if (message.getMessageType().equals(OPMessage.TYPE_JSON_SYSTEM_MESSAGE)) {
            SystemMessage systemMessage = SystemMessage.parseSystemMessage(message.getMessage());
            if (systemMessage.getSystemObject() instanceof CallSystemMessage) {
                CallSystemMessage callSystemMessage = (CallSystemMessage) systemMessage
                    .getSystemObject();
                CallManager.getInstance().
                    handleCallSystemMessage(callSystemMessage, getConversationId(),
                                            message.getTime().toMillis(false));

            } else if (systemMessage.getSystemObject() instanceof ContactsRemovedSystemMessage) {
                ContactsRemovedSystemMessage callSystemMessage = (ContactsRemovedSystemMessage)
                    systemMessage
                    .getSystemObject();
                String selfPeerUri = OPDataManager.getInstance().getCurrentUser().getPeerUri();
                for (String peerUri : callSystemMessage.getContactsRemoved()) {
                    if (peerUri.equals(selfPeerUri)) {
                        setDisabled(true);

                        notifyContactsChanged();
                    }
                }
            }
        }
    }

    /**
     * @return ID array of the participants, excluding yourself
     */
    public long[] getParticipantIDs() {
        return OPModelUtils.getUserIds(participantInfo.getParticipants());
    }

    /**
     *  This function should be called when particpants is added/removed from UI.
     *
     * @param users new users list
     */
    public void onContactsChanged(List<OPUser> users) {
        switch (type){
        case thread:{
            long cbcId = OPModelUtils.getWindowId(users);
            if (cbcId == getCurrentWindowId()) {
                return;
            }

            List<OPUser> addedUsers = new ArrayList<>();
            List<OPUser> removedUsers = new ArrayList<>();
            OPModelUtils.findChangedUsers(participantInfo.getParticipants(), users,
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

        List<OPUser> currentUsers = participantInfo.getParticipants();
        List<OPUser> newUsers = conversationThread.getParticipantInfo().getParticipants();
        List<OPUser> addedUsers = new ArrayList<OPUser>();
        List<OPUser> deletedUsers = new ArrayList<OPUser>();

        CollectionUtils.diff(currentUsers, newUsers, addedUsers, deletedUsers);
        if (addedUsers.isEmpty() && deletedUsers.isEmpty()) {
            Log.e(TAG, "onContactsChanged called when no contacts change");
            return false;
        }

        long oldCbcId = participantInfo.getCbcId();
        mConvThread = conversationThread;
        participantInfo = conversationThread.getParticipantInfo();
        ConversationManager.getInstance().onConversationParticipantsChange(this, oldCbcId,
                                                                           participantInfo
                                                                               .getCbcId());
        OPConversationEvent event = OPConversationEvent.
            newContactsChangeEvent(getConversationId(),
                                   getCurrentWindowId(),
                                   OPModelUtils.getUserIds(addedUsers),
                                   OPModelUtils.getUserIds(deletedUsers));
        onNewEvent(event);
        OPDataManager.getInstance().updateConversation(this);
        notifyContactsChanged();
        return true;
    }

    void notifyContactsChanged() {
        synchronized (mSessionListeners) {
            for (SessionListener listener : mSessionListeners) {
                listener.onContactsChanged();
            }
        }
    }

    public void onContactComposingStateChanged(ComposingStates state,
                                               OPContact contact) {
        OPUser user = OPDataManager.getInstance().getUserByPeerUri(contact.getPeerURI());
        if (user == null) {
            Log.e(TAG, "onContactComposingStateChanged couldn't find particpants");
            return;
        }
        synchronized (mSessionListeners) {
            for (SessionListener listener : mSessionListeners) {
                listener.onContactComposingStateChanged(state, user);
            }
        }
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

    public void setConversationId(String id) {
        this.conversationId = id;
    }

    /**
     * @param call
     * @param state
     */
    public void onCallStateChanged(OPCall call, CallStates state) {
    }

    /**
     * THis fucntion calls ConversationThread.markAllMessagesRead and update database. This
     * function should be call on chat view starts and when a new message received when the chat
     * view is open
     */
    public void markAllMessagesRead() {
        OPDataManager.getInstance().markMessagesRead(this);
        if (mConvThread != null) {
            mConvThread.markAllMessagesRead();
        }
    }

    /**
     * Set the particpants's composing status.This function should be called when particpants start typing,
     * pausing typing, view shows, view hides.
     *
     * @param status
     */
    public void setComposingStatus(ComposingStates status) {
        if (mConvThread != null) {
            mConvThread.setStatusInThread(status);
        }
    }

    public boolean isShowing() {
        return mSessionListeners != null && mSessionListeners.size() > 0;
    }

    public void onMessagePushed(String messageId,OPUser user){

    }
    public void onMessagePushFailure(String messageId,OPUser user){

    }
}
