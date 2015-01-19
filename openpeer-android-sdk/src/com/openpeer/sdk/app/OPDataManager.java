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
package com.openpeer.sdk.app;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPDownloadedRolodexContacts;
import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.javaapi.OPIdentityLookup;
import com.openpeer.javaapi.OPIdentityLookupInfo;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.javaapi.OPRolodexContact;
import com.openpeer.sdk.datastore.ContentUriResolver;
import com.openpeer.sdk.datastore.DatabaseContracts;
import com.openpeer.sdk.datastore.OPModelCursorHelper;
import com.openpeer.sdk.delegates.OPIdentityDelegateImpl;
import com.openpeer.sdk.delegates.OPIdentityLookupDelegateImpl;
import com.openpeer.sdk.model.CallEvent;
import com.openpeer.sdk.model.CallSystemMessage;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.MessageEditState;
import com.openpeer.sdk.model.MessageEvent;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPConversationEvent;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.ParticipantInfo;

import static com.openpeer.sdk.datastore.DatabaseContracts.COLUMN_PEER_URI;

/**
 * Hold reference to objects that cannot be constructed from database,
 * and manages contacts data change.
 */
public class OPDataManager {
    private static final String TAG = OPDataManager.class.getSimpleName();

    public static String INTENT_CONTACTS_CHANGED = "com.openpeer.contacts_changed";

    private static OPDataManager instance;

    private OPAccount mAccount;
    private Hashtable<Long, OPIdentity> mIdentities;
    private List<OPIdentityContact> mSelfContacts;
    private Context mContext;

    long mCurrentUserId;
    /**
     * Users cache using peer uri as index
     */
    private Hashtable<String, OPUser> mUsers = new Hashtable<String, OPUser>();
    private Hashtable<Long, OPUser> mUsersById = new Hashtable<Long, OPUser>();

    private ContentUriResolver mContentUriProvider;

    Hashtable<String, OPIdentityLookup> mIdentityLookups;

    public void addIdentity(OPIdentity identity) {
        if (mIdentities == null) {
            mIdentities = new Hashtable<Long, OPIdentity>();
        }
        mIdentities.put(identity.getID(), identity);
    }

    public OPIdentity getStoredIdentityById(long id) {
        if (mIdentities == null) {
            return null;
        } else {
            return mIdentities.get(id);
        }
    }

    public static OPDataManager getInstance() {
        if (instance == null) {
            instance = new OPDataManager();
        }
        return instance;
    }

    public void init() {
    }

    /**
     * This function should only be called in AccountState_Ready from OPAccountDelegate. This
     * function update the database
     *
     * @param account the logged in account
     */
    public void setSharedAccount(OPAccount account) {
        mAccount = account;
    }

    public void saveAccount() {
        saveAccount(mAccount);
    }

    public OPAccount getSharedAccount() {
        return mAccount;
    }

    public List<OPIdentityContact> getSelfContacts() {
        List<OPIdentity> identities = mAccount.getAssociatedIdentities();
        mSelfContacts = new ArrayList<OPIdentityContact>();
        for (OPIdentity identity : identities) {
            mSelfContacts.add(identity.getSelfIdentityContact());
        }
        return mSelfContacts;
    }

    public void onDownloadedRolodexContacts(OPIdentity identity) {
        OPDownloadedRolodexContacts downloaded = identity
            .getDownloadedRolodexContacts();
        List<OPRolodexContact> contacts = downloaded.getRolodexContacts();
        if (contacts == null) {
            OPLogger.error(OPLogLevel.LogLevel_Detail,
                           "download rolodex contacts is null for identity "
                               + identity.getIdentityURI());
            return;
        } else if (contacts.isEmpty()) {
            OPLogger.debug(OPLogLevel.LogLevel_Detail,
                           "download rolodex contacts is empty for identity "
                               + identity.getIdentityURI());
            return;
        }
        contacts = saveDownloadedRolodexContacts(identity,
                                                 contacts,
                                                 downloaded.getVersionDownloaded());
        identityLookup(identity, contacts);
    }

    public void identityLookup(OPIdentity identity,
                               List<OPRolodexContact> contacts) {

        OPIdentityLookupDelegateImpl mIdentityLookupDelegate = OPIdentityLookupDelegateImpl
            .getInstance(identity);
        List<OPIdentityLookupInfo> inputLookupList = new ArrayList<OPIdentityLookupInfo>();

        for (OPRolodexContact contact : contacts) {
            OPIdentityLookupInfo ilInfo = new OPIdentityLookupInfo();
            ilInfo.initWithRolodexContact(contact);
            inputLookupList.add(ilInfo);
        }

        OPIdentityLookup identityLookup = OPIdentityLookup.create(
            OPDataManager.getInstance().getSharedAccount(),
            mIdentityLookupDelegate,
            inputLookupList,
            OPSdkConfig.getInstance()
                .getIdentityProviderDomain());//
        // "identity-v1-rel-lespaul-i.hcs.io");
        if (identityLookup != null) {
            if (mIdentityLookups == null) {
                mIdentityLookups = new Hashtable<String, OPIdentityLookup>();
            }
            mIdentityLookups.put(identity.getIdentityURI(), identityLookup);
        }
    }

    public void updateIdentityContacts(String identityUri,
                                       List<OPIdentityContact> iContacts) {

        // Each IdentityContact represents a user. Update user info
        saveIdentityContact(iContacts, identityUri.hashCode());
    }

    public void refreshContacts() {
        List<OPIdentity> identities = mAccount.getAssociatedIdentities();
        for (OPIdentity identity : identities) {

            identity.refreshRolodexContacts();
        }
    }

    public boolean isAccountReady() {
        return mAccount != null
            && mAccount.getState() == AccountStates.AccountState_Ready;
    }


    /**
     * @param url
     * @param lookup
     */
    public void onIdentityLookupCompleted(String url, OPIdentityLookup lookup) {
        List<OPIdentityContact> iContacts = lookup.getUpdatedIdentities();
        if (iContacts != null) {
            updateIdentityContacts(url, iContacts);
        }
        if (mIdentityLookups != null) {
            mIdentityLookups.remove(url);
        }
    }

    public void onSignOut() {
        List<OPIdentity> identities = instance.mAccount
            .getAssociatedIdentities();
        if (mIdentityLookups != null && mIdentityLookups.size() > 0) {
            for (OPIdentityLookup lookup : mIdentityLookups.values()) {
                lookup.cancel();
            }
        }
        for (OPIdentity identity : identities) {
            identity.cancel();
        }

        mAccount.shutdown();
        mContentUriProvider.onSignout();

    }


    /**
     *
     */
    public void afterSignout() {
        OPDataManager.getInstance().onSignOut();
        OPIdentityDelegateImpl.clearAfterSignout();
    }

    public void setContentUriProvider(ContentUriResolver provider) {
        mContentUriProvider = provider;
    }

    public OPUser getCurrentUser() {
        if (getCurrentUserId() != 0) {

            return getUserById(getCurrentUserId());
        }
        return null;
    }

    public long getCurrentUserId() {
        if (mCurrentUserId == 0) {

            Cursor cursor = query(DatabaseContracts.OpenpeerContactEntry.URI_PATH_LOGIN_USER, null,
                                  null, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mCurrentUserId = cursor.getLong(0);
            }
            cursor.close();
        }
        return mCurrentUserId;
    }

    public String getReloginInfo() {
        String selection = DatabaseContracts.AccountEntry.COLUMN_LOGGED_IN + "=1";
        Cursor cursor = query(DatabaseContracts.AccountEntry.TABLE_NAME, null, selection, null);
        if (cursor != null) {
            String reloginInfo = null;
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                reloginInfo = cursor.getString(cursor
                                                   .getColumnIndex(DatabaseContracts.AccountEntry
                                                                       .COLUMN_RELOGIN_INFO));
            }
            cursor.close();
            return reloginInfo;
        } else {
            OPLogger.debug(OPLogLevel.LogLevel_Debug,
                           "getReloginInfo retrieved 0 row");
            return null;
        }
    }

    public String getDownloadedContactsVersion(String identityUri) {
        String version = simpleQueryForString(
            DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME,
            DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_CONTACTS_VERSION,
            DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_URI + "=?",
            new String[]{identityUri});

        return version;
    }


    public String getAvatarUri(long rolodexId, int width, int height) {
        String orderBy = " width-" + width;
        String limit = "1";
        Cursor cursor = query(DatabaseContracts.AvatarEntry.TABLE_NAME,
                              new String[]{DatabaseContracts.AvatarEntry.COLUMN_AVATAR_URI},
                              DatabaseContracts.AvatarEntry.COLUMN_ROLODEX_ID + "=" + rolodexId,
                              null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String url = cursor.getString(0);

            cursor.close();
            return url;
        } else {
            cursor.close();
            return null;
        }
    }

    public List<OPRolodexContact.OPAvatar> getAvatars(long contactId) {
        Cursor cursor = query(DatabaseContracts.AvatarEntry.TABLE_NAME, null,
                              "rolodex_id=" + contactId, null);
        if (cursor != null) {
            List<OPRolodexContact.OPAvatar> avatars = new ArrayList<OPRolodexContact.OPAvatar>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                OPRolodexContact.OPAvatar avatar = new OPRolodexContact.OPAvatar(
                    cursor.getString(cursor
                                         .getColumnIndex(DatabaseContracts.AvatarEntry
                                                             .COLUMN_AVATAR_NAME)),
                    cursor.getString(cursor
                                         .getColumnIndex(DatabaseContracts.AvatarEntry
                                                             .COLUMN_AVATAR_URI)),
                    cursor.getInt(cursor
                                      .getColumnIndex(DatabaseContracts.AvatarEntry.COLUMN_WIDTH)),
                    cursor.getInt(cursor
                                      .getColumnIndex(DatabaseContracts.AvatarEntry
                                                          .COLUMN_HEIGHT)));
                avatars.add(avatar);
                cursor.moveToNext();
            }
            cursor.close();
            return avatars;
        }
        return null;
    }


    public OPMessage getMessage(String messageId) {
        String messageIDs[] = new String[]{messageId};
        OPMessage message = null;
        Cursor cursor = query(DatabaseContracts.MessageEntry.TABLE_NAME, null,
                              DatabaseContracts.MessageEntry.COLUMN_MESSAGE_ID + "=?", messageIDs);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                message = OPModelCursorHelper.messageFromCursor(cursor);
            }
            cursor.close();
        }
        return message;
    }


    public OPUser getUserByPeerUri(String peerUri) {
        OPUser user = mUsers.get(peerUri);
        if (user != null) {
            return user;
        }
        String selection = COLUMN_PEER_URI + "=?";
        String args[] = new String[]{peerUri};
        Cursor cursor = getContentResolver()
            .query(mContentUriProvider.getContentUri(DatabaseContracts.OpenpeerContactEntry
                                                         .URI_PATH_INFO_DETAIL),
                   null, selection, args, null);
        if (cursor.getCount() > 0) {
            user = fromDetailCursor(cursor);
            cacheUser(user);
            return user;
        } else {
            return null;
        }
    }


    public OPUser getUser(OPContact contact,
                          List<OPIdentityContact> identityContacts) {
        String peerUri = contact.getPeerURI();
        OPUser user = mUsers.get(peerUri);
        if (user != null) {
            return user;
        }
        user = new OPUser(contact, identityContacts);
        long contactRecordId = 0;
        if (identityContacts == null || identityContacts.size() == 0) {
            OPLogger.error(OPLogLevel.LogLevel_Basic,
                           "No identity attached to contact " + contact.getPeerURI());
            throw new RuntimeException("No identity attached to contact "
                                           + contact.getPeerURI());
        }
        // find the existing record of the account
        String stableId = identityContacts.get(0).getStableID();
        String where = DatabaseContracts.OpenpeerContactEntry.COLUMN_STABLE_ID + "=?" + " or "
            + DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERURI + "=?";
        String args[] = new String[]{stableId, peerUri};
        contactRecordId = simpleQueryForId(
            DatabaseContracts.OpenpeerContactEntry.TABLE_NAME,
            where, args);
        if (contactRecordId == 0) {// No existing op contact found
            boolean isExisting = false;
            // look for the identity uri matches
            for (OPIdentityContact ic : identityContacts) {

                String params[] = new String[]{ic.getIdentityURI()};
                String opIdStr = simpleQueryForString(
                    DatabaseContracts.RolodexContactEntry.TABLE_NAME,
                    DatabaseContracts.RolodexContactEntry.COLUMN_OPENPEER_CONTACT_ID,
                    DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_URI + "=?", params);
                if (!TextUtils.isEmpty(opIdStr)) {
                    contactRecordId = Long.parseLong(opIdStr);
                    // Found OP contact. now update the openpeer_contact record
                    if (contactRecordId > 0) {
                        user.setUserId(contactRecordId);
                        updateOPTable(contactRecordId, stableId, peerUri,
                                      contact.getPeerFilePublic());
                        isExisting = true;
                        break;
                    }
                }
            }
            if (!isExisting) {
                saveUser(user, 0);
            }
        } else {
            user.setUserId(contactRecordId);
            updateOPTable(contactRecordId, stableId, peerUri,
                          contact.getPeerFilePublic());
            // add/delete identities associated
            for (OPIdentityContact ic : identityContacts) {
                if (simpleQueryForId(DatabaseContracts.RolodexContactEntry.TABLE_NAME,
                                     DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_URI +
                                         "=?",
                                     new String[]{ic.getIdentityURI()}) > 0) {

                }
            }
        }
        cacheUser(user);
        return user;
    }


    public List<OPUser> getUsers(long[] userIDs) {
        List<OPUser> users = new ArrayList<OPUser>();
        for (long userId : userIDs) {
            OPUser user = getUserById(userId);
            users.add(user);
        }
        return users;
    }


    public List<OPUser> getUsersByCbcId(long cbcId) {
        Cursor cursor = query(DatabaseContracts.ParticipantEntry.TABLE_NAME,
                              new String[]{DatabaseContracts.ParticipantEntry.COLUMN_CONTACT_ID},
                              DatabaseContracts.ParticipantEntry.COLUMN_CBC_ID + "=" + cbcId,
                              null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                List<OPUser> users = new ArrayList<>(cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long userId = cursor.getLong(0);
                    OPUser user = getUserById(userId);
                    if (user != null) {
                        users.add(user);
                    }
                    cursor.moveToNext();
                }
                cursor.close();
                return users;
            }
        }
        return null;
    }

    public OPUser getUserById(long id) {
        OPUser user = mUsersById.get(id);
        if (user != null) {
            return user;
        }
        Cursor cursor = getContentResolver()
            .query(mContentUriProvider.getContentUri(DatabaseContracts.OpenpeerContactEntry
                                                         .URI_PATH_INFO_DETAIL
                                                         + "/" + id), null, null, null, null);
        if (cursor.getCount() > 0) {
            user = fromDetailCursor(cursor);
            cacheUser(user);
        }
        return user;
    }

    public OPConversation getConversation(GroupChatMode type, ParticipantInfo participantInfo,
                                          String conversationId) {
        String where = null;
        String args[] = null;
        switch (type){
        case contact:{
            where = DatabaseContracts.ConversationEntry.COLUMN_PARTICIPANTS + "=" +
                participantInfo.getCbcId() +

                " and " + DatabaseContracts.ConversationEntry.COLUMN_TYPE + "=?";
            args = new String[]{type.name()};

            break;
        }
        case thread:{
            where = DatabaseContracts.ConversationEntry.COLUMN_CONVERSATION_ID + "=?" +
                " and " + DatabaseContracts.ConversationEntry.COLUMN_TYPE + "=?";
            args = new String[]{conversationId, type.name()};
            break;
        }
        default:
            break;
        }
        Cursor cursor = query(DatabaseContracts.ConversationEntry.TABLE_NAME, null, where, args);
        OPConversation conversation = null;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            // We always use existing conversationId
            conversationId = cursor.getString(cursor.getColumnIndex(DatabaseContracts
                                                                        .ConversationEntry
                                                                        .COLUMN_CONVERSATION_ID));
            conversation = new OPConversation(participantInfo, conversationId, type);
            conversation.setId(cursor.getLong(0));
            conversation.setCbcId(cursor.getLong(cursor.getColumnIndex(DatabaseContracts
                                                                           .ConversationEntry
                                                                           .COLUMN_PARTICIPANTS)));
            conversation.setDisabled(cursor.getInt(cursor.getColumnIndex(DatabaseContracts
                                                                             .ConversationEntry
                                                                             .COLUMN_DISABLED))
                                         == 0 ? false : true);

        } else {
            //TODO: error handling. There should be only one record
        }
        cursor.close();
        return conversation;
    }

    public List<OPConversationEvent> getConversationEvents(OPConversation conversation) {
        List<OPConversationEvent> events = null;
        Cursor cursor = query(DatabaseContracts.ConversationEventEntry.TABLE_NAME, null,
                              DatabaseContracts.ConversationEventEntry.COLUMN_CONVERSATION_ID +
                                  "=?", new String[]{conversation.getConversationId()});
        if (cursor.getCount() > 0) {
            events = new ArrayList<OPConversationEvent>();
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                OPConversationEvent event = new OPConversationEvent(conversation,
                                                                    OPConversationEvent
                                                                        .EventTypes.valueOf
                                                                        (cursor.getString(cursor
                                                                                              .getColumnIndex(DatabaseContracts.ConversationEventEntry.COLUMN_EVENT))),
                                                                    cursor.getString(cursor
                                                                                         .getColumnIndex(DatabaseContracts.ConversationEventEntry.COLUMN_CONTENT)));
                events.add(event);
            }
        }
        cursor.close();
        return events;
    }

    public List<MessageEvent> getMessageEvents(String messageId) {
        List<MessageEvent> events = null;
        // TODO: fix the message id being replaced when editting/deleting. This shouldn't be a
        // problemm since we'll always be using the
        // current message id
        Cursor cursor = query(
            DatabaseContracts.MessageEventEntry.TABLE_NAME,
            null,
            DatabaseContracts.MessageEventEntry.COLUMN_MESSAGE_ID
                + "=(select _id from message where message_id=?)",
            new String[]{messageId});
        if (cursor.getCount() > 0) {
            events = new ArrayList<MessageEvent>();
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                MessageEvent event = new MessageEvent(messageId,
                                                      MessageEvent.EventType.valueOf(cursor
                                                                                         .getString(cursor
                                                                                                        .getColumnIndex(DatabaseContracts.MessageEventEntry.COLUMN_EVENT))),
                                                      cursor.getString(cursor
                                                                           .getColumnIndex
                                                                               (DatabaseContracts
                                                                                    .MessageEventEntry.COLUMN_DESCRIPTION)),
                                                      cursor.getLong(cursor.getColumnIndex
                                                          (DatabaseContracts.MessageEventEntry
                                                               .COLUMN_TIME)));
                events.add(event);
            }
        }
        cursor.close();
        return events;
    }


    public List<CallEvent> getCallEvents(String callId) {
        List<CallEvent> events = null;
        Cursor cursor = query(DatabaseContracts.CallEventEntry.TABLE_NAME, null,
                              DatabaseContracts.CallEventEntry.COLUMN_CALL_ID + "=?",
                              new String[]{callId});
        if (cursor.getCount() > 0) {
            events = new ArrayList<CallEvent>();
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                CallEvent event = new CallEvent(callId,
                                                cursor.getString(cursor
                                                                     .getColumnIndex
                                                                         (DatabaseContracts
                                                                              .CallEventEntry
                                                                              .COLUMN_EVENT)),
                                                cursor.getLong(cursor.getColumnIndex
                                                    (DatabaseContracts.CallEventEntry
                                                         .COLUMN_TIME)));
                events.add(event);
            }
        }
        cursor.close();
        return events;
    }


    public void markMessagesRead(OPConversation conversation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_READ, 1);
        String where = DatabaseContracts.MessageEntry.COLUMN_MESSAGE_READ + "=0 ";
        String url = DatabaseContracts.MessageEntry.URI_PATH_INFO_CONTEXT_URI_BASE + conversation
            .getConversationId();

        int count = update(url, values, where, null);
        OPLogger.debug(OPLogLevel.LogLevel_Debug, "markMessagesRead update count " + count);
    }


    public int updateMessage(OPMessage message, OPConversation conversation) {
        int count = 0;

        MessageEvent event = null;
        if (TextUtils.isEmpty(message.getMessage())) {
            message.setEditState(MessageEditState.Deleted);
            event = new MessageEvent(message.getMessageId(), MessageEvent.EventType.Delete, "",
                                     System.currentTimeMillis());
            saveMessageEvent(event);
        } else {
            // this is an edit. put in the edited flag and new message text
            message.setEditState(MessageEditState.Edited);
            event = new MessageEvent(message.getMessageId(), MessageEvent.EventType.Delete, "",
                                     System.currentTimeMillis());
            saveMessageEvent(event);
        }
        String where = DatabaseContracts.MessageEntry.COLUMN_MESSAGE_ID + "=?";
        String args[] = new String[]{message.getReplacesMessageId()};
        String url = DatabaseContracts.MessageEntry.URI_PATH_INFO_CONTEXT + conversation
            .getConversationId();

        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_ID, message.getMessageId());
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_TEXT, message.getMessage());

        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_TYPE,
                   message.getMessageType());
        values.put(DatabaseContracts.MessageEntry.COLUMN_SENDER_ID, message.getSenderId());
        values.put(DatabaseContracts.MessageEntry.COLUMN_CBC_ID, conversation.getCurrentWindowId());
        values.put(DatabaseContracts.MessageEntry.COLUMN_CONTEXT_ID, conversation
            .getConversationId());

        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_READ, message.isRead());
        values.put(DatabaseContracts.MessageEntry.COLUMN_EDIT_STATUS, message.getEditState()
            .ordinal());

        count = getContentResolver().update(
            mContentUriProvider.getContentUri(url), values, where, args);
        if (count == 0) {
            OPLogger.error(OPLogLevel.LogLevel_Basic,
                           "updating message failed " + message.getMessageId());
        }

        return count;
    }


    public Uri saveMessage(OPMessage message, String conversationId,
                           ParticipantInfo participantInfo) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_ID, message.getMessageId());
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_TEXT, message.getMessage());

        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_TYPE, message.getMessageType());
        values.put(DatabaseContracts.MessageEntry.COLUMN_SENDER_ID, message.getSenderId());

        values.put(DatabaseContracts.MessageEntry.COLUMN_CONTEXT_ID, conversationId);
        values.put(DatabaseContracts.MessageEntry.COLUMN_CBC_ID, participantInfo.getCbcId());
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_READ, message.isRead());
        values.put(DatabaseContracts.MessageEntry.COLUMN_EDIT_STATUS,
                   message.getEditState().ordinal());

        // we set the time here because we don't want to update the message time for edit/delete
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_TIME, message.getTime().toMillis
            (false));
        Uri uri = insert(DatabaseContracts.MessageEntry.TABLE_NAME, values);

        uri = notifyMessageChanged(conversationId, 0);

        return uri;
    }


    public boolean updateMessageDeliveryStatus(String messageId,
                                               String conversationId,
                                               MessageDeliveryStates deliveryStatus) {
        int count;

        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_ID, messageId);
        values.put(DatabaseContracts.MessageEntry.COLUMN_MESSAGE_DELIVERY_STATUS,
                   deliveryStatus.name());
        String selection = DatabaseContracts.MessageEntry.COLUMN_MESSAGE_ID + "=?";
        String args[] = new String[]{messageId};
        count = update(DatabaseContracts.MessageEntry.URI_PATH_INFO_CONTEXT_URI_BASE +
                           conversationId, values,
                       selection, args);
        MessageEvent event = new MessageEvent(messageId,
                                              MessageEvent.EventType.DeliveryStateChange,
                                              MessageEvent.getStateChangeJsonBlob
                                                  (deliveryStatus),
                                              System.currentTimeMillis());
        saveMessageEvent(event);

        return count > 0;
    }


    public boolean saveAccount(OPAccount account) {
        boolean result = false;

        long accountRecordId = 0;
        OPContact contact = OPContact.getForSelf(account);
        // find the existing record of the account
        String peerUri = contact.getPeerURI();
        String peerfile = contact.getPeerFilePublic();
        String stableId = account.getStableID();
        List<OPIdentity> identities = account.getAssociatedIdentities();
        String where = DatabaseContracts.AccountEntry.COLUMN_STABLE_ID + "=?";
        String args[] = new String[]{stableId};

        accountRecordId = simpleQueryForId(DatabaseContracts.AccountEntry.TABLE_NAME, where, args);
        if (accountRecordId == 0) {
            if (identities.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (OPIdentity identity : identities) {
                    sb.append(identity.getIdentityURI() + ",");
                }
                sb.deleteCharAt(sb.length() - 1);
                Cursor cursor = query(DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME,
                                      new String[]{DatabaseContracts.AssociatedIdentityEntry
                                                       .COLUMN_ACCOUNT_ID},
                                      DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_URI
                                          + " in (?)",
                                      new String[]{sb.toString()});
                // There could be multiple records when we support multiple identities but they
                // should all point to same account.
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    accountRecordId = cursor.getLong(0);
                }
                cursor.close();

            } else {
                Cursor cursor = query(DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME,
                                      new String[]{DatabaseContracts.AssociatedIdentityEntry
                                                       .COLUMN_ACCOUNT_ID},
                                      DatabaseContracts.AssociatedIdentityEntry
                                          .COLUMN_IDENTITY_URI + " =?",
                                      new String[]{identities.get(0).getIdentityURI()});
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    accountRecordId = cursor.getLong(0);
                }
                cursor.close();
            }
        }
        // This is a new account, insert it
        if (accountRecordId == 0) {
            ContentValues values = new ContentValues();
            values.put(DatabaseContracts.AccountEntry.COLUMN_LOGGED_IN, 0);
            update(DatabaseContracts.AccountEntry.TABLE_NAME, values, null, null);

            values.put(DatabaseContracts.AccountEntry.COLUMN_LOGGED_IN, 1);
            values.put(DatabaseContracts.AccountEntry.COLUMN_STABLE_ID, stableId);
            values.put(DatabaseContracts.AccountEntry.COLUMN_RELOGIN_INFO,
                       account.getReloginInformation());

            accountRecordId = getId(insert(DatabaseContracts.AccountEntry.TABLE_NAME, values));

            // insert openpeer_contact entry of myself
            long opId = saveOPContactTable(stableId, peerUri, peerfile);
            saveOrUpdateIdentities(identities, accountRecordId, opId);
            account.setAccountId(accountRecordId);
            account.setSelfContactId(opId);
            result = true;
        } else {
            account.setAccountId(accountRecordId);
            Cursor cursor = query(DatabaseContracts.OpenpeerContactEntry.URI_PATH_LOGIN_USER,
                                  null, null, null);

            if (cursor == null) {
            }
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                long selfContactId = cursor.getLong(0);
                account.setSelfContactId(selfContactId);

                ContentValues values = new ContentValues();

                values.put(DatabaseContracts.AccountEntry.COLUMN_LOGGED_IN, 1);
                values.put(DatabaseContracts.AccountEntry.COLUMN_STABLE_ID, stableId);
                values.put(DatabaseContracts.AccountEntry.COLUMN_RELOGIN_INFO,
                           account.getReloginInformation());

                update(DatabaseContracts.AccountEntry.TABLE_NAME, values,
                       "_id=" + accountRecordId, null);
                values.clear();
                values = new ContentValues();
                values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_STABLE_ID, stableId);
                values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERURI, peerUri);
                values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERFILE_PUBLIC, peerfile);
                update(DatabaseContracts.OpenpeerContactEntry.TABLE_NAME, values,
                       "_id=" + selfContactId, null);
                saveOrUpdateIdentities(identities, accountRecordId, selfContactId);
                result = true;
            } else {
                OPLogger.error(OPLogLevel.LogLevel_Basic,
                               "Something went terribly wrong because the self contact is empty " +
                                   "for account!");
            }

        }

        return result;
    }


    public List<OPRolodexContact> saveDownloadedRolodexContacts(
        OPIdentity identity,
        List<OPRolodexContact> contacts, String contactsVersion) {

        long identityId = simpleQueryForId(
            DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME,
            DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_URI + "=?",
            new String[]{identity.getIdentityURI()});

        setDownloadedContactsVersion(
            identity.getIdentityURI(), contactsVersion);
        List<OPRolodexContact> contactsToLookup = new ArrayList<OPRolodexContact>();
        for (OPRolodexContact contact : contacts) {
            switch (contact.getDisposition()){
            case Disposition_Remove:
                deleteContact(contact.getIdentityURI());
                contactsToLookup.add(contact);
                break;
            case Disposition_Update:
                // updateRolodexContactTable(contact, 0, 0, identityId);
                // break;
            default:
                saveRolodexContactTable(contact, 0, 0, identityId);
            }
        }
        if (!contactsToLookup.isEmpty()) {
            contacts.removeAll(contactsToLookup);
        }
        notifyContactsChanged();
        return contacts;
    }


    public long saveConversation(OPConversation conversation) {
        long id = 0;
        String where = conversation.getType() == GroupChatMode.contact ? DatabaseContracts
            .ConversationEntry
            .COLUMN_PARTICIPANTS + "=" + conversation.getCurrentWindowId() :
            DatabaseContracts.ConversationEntry.COLUMN_CONVERSATION_ID + "=?";
        String args[] = conversation.getType() == GroupChatMode.contact ? null : new
            String[]{conversation.getConversationId()};
        id = simpleQueryForId(DatabaseContracts.ConversationEntry.TABLE_NAME, where, args);
        if (id != 0) {
            conversation.setId(id);
            return id;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.ConversationEntry.COLUMN_TYPE, conversation.getType().name());
        values.put(DatabaseContracts.ConversationEntry.COLUMN_START_TIME,
                   System.currentTimeMillis());
        values.put(DatabaseContracts.ConversationEntry.COLUMN_PARTICIPANTS,
                   conversation.getCurrentWindowId());
        values.put(DatabaseContracts.ConversationEntry.COLUMN_CONVERSATION_ID,
                   conversation.getConversationId());
        values.put(DatabaseContracts.ConversationEntry.COLUMN_ACCOUNT_ID,
                   getCurrentUserId());
        id = getId(insert(DatabaseContracts.ConversationEntry.TABLE_NAME, values));
        conversation.setId(id);
        saveParticipants(conversation.getCurrentWindowId(), conversation.getParticipants());

        return id;
    }


    public long updateConversation(OPConversation conversation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.ConversationEntry.COLUMN_PARTICIPANTS,
                   conversation.getCurrentWindowId());
        values.put(DatabaseContracts.ConversationEntry.COLUMN_DISABLED,
                   conversation.isDisabled());
        int count = update(DatabaseContracts.ConversationEntry.TABLE_NAME, values,
                           DatabaseContracts.ConversationEntry.COLUMN_CONVERSATION_ID + "=?",
                           new String[]{conversation.getConversationId()});

        getContentResolver().notifyChange(
            mContentUriProvider.getContentUri(DatabaseContracts.WindowViewEntry
                                                  .URI_PATH_INFO_CONTEXT), null);
        return count;
    }

    public long saveConversationEvent(OPConversationEvent event) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.ConversationEventEntry.COLUMN_CONVERSATION_ID,
                   event.getConversationId());
        values.put(DatabaseContracts.ConversationEventEntry.COLUMN_EVENT, event.getEventType()
            .name());
        values.put(DatabaseContracts.ConversationEventEntry.COLUMN_CONTENT, event
            .getContentString());
        values.put(DatabaseContracts.ConversationEventEntry.COLUMN_PARTICIPANTS, event.getCbcId());
        values.put(DatabaseContracts.ConversationEventEntry.COLUMN_TIME, event.getTime());
        long id = getId(insert(DatabaseContracts.ConversationEventEntry.TABLE_NAME, values));
        return id;
    }


    public long saveMessageEvent(MessageEvent event) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.MessageEventEntry.COLUMN_MESSAGE_ID, event.getMessageId());
        values.put(DatabaseContracts.MessageEventEntry.COLUMN_EVENT, event.getEvent().name());
        values.put(DatabaseContracts.MessageEventEntry.COLUMN_DESCRIPTION, event.getDescription());
        values.put(DatabaseContracts.MessageEventEntry.COLUMN_TIME, event.getTime());
        Uri uri = insert(DatabaseContracts.MessageEventEntry.TABLE_NAME, values);
        return getId(uri);
    }


    public long saveCall(String callId,
                         String conversationId,
                         long peerId,
                         int direction,
                         String mediaType) {
        long callRecordId = simpleQueryForId(DatabaseContracts.CallEntry.TABLE_NAME,
                                             DatabaseContracts.CallEntry.COLUMN_CALL_ID + "=?",
                                             new String[]{callId});
        if (callRecordId != 0) {
            return callRecordId;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.CallEntry.COLUMN_CALL_ID, callId);
        values.put(DatabaseContracts.CallEntry.COLUMN_CONTVERSATION_ID, conversationId);

        values.put(DatabaseContracts.CallEntry.COLUMN_PEER_ID, peerId);
        // 0 for outgoing,1 for incoming
        values.put(DatabaseContracts.CallEntry.COLUMN_DIRECTION, direction);
        values.put(DatabaseContracts.CallEntry.COLUMN_TIME, System.currentTimeMillis());

        values.put(DatabaseContracts.CallEntry.COLUMN_TYPE, "call/" + mediaType);

        Uri uri = insert(DatabaseContracts.CallEntry.TABLE_NAME, values);
        return getId(uri);
    }


    public long saveCallEvent(String callId, String conversationId, CallEvent event) {
        long eventRecordId = 0;

        switch (event.getState()){
        case CallSystemMessage.TYPE_ANSWERED:{
            ContentValues callValues = new ContentValues();
            callValues.put(DatabaseContracts.CallEntry.COLUMN_ANSWER_TIME,
                           System.currentTimeMillis());
            updateCallTable(callId, callValues);
        }
        break;
        case CallSystemMessage.TYPE_HUNGUP:{
            ContentValues callValues = new ContentValues();
            callValues.put(DatabaseContracts.CallEntry.COLUMN_END_TIME, System.currentTimeMillis());
            updateCallTable(callId, callValues);
        }
        break;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.CallEventEntry.COLUMN_CALL_ID, callId);
        values.put(DatabaseContracts.CallEventEntry.COLUMN_EVENT, event.getState());
        values.put(DatabaseContracts.MessageEventEntry.COLUMN_TIME, event.getTime());
        eventRecordId = ContentUris.parseId(insert(DatabaseContracts.CallEventEntry.TABLE_NAME,
                                                   values));
        notifyMessageChanged(conversationId, 30000 + eventRecordId);

        return eventRecordId;
    }


    public void saveIdentityContact(List<OPIdentityContact> iContacts,
                                    long associatedIdentityId) {

        for (OPIdentityContact contact : iContacts) {
            saveIdentityContactTable(contact, associatedIdentityId);
        }
    }


    public boolean deleteIdentity(String identityUri) {
        // TODO Auto-generated method stub
        return false;
    }

    private OPUser saveUser(OPUser user, long associatedIdentityId) {

        OPContact opContact = user.getOPContact();
        List<OPIdentityContact> identityContacts = user
            .getIdentityContacts();

        long opId = saveOPContactTable(identityContacts.get(0)
                                           .getStableID(), opContact.getPeerURI(),
                                       opContact.getPeerFilePublic());

        for (OPIdentityContact contact : identityContacts) {
            long identityContactId = saveIdentityContactTable(contact);

            long rolodexId = saveRolodexContactTable(contact, opId,
                                                     identityContactId, associatedIdentityId);

        }
        user.setUserId(opId);
        notifyContactsChanged();

        return user;

    }

    private void updateCallTable(String callId, ContentValues values) {
        String where = DatabaseContracts.CallEntry.COLUMN_CALL_ID + "=?";
        String[] args = new String[]{callId};
        update(DatabaseContracts.CallEntry.TABLE_NAME, values, where, args);
    }

    private long saveRolodexContactTable(OPRolodexContact contact,
                                         long opId,
                                         long identityContactId,
                                         long associatedIdentityId) throws SQLiteException {

        long identityProviderId = simpleQueryForId(
            DatabaseContracts.IdentityProviderEntry.TABLE_NAME,
            DatabaseContracts.IdentityProviderEntry.COLUMN_DOMAIN + "=?",
            new String[]{contact.getIdentityProvider()});
        if (identityProviderId == 0) {
            identityProviderId = saveIdentityProviderTable(contact
                                                               .getIdentityProvider());
        }
        ContentValues values = new ContentValues();
        if (opId != 0) {
            values.put(DatabaseContracts.RolodexContactEntry.COLUMN_OPENPEER_CONTACT_ID, opId);
        }
        if (identityContactId != 0) {
            values.put(DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_CONTACT_ID,
                       identityContactId);
        }
        if (associatedIdentityId != 0) {
            values.put(DatabaseContracts.RolodexContactEntry.COLUMN_ASSOCIATED_IDENTITY_ID,
                       associatedIdentityId);
        }
        values.put(DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_PROVIDER_ID,
                   identityProviderId);
        values.put(DatabaseContracts.RolodexContactEntry.COLUMN_CONTACT_NAME,
                   contact.getName());

        values.put(DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_URI,
                   contact.getIdentityURI());
        values.put(DatabaseContracts.RolodexContactEntry.COLUMN_PROFILE_URL,
                   contact.getProfileURL());
        values.put(DatabaseContracts.RolodexContactEntry.COLUMN_VPROFILE_URL,
                   contact.getVProfileURL());

        long rolodexId = simpleQueryForId(DatabaseContracts.RolodexContactEntry.TABLE_NAME,
                                          DatabaseContracts.RolodexContactEntry
                                              .COLUMN_IDENTITY_URI + "=?",
                                          new String[]{contact.getIdentityURI()});
        if (rolodexId == 0) {
            rolodexId = getId(insert(DatabaseContracts.RolodexContactEntry.TABLE_NAME, values));
            if (rolodexId == -1) {
                throw new SQLiteException("Inserting rolodex contact failed "
                                              + values.toString());
            }
            // insert avatars
            List<OPRolodexContact.OPAvatar> avatars = contact.getAvatars();
            if (avatars != null && !avatars.isEmpty()) {
                for (OPRolodexContact.OPAvatar avatar : avatars) {
                    saveAvatarTable(avatar, rolodexId);
                }
            }
        } else {
            update(DatabaseContracts.RolodexContactEntry.TABLE_NAME, values, BaseColumns._ID
                + "=" + rolodexId, null);
            List<OPRolodexContact.OPAvatar> avatars = contact.getAvatars();
            if (avatars != null && !avatars.isEmpty()) {
                for (OPRolodexContact.OPAvatar avatar : avatars) {
                    long avatarRecordId = simpleQueryForId(
                        DatabaseContracts.AvatarEntry.TABLE_NAME,
                        DatabaseContracts.AvatarEntry.COLUMN_AVATAR_URI + "=? and "
                            + DatabaseContracts.AvatarEntry.COLUMN_ROLODEX_ID + "="
                            + rolodexId,
                        new String[]{avatar.getURL()});
                    if (avatarRecordId == 0) {
                        avatarRecordId = saveAvatarTable(avatar, rolodexId);
                    }
                }
                // TODO: delete avatars
            }
        }

        return rolodexId;
    }

    private long saveIdentityContactTable(OPIdentityContact contact)
        throws SQLiteException {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.IdentityContactEntry.COLUMN_PRORITY, contact.getPriority());
        values.put(DatabaseContracts.IdentityContactEntry.COLUMN_WEIGHT, contact.getWeight());
        values.put(DatabaseContracts.IdentityContactEntry.COLUMN_IDENTITY_PROOF_BUNDLE,
                   contact.getIdentityProofBundle());
        values.put(DatabaseContracts.IdentityContactEntry.COLUMN_LAST_UPDATE_TIME, contact
            .getLastUpdated().toMillis(false));
        values.put(DatabaseContracts.IdentityContactEntry.COLUMN_EXPIRE,
                   contact.getExpires().toMillis(false));

        Uri uri = insert(DatabaseContracts.IdentityContactEntry.TABLE_NAME, values);
        return getId(uri);
    }

    private long saveIdentityTable(OPIdentity contact, long accountId,
                                   long selfContactId)
        throws SQLiteException {

        String identityProvider = contact.getIdentityProviderDomain();
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.AssociatedIdentityEntry.COLUMN_ACCOUNT_ID, accountId);
        values.put(DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_URI,
                   contact.getIdentityURI());
        values.put(DatabaseContracts.AssociatedIdentityEntry.COLUMN_SELF_CONTACT_ID, selfContactId);
        long identityProviderId = simpleQueryForId(
            DatabaseContracts.IdentityProviderEntry.TABLE_NAME,
            DatabaseContracts.IdentityProviderEntry.COLUMN_DOMAIN + "=?",
            new String[]{identityProvider});
        if (identityProviderId == 0) {
            identityProviderId = saveIdentityProviderTable(identityProvider);
        }
        values.put(DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_PROVIDER_ID,
                   identityProviderId);
        Uri uri = insert(DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME,
                         values);
        if (uri != null) {
            return ContentUris.parseId(uri);
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openpeer.sdk.datastore.OPDatastoreDelegate#notifyContactsChanged()
     */
    private void notifyContactsChanged() {
        getContentResolver()
            .notifyChange(
                mContentUriProvider.getContentUri(DatabaseContracts.RolodexContactEntry
                                                      .URI_PATH_INFO),
                null);
    }

    /**
     * Construct user object from contacts table
     *
     * @param cursor
     * @return
     */
    private OPUser fromDetailCursor(Cursor cursor) {

        if (cursor != null & cursor.getCount() > 0) {
            OPUser user = new OPUser();
            List<OPIdentityContact> contacts = new ArrayList<OPIdentityContact>();
            cursor.moveToFirst();

            user.setUserId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
            user.setPeerUri(cursor.getString(cursor.getColumnIndex(DatabaseContracts
                                                                       .OpenpeerContactEntry
                                                                       .COLUMN_PEERURI)));
            while (!cursor.isAfterLast()) {
                contacts.add(contactFromCursor(cursor));
                cursor.moveToNext();
            }
            user.setIdentityContacts(contacts);
            return user;
        }
        return null;
    }

    private void setDownloadedContactsVersion(String identityUri, String version) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_CONTACTS_VERSION,
                   version);
        String whereClause = DatabaseContracts.AssociatedIdentityEntry.COLUMN_IDENTITY_URI + "=?";
        String args[] = new String[]{identityUri};
        long rowId = update(DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME, values,
                            whereClause, args);
        OPLogger.debug(OPLogLevel.LogLevel_Debug, "setDownloadedContactsVersion " + rowId
            + " version " + version + " id " + identityUri);
    }

    /**
     * Do NOT use this method if you wish to implement your own data store. This function is
     * bound the default datastore implementation
     *
     * @param cursor
     * @return
     */
    private OPIdentityContact contactFromCursor(Cursor cursor) {
        int identityUrlIndex = cursor
            .getColumnIndex(DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_URI);
        int identityProviderIndex = cursor
            .getColumnIndex(DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_PROVIDER);
        int nameIndex = cursor
            .getColumnIndex(DatabaseContracts.RolodexContactEntry.COLUMN_CONTACT_NAME);
        int profileURLIndex = cursor
            .getColumnIndex(DatabaseContracts.RolodexContactEntry.COLUMN_PROFILE_URL);
        int vprofileURLIndex = cursor
            .getColumnIndex(DatabaseContracts.RolodexContactEntry.COLUMN_VPROFILE_URL);
        long rolodexId = cursor.getLong(cursor.getColumnIndex("rolodex_id"));

        OPIdentityContact contact = new OPIdentityContact(rolodexId,
                                                          cursor.getString(identityUrlIndex),
                                                          cursor.getString(identityProviderIndex),
                                                          cursor.getString(nameIndex),
                                                          cursor.getString(profileURLIndex),
                                                          cursor.getString(vprofileURLIndex), null);
        List<OPRolodexContact.OPAvatar> avatars = getAvatars(rolodexId);

        contact.setAvatars(avatars);
        contact.setIdentityParams(
            cursor.getString(cursor.getColumnIndex(DatabaseContracts.OpenpeerContactEntry
                                                       .COLUMN_STABLE_ID)),
            cursor.getString(cursor.getColumnIndex(DatabaseContracts.OpenpeerContactEntry
                                                       .COLUMN_PEERFILE_PUBLIC)),
            cursor.getString(cursor.
                getColumnIndex(DatabaseContracts.IdentityContactEntry
                                   .COLUMN_IDENTITY_PROOF_BUNDLE)),
            cursor.getInt(cursor.getColumnIndex(DatabaseContracts.IdentityContactEntry
                                                    .COLUMN_PRORITY)),
            cursor.getInt(cursor.getColumnIndex(DatabaseContracts.IdentityContactEntry
                                                    .COLUMN_WEIGHT)),
            cursor.getLong(cursor.getColumnIndex(DatabaseContracts.IdentityContactEntry
                                                     .COLUMN_LAST_UPDATE_TIME)),
            cursor.getLong(cursor.getColumnIndex(DatabaseContracts.IdentityContactEntry
                                                     .COLUMN_EXPIRE)));

        return contact;
    }

    private Uri notifyMessageChanged(String conversationId, long id) {
        StringBuilder sb = new StringBuilder();

        sb.append(DatabaseContracts.MessageEntry.URI_PATH_INFO_CONTEXT_URI_BASE + conversationId);

        if (id != 0) {
            sb.append("/" + id);
        }

        Uri uri = mContentUriProvider.getContentUri(sb.toString());
        getContentResolver().notifyChange(uri, null);
        return uri;
    }

    /**
     * Put the user object in cache so we have to query db later on.
     *
     * @param user
     */
    private void cacheUser(OPUser user) {
        mUsers.put(user.getPeerUri(), user);
        mUsersById.put(user.getUserId(), user);
    }


    public void saveParticipants(long windowId, List<OPUser> userList) {
        long id = simpleQueryForId(DatabaseContracts.ParticipantEntry.TABLE_NAME,
                                   DatabaseContracts.ParticipantEntry.COLUMN_CBC_ID + "=" +
                                       windowId, null);
        if (id != 0) {
            OPLogger.debug(OPLogLevel.LogLevel_Debug, "saveParticipants existed" + windowId);
            return;
        }
        // now insert the participants
        ContentValues contentValues[] = new ContentValues[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            OPUser user = userList.get(i);
            contentValues[i] = new ContentValues();
            contentValues[i].put(DatabaseContracts.ParticipantEntry.COLUMN_CBC_ID, windowId);
            contentValues[i].put(
                DatabaseContracts.ParticipantEntry.COLUMN_CONTACT_ID,
                user.getUserId());
        }
        int count = getContentResolver()
            .bulkInsert(
                mContentUriProvider.getContentUri(DatabaseContracts.ParticipantEntry
                                                      .URI_PATH_INFO),
                contentValues);
        Log.d("test", "Inserted window participants " + count + " values "
            + Arrays.deepToString(contentValues));

    }

    public Uri getContentUri(String path) {
        return mContentUriProvider.getContentUri(path);
    }

    private boolean saveOrUpdateIdentities(List<OPIdentity> identities,
                                           long accountId, long opId) {
        for (OPIdentity identity : identities) {
            OPIdentityContact iContact = identity.getSelfIdentityContact();
            long identityContactId = saveIdentityContactTable(iContact);
            long rolodexId = saveRolodexContactTable(iContact, opId,
                                                     identityContactId, 0);
            saveIdentityTable(identity, accountId, rolodexId);
        }
        return true;
    }

    private void deleteById(String tableName, long id) {
        delete(tableName, BaseColumns._ID + "=" + id, null);
    }

    private Cursor simpleQuery(SQLiteDatabase db, String tableName,
                               String[] columns, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return db.query(tableName, columns, selection, selectionArgs, null,
                        null, null);
    }

    private String simpleQueryForString(String tableName,
                                        String column,
                                        String where, String[] args) {
        String value = null;
        Cursor cursor = query(tableName, new String[]{column}, where, args);
        if (cursor == null) {

        }
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    private String simpleQueryForString(SQLiteDatabase db, String tableName,
                                        String column,
                                        String where, String[] args) {
        String value = null;
        Cursor cursor = simpleQuery(db, tableName, new String[]{column},
                                    where, args);
        if (cursor == null) {

        }
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    private long saveOPContactTable(String stableId, String peerUri,
                                    String peerfile) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_STABLE_ID, stableId);
        values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERURI, peerUri);
        values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERFILE_PUBLIC, peerfile);
        return getId(insert(DatabaseContracts.OpenpeerContactEntry.TABLE_NAME, values));
    }

    /**
     * @param avatar
     * @param rolodexId
     */
    private long saveAvatarTable(OPRolodexContact.OPAvatar avatar, long rolodexId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.AvatarEntry.COLUMN_ROLODEX_ID, rolodexId);
        values.put(DatabaseContracts.AvatarEntry.COLUMN_AVATAR_NAME, avatar.getName());
        values.put(DatabaseContracts.AvatarEntry.COLUMN_AVATAR_URI, avatar.getURL());
        values.put(DatabaseContracts.AvatarEntry.COLUMN_HEIGHT, avatar.getHeight());
        values.put(DatabaseContracts.AvatarEntry.COLUMN_WIDTH, avatar.getWidth());
        return getId(insert(DatabaseContracts.AvatarEntry.TABLE_NAME, values));
    }

    private long saveIdentityProviderTable(String providerDomain) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.IdentityProviderEntry.COLUMN_DOMAIN, providerDomain);
        return getId(insert(DatabaseContracts.IdentityProviderEntry.TABLE_NAME, values));
    }

    private void updateOPTable(long id, String stableId, String peerUri,
                               String peerfile) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_STABLE_ID, stableId);
        values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERURI, peerUri);
        values.put(DatabaseContracts.OpenpeerContactEntry.COLUMN_PEERFILE_PUBLIC,
                   peerfile);
        update(DatabaseContracts.OpenpeerContactEntry.TABLE_NAME,
               values, BaseColumns._ID + "=" + id,
               null);
    }

//    private long insertIfNonExist(SQLiteDatabase db, String table,
//            ContentValues values, String where, String params[]) {
//        long id = DbUtils.simpleQueryForId(db, table, where, params);
//        if (id == 0) {
//            return db.insert(table, null, values);
//        }
//        return -1l;
//    }

    private long saveIdentityContactTable(
        OPIdentityContact contact, long associatedIdentityId) {

        String where = DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_URI + "=?";
        String args[] = new String[]{contact.getIdentityURI()};
        String columns[] = new String[]{
            BaseColumns._ID,
            DatabaseContracts.RolodexContactEntry.COLUMN_OPENPEER_CONTACT_ID,
            DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_CONTACT_ID};
        Cursor cursor = query(DatabaseContracts.RolodexContactEntry.TABLE_NAME,
                              columns, where, args);
        if (cursor == null) {
        }
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();

            cursor.moveToFirst();

            long rolodexId = cursor.getLong(0);
            long opId = cursor.getLong(1);
            long identityContactId = cursor.getLong(2);
            cursor.close();

            if (identityContactId == 0) {
                identityContactId = saveIdentityContactTable(contact);
                values.put(
                    DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_CONTACT_ID,
                    identityContactId);
            } else {
                // TODO: update
            }
            // Insert openpeer_contact table entry
            if (opId == 0) {
                // now update rolodex_contact.identity_contact_id
                String peerfile = contact.getPeerFilePublic()
                    .getPeerFileString();
                OPContact opContact = OPContact
                    .createFromPeerFilePublic(OPDataManager
                                                  .getInstance().getSharedAccount(),
                                              peerfile);
                String peerUri = opContact.getPeerURI();

                opId = saveOPContactTable(contact.getStableID(),
                                          peerUri, peerfile);
                values.put(
                    DatabaseContracts.RolodexContactEntry.COLUMN_OPENPEER_CONTACT_ID,
                    opId);
            } else {
                // TODO: update

            }
            if (values.size() > 0) {
                int count = update(DatabaseContracts.RolodexContactEntry.TABLE_NAME,
                                   values,
                                   DatabaseContracts.RolodexContactEntry._ID + "=" + rolodexId,
                                   null);
            }

            getContentResolver()
                .notifyChange(
                    mContentUriProvider
                        .getContentUri(DatabaseContracts.RolodexContactEntry.URI_PATH_INFO),
                    null);

            return identityContactId;

        }
        return 0;

    }

//    private boolean saveOrUpdateIdentity(OPIdentity identity, long accountId) {
//        SQLiteDatabase db = getWritableDB();
//
//        long identityId = DbUtils.simpleQueryForId(db,
//                DatabaseContracts.AssociatedIdentityEntry.TABLE_NAME,
//                AssociatedIdentityEntry.COLUMN_IDENTITY_URI + "=?",
//                new String[] { identity.getIdentityURI() });
//        if (identityId == 0) {
//            long opId = 0;
//
//            OPIdentityContact iContact = identity.getSelfIdentityContact();
//            long identityContactId = saveIdentityContactTable(iContact);
//            long rolodexId = saveRolodexContactTable(iContact, opId,
//                    identityContactId, 0);
//            saveIdentityTable(identity, accountId, rolodexId);
//        } else if (accountId != 0) {
//            ContentValues values = new ContentValues();
//            values.put(AssociatedIdentityEntry.COLUMN_ACCOUNT_ID, accountId);
//            db.update(AssociatedIdentityEntry.TABLE_NAME, values,
//                    BaseColumns._ID + "=" + identityId, null);
//        }
//        return true;
//    }

//    private boolean flushContactsForIdentity(long id) {
//        String selection = IdentityContactEntry.COLUMN_ASSOCIATED_IDENTITY_ID
//                + "=" + id;
//        String cSelection = RolodexContactEntry.COLUMN_ASSOCIATED_IDENTITY_ID
//                + "=" + id;
//        // mOpenHelper.getWritableDatabase().
//        delete(IdentityContactEntry.TABLE_NAME, selection, null);
//        delete(RolodexContactEntry.TABLE_NAME, cSelection, null);
//        return true;
//    }

    private boolean deleteContact(String identityUri) {

        String fields[] = new String[]{DatabaseContracts.RolodexContactEntry._ID,
                                       DatabaseContracts.RolodexContactEntry
                                           .COLUMN_OPENPEER_CONTACT_ID,
                                       DatabaseContracts.RolodexContactEntry
                                           .COLUMN_IDENTITY_CONTACT_ID};
        String where = DatabaseContracts.RolodexContactEntry.COLUMN_IDENTITY_URI + "=?";
        String whereArgs[] = new String[]{identityUri};
        Cursor cursor = query(DatabaseContracts.RolodexContactEntry.TABLE_NAME, fields,
                              where,
                              whereArgs);
        if (cursor == null) {

        }
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long rolodexId = cursor.getLong(0);
            long opId = cursor.getLong(1);
            if (opId > 0) {
                OPUser user = mUsersById.get(opId);
                if (user != null) {
                    mUsersById.remove(opId);
                    mUsers.remove(user.getPeerUri());
                }
            }
            long identityContactId = cursor.getLong(2);
            if (identityContactId > 0) {
                deleteById(DatabaseContracts.IdentityContactEntry.TABLE_NAME,
                           identityContactId);
            }
            // TODO: delete openpeer_contact entry if no more rolodex left
            // delete avatars and rolodex contact entry
            delete(DatabaseContracts.AvatarEntry.TABLE_NAME, DatabaseContracts.AvatarEntry
                .COLUMN_ROLODEX_ID
                + "=" + rolodexId, null);
            deleteById(DatabaseContracts.RolodexContactEntry.TABLE_NAME, rolodexId);
        }

        return true;
    }

    private int delete(String tableName, String whereClause, String[] whereArgs) {
        return getContentResolver().delete(
            mContentUriProvider.getContentUri("/" + tableName), whereClause,
            whereArgs);
    }

    private Uri insert(String tableName, ContentValues values) {
        return getContentResolver().insert(
            mContentUriProvider.getContentUri("/" + tableName), values);
    }

    private int update(String tableName, ContentValues values, String whereClause,
                       String[] whereArgs) {
        Uri uri = mContentUriProvider.getContentUri("/" + tableName);
        return getContentResolver().update(uri, values, whereClause,
                                           whereArgs);

    }

    private Cursor query(String tableName, String columns[], String whereClause,
                         String[] whereArgs) {
        Uri uri = mContentUriProvider.getContentUri("/" + tableName);

        return getContentResolver().query(uri, columns, whereClause,
                                          whereArgs, null);
    }

    private boolean upsert(String tableName, ContentValues values, String whereClause,
                           String[] whereArgs) {
        boolean result;
        Uri uri = mContentUriProvider.getContentUri("/" + tableName);
        Cursor cursor = getContentResolver().query(uri, null,
                                                   whereClause, whereArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {

                int updatedCount = getContentResolver().update(uri,
                                                               values, whereClause,
                                                               whereArgs);
                result = (updatedCount > 0);
            }
            cursor.close();
        }

        Uri _uri = getContentResolver().insert(uri, values);
        result = (_uri != null);
        return result;

    }

    private long simpleQueryForId(String tableName,
                                  String selection, String[] selectionArgs) throws SQLiteException {

        Cursor cursor = query(tableName, new String[]{"_id"}, selection,
                              selectionArgs);
        if (cursor == null) {
            throw new SQLiteException();
        }
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        return 0;
    }

    private long getId(Uri uri) {
        if (uri != null) {
            return ContentUris.parseId(uri);
        }
        return 0;
    }

    private ContentResolver getContentResolver() {
        if (mContext == null) {
            mContext = OPHelper.getInstance().getApplicationContext();
        }
        return mContext.getContentResolver();
    }
}
