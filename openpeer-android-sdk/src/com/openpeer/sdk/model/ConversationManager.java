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

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.ContactConnectionStates;
import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPConversationThreadDelegate;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.utils.OPModelUtils;

import java.util.Hashtable;
import java.util.List;

public class ConversationManager extends OPConversationThreadDelegate {
    private static ConversationManager instance;
    private Hashtable<Long, OPConversation> cbcIdToConversationTable;
    private Hashtable<String, OPConversation> conversationTable;//conversationId to conversation

    public static ConversationManager getInstance() {
        if (instance == null) {
            instance = new ConversationManager();
        }
        return instance;
    }

    private ConversationManager() {
    }

    void cacheCbcToConversation(long cbcId, OPConversation conversation) {
        if (cbcIdToConversationTable == null) {
            cbcIdToConversationTable = new Hashtable<>();
        }
        cbcIdToConversationTable.put(cbcId, conversation);
    }

    void cacheConversation(OPConversation conversation) {
        if (conversationTable == null) {
            conversationTable = new Hashtable<>();
        }
        conversationTable.put(conversation.getConversationId(), conversation);
    }

    void onConversationParticipantsChange(OPConversation conversation, long oldCbcId,
                                          long newCbcId) {
        if (oldCbcId != 0 && cbcIdToConversationTable != null) {
            cbcIdToConversationTable.remove(oldCbcId);
        }
        cacheCbcToConversation(newCbcId, conversation);
    }

    OPConversation getConversation(OPConversationThread thread, boolean createNew) {
        OPConversationThread t = getCachedThread(thread);
        thread = t == null ? thread : t;
        OPConversation conversation = getConversation(thread.getConverationType(),
                                                      thread.getParticipantInfo(),
                                                      thread.getConversationId(), false);

        if (conversation == null && createNew) {
            conversation = new OPConversation(thread.getParticipantInfo(),
                                              thread.getConversationId(),
                                              thread.getConverationType()
            );
            conversation.setThread(thread);
            cacheCbcToConversation(thread.getParticipantInfo().getCbcId(), conversation);
            cacheConversation(conversation);
            conversation.save();
        }
        return conversation;
    }

    public OPConversation getConversation(GroupChatMode type, ParticipantInfo participantInfo,
                                          String conversationId,
                                          boolean createNew) {
        OPConversation conversation = null;
        switch (type){
        case contact:{
            long cbcId = participantInfo.getCbcId();
            conversation = getConversationByCbcId(cbcId);
            if (conversation == null || conversation.getType() != GroupChatMode.contact) {
                conversation = OPDataManager.getInstance().getConversation
                    (type, participantInfo, conversationId);
                if (conversation != null) {
                    conversation.setParticipants(participantInfo.getParticipants());
                    if (!conversation.isDisabled()) {
                        conversation.setThread(getThread(type,
                                                         conversation.getConversationId(),
                                                         participantInfo,
                                                         true));
                    }
                    cacheCbcToConversation(participantInfo.getCbcId(), conversation);
                    cacheConversation(conversation);
                }
            }
        }
        break;
        case thread:{
            if (conversationId != null) {
                conversation = getConversationById(conversationId);

                if (conversation == null) {
                    conversation = OPDataManager.getInstance().getConversation
                        (type, participantInfo, conversationId);
                    if (conversation != null) {
                        conversation.setThread(getThread(type, conversation.getConversationId(),
                                                         participantInfo,
                                                         true));

                        if (conversation.getCurrentWindowId() != participantInfo.getCbcId()) {
                            conversation.setParticipantInfo(participantInfo);
                        }
                        cacheCbcToConversation(participantInfo.getCbcId(), conversation);
                        cacheConversation(conversation);
                    }
                }
            }
        }
        break;
        default:
            return null;
        }
        if (conversation == null && createNew) {
            conversation = new OPConversation(participantInfo, conversationId, type);

            conversation.setThread(getThread(type, conversationId, participantInfo, true));
            cacheCbcToConversation(participantInfo.getCbcId(), conversation);
            cacheConversation(conversation);
            conversation.save();
        }
        return conversation;
    }

    public OPConversation getConversationById(String id) {
        if (conversationTable == null) {
            return null;
        }
        return conversationTable.get(id);
    }

    public OPConversation getConversationByCbcId(long id) {
        if (cbcIdToConversationTable != null) {
            return cbcIdToConversationTable.get(id);
        }
        return null;
    }

    //Start of thread management
    PushServiceInterface mPushService;
    private Hashtable<Long, OPConversationThread> mCbcToThreads;
    private Hashtable<String, OPConversationThread> mThreads;

    public void registerPushService(PushServiceInterface pushService) {
        mPushService = pushService;
    }

    public void unregisterPushService() {
        mPushService = null;
    }

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

    OPConversationThread getCachedThread(OPConversationThread thread) {
        if (mThreads != null) {
            return mThreads.get(thread.getThreadID());
        } else {
            OPLogger.error(OPLogLevel.LogLevel_Basic, "getCachedThread Weird! thread not cached");
            return null;
        }
    }

    public OPConversationThread getThread(GroupChatMode conversationType, String conversationId,
                                          ParticipantInfo participantInfo, boolean createNew) {
        OPConversationThread thread = null;
//        switch (mode){
//        case contact:{
//            thread = findThreadByCbcId(participantInfo.getCbcId());
//            break;
//        }
//        case thread:{
//            if (mThreads != null) {
//                thread = mThreads.get(conversationId);
//            }
//            break;
//        }
//        }
        String metaData = ThreadMetaData.newMetaData(conversationType.toString()).toJsonBlob();
        if (thread == null && createNew) {
            thread = OPConversationThread.create(
                OPDataManager.getInstance().getSharedAccount(),
                OPDataManager.getInstance().getSelfContacts(),
                OPModelUtils.getProfileInfo(participantInfo.getParticipants()),
                conversationId,
                metaData);
            thread.setParticipantInfo(participantInfo);

            cacheThread(thread);
            cacheCbcToThread(participantInfo.getCbcId(), thread);
        }
        return thread;
    }


    //Beginning of OPConversationThreadDelegate

    @Override
    public void onConversationThreadNew(
        OPConversationThread thread) {
        List<OPUser> participants = OPModelUtils.getParticipantsOfThread(thread);
        long cbcId = OPModelUtils.getWindowId(participants);
        thread.setParticipantInfo(new ParticipantInfo(cbcId, participants));

        OPDataManager.getInstance().saveParticipants(cbcId, participants);
        OPLogger.debug(OPLogLevel.LogLevel_Detail, "onConversationThreadNew caching new thread id" +
            " " + thread.getThreadID() + " cbcId " + cbcId);
        cacheThread(thread);
        cacheCbcToThread(cbcId, thread);
    }

    @Override
    public void onConversationThreadContactsChanged(OPConversationThread thread) {

        OPDataManager.getInstance().
            saveParticipants(thread.getParticipantInfo().getCbcId(),
                             thread.getParticipantInfo().getParticipants());

        OPConversationThread oldThread = getCachedThread(thread);
        if (oldThread != null) {
            long oldCbcId = oldThread.getParticipantInfo().getCbcId();
            OPConversation conversation = ConversationManager.getInstance().
                getConversation(oldThread, false);
            OPLogger.debug(OPLogLevel.LogLevel_Detail,
                           "onConversationThreadContactsChanged find old thread cbcId " + oldCbcId);
            if (conversation != null) {
                conversation.setDisabled(amIRemoved(thread));
                conversation.onContactsChanged(thread);
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
            OPConversation conversation = ConversationManager.getInstance()
                .getConversation(conversationThread, true);
            if (conversation != null) {
                conversation.onContactComposingStateChanged(state, contact);
            }
        }
    }

    @Override
    public void onConversationThreadMessage(
        OPConversationThread conversationThread, String messageID) {
        OPMessage message = conversationThread
            .getMessageById(messageID);

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
        OPConversation conversation = ConversationManager.getInstance().getConversation
            (conversationThread, true);
        conversation.onMessageReceived(conversationThread, message);
    }

    @Override
    public void onConversationThreadMessageDeliveryStateChanged(
        OPConversationThread conversationThread, String messageID,
        MessageDeliveryStates state) {
        OPDataManager.getInstance().
            updateMessageDeliveryStatus(messageID,
                                        conversationThread.getConversationId(),
                                        state);
    }

    @Override
    public void onConversationThreadPushMessage(
        OPConversationThread conversationThread,
        final String messageID,
        OPContact contact) {
        if (mPushService != null) {
            final OPMessage message = conversationThread
                .getMessageById(messageID);

            OPConversation conversation = ConversationManager.getInstance()
                .getConversation(conversationThread, true);
            OPUser user = OPDataManager.getInstance().getUserByPeerUri(contact.getPeerURI());
            mPushService.onConversationThreadPushMessage(conversation, message, user);
        }
    }

    @Override
    public void onConversationThreadContactConnectionStateChanged(
        OPConversationThread conversationThread, OPContact contact,
        ContactConnectionStates state) {
    }
    //End of OPConversationThreadDelegate

    public static void clearOnSignout() {
        if (instance != null) {
            instance.cbcIdToConversationTable = null;
            instance.conversationTable = null;
        }
    }
}
