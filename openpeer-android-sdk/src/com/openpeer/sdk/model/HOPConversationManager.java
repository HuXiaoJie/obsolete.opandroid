/*
 * ******************************************************************************
 *  *
 *  *  Copyright (c) 2014 , Hookflash Inc.
 *  *  All rights reserved.
 *  *
 *  *  Redistribution and use in source and binary forms, with or without
 *  *  modification, are permitted provided that the following conditions are met:
 *  *
 *  *  1. Redistributions of source code must retain the above copyright notice, this
 *  *  list of conditions and the following disclaimer.
 *  *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  *  this list of conditions and the following disclaimer in the documentation
 *  *  and/or other materials provided with the distribution.
 *  *
 *  *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  *
 *  *  The views and conclusions contained in the software and documentation are those
 *  *  of the authors and should not be interpreted as representing official policies,
 *  *  either expressed or implied, of the FreeBSD Project.
 *  ******************************************************************************
 */

package com.openpeer.sdk.model;

import android.text.TextUtils;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.ContactConnectionStates;
import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPConversationThreadDelegate;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.app.HOPSettingsHelper;
import com.openpeer.sdk.utils.HOPModelUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class HOPConversationManager implements OPConversationThreadDelegate {
    private static HOPConversationManager instance;
    private Hashtable<Long, HOPConversation> cbcIdToConversationTable;
    private Hashtable<String, HOPConversation> conversationTable;//conversationId to conversation
    private HOPConversationDelegate mHOPConversationDelegate;

    public static HOPConversationManager getInstance() {
        if (instance == null) {
            instance = new HOPConversationManager();
        }
        return instance;
    }

    private HOPConversationManager() {
    }

    public void registerDelegate(HOPConversationDelegate listener) {
        mHOPConversationDelegate = listener;
    }

    public void unregisterDelegate(HOPConversationDelegate listener) {
        mHOPConversationDelegate = null;
    }

    void cacheCbcToConversation(long cbcId, HOPConversation conversation) {
        if (cbcIdToConversationTable == null) {
            cbcIdToConversationTable = new Hashtable<>();
        }
        cbcIdToConversationTable.put(cbcId, conversation);
    }

    void cacheConversation(String conversationId, HOPConversation conversation) {
        if (conversationTable == null) {
            conversationTable = new Hashtable<>();
        }
        if (conversationId == null) {
            conversationId = conversation.getConversationId();
        }
        conversationTable.put(conversationId, conversation);
    }

    void onConversationParticipantsChange(HOPConversation conversation, long oldCbcId,
                                          long newCbcId) {
        if (oldCbcId != 0 && cbcIdToConversationTable != null) {
            cbcIdToConversationTable.remove(oldCbcId);
        }
        cacheCbcToConversation(newCbcId, conversation);
    }

    HOPConversation getConversation(OPConversationThread thread, boolean createNew) {
        OPConversationThread t = getCachedThread(thread.getThreadID());
        thread = t == null ? thread : t;
        HOPConversation conversation = getConversation(thread.getConverationType(),
                                                       thread.getParticipantInfo(),
                                                       thread.getConversationId(), false);

        if (conversation == null) {
            if (createNew) {
                conversation = new HOPConversation(thread.getParticipantInfo(),
                                                   thread.getConversationId(),
                                                   thread.getConverationType()
                );
                conversation.setThread(thread);
                cacheCbcToConversation(thread.getParticipantInfo().getCbcId(), conversation);
                cacheConversation(thread.getConversationId(), conversation);
                conversation.save();
            }
        } else if (!thread.getConversationId().equals(conversation.getConversationId())) {
            cacheConversation(thread.getConversationId(), conversation);
        }
        if (conversation != null) {
            conversation.setThread(thread);
        }
        return conversation;
    }

    public HOPConversation getConversation(GroupChatMode type,
                                           HOPParticipantInfo HOPParticipantInfo,
                                           String conversationId,
                                           boolean createNew) {
        HOPConversation conversation = null;
        switch (type){
        case contact:{
            long cbcId = HOPParticipantInfo.getCbcId();
            conversation = getConversationByCbcId(cbcId);
            if (conversation == null || conversation.getType() != GroupChatMode.contact) {
                conversation = HOPDataManager.getInstance().getConversation
                    (type, HOPParticipantInfo, conversationId);
                if (conversation != null) {
                    conversation.setParticipants(HOPParticipantInfo.getParticipants());
                    if (!conversation.amIRemoved()) {
                        //For contact based conversation, teh conversation id might be different.
                        conversation.setThread(getThread(type,
                                                         conversationId,
                                                         HOPParticipantInfo,
                                                         true));
                    }
                    cacheCbcToConversation(HOPParticipantInfo.getCbcId(), conversation);

                    cacheConversation(conversationId, conversation);
                }
            }
        }
        break;
        case thread:{
            if (conversationId != null) {
                conversation = getConversationById(conversationId);

                if (conversation == null) {
                    conversation = HOPDataManager.getInstance().getConversation
                        (type, HOPParticipantInfo, conversationId);
                    if (conversation != null) {
                        conversation.setThread(getThread(type, conversationId,
                                                         HOPParticipantInfo,
                                                         true));

                        if (conversation.getCurrentCbcId() != HOPParticipantInfo.getCbcId()) {
                            conversation.setParticipantInfo(HOPParticipantInfo);
                        }
                        cacheCbcToConversation(HOPParticipantInfo.getCbcId(), conversation);
                        cacheConversation(conversationId, conversation);
                    }
                }
            }
        }
        break;
        default:
            return null;
        }
        if (conversation == null && createNew) {
            conversation = new HOPConversation(HOPParticipantInfo, conversationId, type);

            conversation.setThread(getThread(type, conversationId, HOPParticipantInfo, true));
            cacheCbcToConversation(HOPParticipantInfo.getCbcId(), conversation);
            if (!TextUtils.isEmpty(conversation.getConversationId())) {
                cacheConversation(conversation.getConversationId(), conversation);
            }
            conversation.save();
        }
        return conversation;
    }

    public HOPConversation getConversationById(String id) {
        if (conversationTable == null) {
            return null;
        }
        return conversationTable.get(id);
    }

    HOPConversation getConversationByCbcId(long id) {
        if (cbcIdToConversationTable != null) {
            return cbcIdToConversationTable.get(id);
        }
        return null;
    }

    //Start of thread management
    private Hashtable<Long, OPConversationThread> mCbcToThreads;
    private Hashtable<String, OPConversationThread> mThreads;

    void cacheCbcToThread(long cbcId, OPConversationThread thread) {
        if (mCbcToThreads == null) {
            mCbcToThreads = new Hashtable<>();
        }
        mCbcToThreads.put(cbcId, thread);
    }

    void cacheThread(OPConversationThread thread) {
        if (mThreads == null) {
            mThreads = new Hashtable<>();
        }
        mThreads.put(thread.getThreadID(), thread);
    }

    private OPConversationThread getCachedThread(String threadId) {
        if (mThreads != null) {
            return mThreads.get(threadId);
        } else {
            OPLogger.error(OPLogLevel.LogLevel_Basic, "getCachedThread Weird! thread not cached");
            return null;
        }
    }

    public OPConversationThread getThread(GroupChatMode conversationType, String conversationId,
                                          HOPParticipantInfo participantInfo,
                                          boolean createNew) {
        if (!HOPAccount.isAccountReady()) {
            return null;
        }

        OPConversationThread thread = null;
        if (!TextUtils.isEmpty(conversationId)) {
            thread = getCachedThread(conversationId);
        }
        String metaData = ThreadMetaData.newMetaData(conversationType.toString()).toJsonBlob();
        if (thread == null && createNew) {

            thread = OPConversationThread.create(
                HOPAccount.currentAccount().getAccount(),
                HOPAccount.currentAccount().identityContacts(),
                HOPModelUtils.getProfileInfo(participantInfo.getParticipants()),
                conversationId,
                metaData);
            thread.setParticipantInfo(participantInfo);

            cacheThread(thread);
            cacheCbcToThread(participantInfo.getCbcId(), thread);
        }
        return thread;
    }

    void quitConversation(boolean deleteHistory) {
        //TODO: implement proper logic
    }

    void notifyContactsChanged(HOPConversation conversation) {
        mHOPConversationDelegate.onConversationContactsChanged(conversation);
    }

    //Beginning of OPConversationThreadDelegate

    @Override
    public void onConversationThreadNew(
        OPConversationThread thread) {
        List<HOPContact> participants = HOPModelUtils.getParticipantsOfThread(thread);
        long cbcId = HOPModelUtils.getWindowId(participants);
        thread.setParticipantInfo(new HOPParticipantInfo(cbcId, participants));

        HOPDataManager.getInstance().saveParticipants(cbcId, participants);
        OPLogger.debug(OPLogLevel.LogLevel_Detail, "onConversationThreadNew caching new thread id" +
            " " + thread.getThreadID() + " cbcId " + cbcId);
        cacheThread(thread);
        cacheCbcToThread(cbcId, thread);
    }


    @Override
    public void onConversationThreadContactsChanged(OPConversationThread thread) {

        HOPDataManager.getInstance().
            saveParticipants(thread.getParticipantInfo().getCbcId(),
                             thread.getParticipantInfo().getParticipants());

        OPConversationThread oldThread = getCachedThread(thread.getThreadID());
        if (oldThread != null) {
            long oldCbcId = oldThread.getParticipantInfo().getCbcId();
            HOPConversation conversation = HOPConversationManager.getInstance().
                getConversation(oldThread, true);
            OPLogger.debug(OPLogLevel.LogLevel_Detail,
                           "onConversationThreadContactsChanged find old thread cbcId " + oldCbcId);
            if (conversation != null) {
                conversation.setDisabled(amIRemoved(thread));
                if (conversation.onContactsChanged(thread)) {
                    notifyContactsChanged(conversation);
                }
            }
            if (oldCbcId != 0) {
                mCbcToThreads.remove(oldCbcId);
            }

        } else {
            OPLogger.debug(OPLogLevel.LogLevel_Detail, "onConversationThreadContactsChanged " +
                "couldn't find cached thread for " + thread.getThreadID());
        }

        cacheThread(thread);
    }

    public HOPConversation onConversationParticipantsChanged(HOPConversation conversation,
                                                             List<HOPContact> newParticipants) {
        HOPParticipantInfo mHOPParticipantInfo = new HOPParticipantInfo(
            HOPModelUtils.getWindowId(newParticipants),
            newParticipants);
        HOPConversation newConversation = conversation;
        switch (HOPSettingsHelper.getInstance().getGroupChatMode()){
        case contact:{
            newConversation = HOPConversationManager.getInstance().
                getConversation(GroupChatMode.contact, mHOPParticipantInfo, null, true);
        }
        break;
        case thread:{
            if (conversation.getType() == GroupChatMode.contact) {
                newConversation = HOPConversationManager.getInstance().
                    getConversation(GroupChatMode.thread, mHOPParticipantInfo, null, true);
            } else {
                long cbcId = HOPModelUtils.getWindowId(newParticipants);
                if (cbcId != conversation.getCurrentCbcId()) {
                    List<HOPContact> addedUsers = new ArrayList<>();
                    List<HOPContact> removedUsers = new ArrayList<>();
                    HOPModelUtils.findChangedUsers(conversation.getParticipants(), newParticipants,
                                                   addedUsers,
                                                   removedUsers);
                    if (!addedUsers.isEmpty()) {
                        conversation.addParticipants(addedUsers);
                    }
                    if (!removedUsers.isEmpty()) {
                        conversation.removeParticipants(removedUsers);
                    }
                }
            }
        }
        break;
        }
        return newConversation;
    }

    boolean amIRemoved(OPConversationThread thread) {
        for (OPContact contact : thread.getContacts()) {
            if (contact.isSelf()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onConversationThreadContactStatusChanged(
        OPConversationThread conversationThread, OPContact contact) {
        if (contact.isSelf()) {
            OPLogger.error(OPLogLevel.LogLevel_Basic,
                           "weird! onConversationThreadContactStatusChanged for self " + contact
                               .getPeerURI());
        }

        ComposingStates state = conversationThread
            .getContactComposingStatus(contact);
        if (state != null) {
            HOPConversation conversation = HOPConversationManager.getInstance()
                .getConversation(conversationThread, true);
            if (conversation != null) {
                HOPContact user = HOPDataManager.getInstance().getUserByPeerUri(contact
                                                                                    .getPeerURI());
                if (user == null) {
                    return;
                }

                mHOPConversationDelegate.onConversationContactStatusChanged(conversation,
                                                                            state, user);
            }
        }
    }

    @Override
    public void onConversationThreadMessage(
        OPConversationThread conversationThread, String messageID) {
        OPMessage message = conversationThread.getMessageById(messageID);

        if (message.getFrom().isSelf()) {
            OPLogger.debug(
                OPLogLevel.LogLevel_Basic,
                "Weird! received message from myself!"
                    + message.getMessageId()
                    + " messageId "
                    + messageID + " type "
                    + message.getMessageType());

            return;
        }
        HOPConversation conversation = HOPConversationManager.getInstance().getConversation
            (conversationThread, true);
        if (message.getMessageType().equals(OPMessage.TYPE_TEXT)) {
            conversation.onMessageReceived(conversationThread, message);
        }
        mHOPConversationDelegate.onConversationMessage(conversation, message);
    }

    @Override
    public void onConversationThreadMessageDeliveryStateChanged(
        OPConversationThread conversationThread, String messageID,
        MessageDeliveryStates state) {
        HOPDataManager.getInstance().
            updateMessageDeliveryStatus(messageID,
                                        getConversation(conversationThread, true).getId(),
                                        state);
    }

    @Override
    public void onConversationThreadPushMessage(
        OPConversationThread conversationThread,
        final String messageID,
        OPContact contact) {
        HOPConversation conversation = HOPConversationManager.getInstance()
            .getConversation(conversationThread, true);
        OPMessage message = conversationThread.getMessageById(messageID);

        HOPContact user = HOPDataManager.getInstance().getUserByPeerUri(contact.getPeerURI());
        mHOPConversationDelegate.onConversationPushMessage(conversation, message, user);
    }

    @Override
    public void onConversationThreadContactConnectionStateChanged(
        OPConversationThread conversationThread, OPContact contact,
        ContactConnectionStates state) {
        HOPConversation conversation = HOPConversationManager.getInstance()
            .getConversation(conversationThread, true);
        HOPContact user = HOPDataManager.getInstance().getUserByPeerUri(contact.getPeerURI());
        mHOPConversationDelegate.onConversationContactConnectionStateChanged(conversation, user,
                                                                             state);
    }
    //End of OPConversationThreadDelegate

    public static void clearOnSignout() {
        if (instance != null) {
            instance.cbcIdToConversationTable = null;
            instance.conversationTable = null;
        }
    }
}
