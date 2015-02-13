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

import android.util.Log;

import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.HOPParticipantInfo;
import com.openpeer.sdk.model.ThreadMetaData;
import com.openpeer.sdk.utils.HOPModelUtils;

import java.util.List;

public class OPConversationThread {

    private static final String TAG = OPConversationThread.class
            .getSimpleName();

    HOPParticipantInfo mHOPParticipantInfo;
    GroupChatMode mConversationType;

    public HOPParticipantInfo getParticipantInfo() {
        if (mHOPParticipantInfo == null) {
            List<HOPContact> users = HOPModelUtils.getParticipantsOfThread(this);
            mHOPParticipantInfo = new HOPParticipantInfo(HOPModelUtils.getWindowId(users), users);
        }
        return mHOPParticipantInfo;
    }

    public void setParticipantInfo(HOPParticipantInfo mParticipants) {
        this.mHOPParticipantInfo = mParticipants;
    }

    public GroupChatMode getConverationType() {
        if (mConversationType == null) {
            String metadata = getMetaData();
            ThreadMetaData threadMetaData = ThreadMetaData.fromJsonBlob(metadata);
            mConversationType = GroupChatMode.valueOf(threadMetaData.getConversationType());
        }
        return mConversationType;
    }
    /**
     * Helper function to make sure required fields are populated.
     * 
     * @param messageID
     * @return
     */
    public OPMessage getMessageById(String messageID) {
        OPMessage message = getMessage(messageID);
        OPContact from = message.getFrom();
        HOPContact user = HOPDataManager.getInstance().getUser(from,
                getIdentityContactList(from));
        message.setSenderId(user.getUserId());
        return message;
    }

    /**
     * Call this method in {@link com.openpeer.jni.OPConversationThreadDelegate#onConversationThreadContactStateChanged} to retrieve the
     * current user composing states
     * 
     * @param contact
     * @return
     */
    public ComposingStates getContactComposingStatus(OPContact contact) {
        Log.d(TAG,
                "getContactComposingStatus for contact " + contact.getPeerURI());
        OPElement element = getContactStatus(contact);
        if (element != null) {
            Log.d(TAG,
                    "getContactComposingStatus element " + element
                            + " toString "
                            + element.convertToString());
            OPComposingStatus composingStatus = OPComposingStatus
                    .extract(element);
            ComposingStates state = composingStatus.getComposingState();
            Log.d(TAG, "getContactComposingStatus state " + state);
            // TODO: remove debugging hard coded value
            // if (state == null)
            // state = ComposingStates.ComposingState_Composing;
            return state;
        } else {
            Log.d(TAG, "getContactComposingStatus received element null");
            return null;
        }
    }

    /**
     * Call this function when user starts typing and stop typing, or any other user status of interest
     * 
     * @param composingState
     */
    public void setStatusInThread(ComposingStates composingState) {
        OPElement element = createEmptyStatus();
        OPComposingStatus status = OPComposingStatus.create(composingState);
        status.insert(element);
        Log.d(TAG, "Inserting status " + element.convertToString());

        Log.d(TAG, "Inserting status " + status.getComposingState());
        setStatusInThread(element);

    }

    // START OF JNI -- DON'T TOUCH THE SIGNATURES!!!
    private long nativeClassPointer;

    public static native String toString(MessageDeliveryStates state);

    public static native String toString(ContactConnectionStates state);

    public static native String toDebugString(OPConversationThread thread,
            boolean includeCommaPrefix);

    public static native OPConversationThread create(OPAccount account,
            List<OPIdentityContact> identityContactsOfSelf,
            List<OPContactProfileInfo> addContacts,
            String threadID, 
            String metaData);

    public  native String getMetaData();
    public static native List<OPConversationThread> getConversationThreads(
            OPAccount account);

    public static native OPConversationThread getConversationThreadByID(
            OPAccount account, String threadID);

    public native long getID();

    public native String getThreadID();

    public native boolean amIHost();

    public native OPAccount getAssociatedAccount();

    public native List<OPContact> getContacts();

    public native List<OPIdentityContact> getIdentityContactList(
            OPContact contact);

    public native ContactConnectionStates getContactConnectionState(
            OPContact contact);

    public native void addContacts(
            List<OPContactProfileInfo> contactProfileInfos);

    public native void removeContacts(List<OPContact> contacts);

    /**
     * sending a message will cause the message to be delivered to all the contacts currently in the conversation
     * 
     * @param messageID
     * @param replacesMessageID
     *            The existing messageId to be replaced
     * @param messageType
     * @param message
     * @param signMessage
     *            whether or not to sign the message
     */
    public native void sendMessage(String messageID, String replacesMessageID,
            String messageType, String message, boolean signMessage);

    private native OPMessage getMessage(String messageID);
    
    public native void getMessageDeliveryState(String messageID, MessageDeliveryStates state);

    public native MessageDeliveryStates getMessageDeliveryState(String messageID);

    private static native OPElement createEmptyStatus();

    private native OPElement getContactStatus(OPContact contact);

    private native void setStatusInThread(OPElement contactStatusInThreadOfSelf);

    public native void markAllMessagesRead();

    public String getConversationId(){return getThreadID();}

    private native void releaseCoreObjects();

    protected void finalize() throws Throwable {

        if (nativeClassPointer != 0) {
            Log.d("output", "Cleaning conversation thread core objects");
            releaseCoreObjects();
        }

        super.finalize();
    }

    // END OF JNI

    @Override
    public boolean equals(Object o) {
        return o instanceof OPConversationThread
                && this.getID() == ((OPConversationThread) o).getID();
    }

}
