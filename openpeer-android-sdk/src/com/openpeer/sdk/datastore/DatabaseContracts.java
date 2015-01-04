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
package com.openpeer.sdk.datastore;

import android.provider.BaseColumns;

/**
 * Database definitions and create statements.
 */
public class DatabaseContracts {
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String INTEGER_PRIMARY_KEY_TYPE = " INTEGER PRIMARY KEY";
    public static final String UNIQUE_TYPE = " UNIQUE";
    public static final String PRIMARY_KEY_TYPE = " PRIMARY KEY";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    public static final String CREATE_VIEW = "CREATE VIEW IF NOT EXISTS ";

    public static final String COMMA_SEP = ",";
    public static final String LEFT_JOIN = " left join ";
    public static final String ON = " on ";

    public static final String AUTHORITY = "com.openpeer.sample.provider";
    static final String SCHEME = "content://";
    public static final String URI_PREFIX = SCHEME + AUTHORITY;
    public static final String COLUMN_IDENTITY_URI = "identity_uri";
    public static final String COLUMN_THREAD_ID = "thread_id";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_STABLE_ID = "stable_id";
    public static final String COLUMN_PEER_URI = "peer_uri";
    public static final String COLUMN_CBC_ID = "cbc_id";
    public static final String COLUMN_OPENPEER_CONTACT_ID = "openpeer_contact_id";

    public DatabaseContracts() {
    }

    // We shouldn't really need a table for account since we support one account
    // only,
    // probably better off storing it in a preference file
    // The public key and secret should be stored in system keychain for
    // security.
    public static abstract class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";

        public static final String COLUMN_LOGGED_IN = "logged_in";
        public static final String COLUMN_RELOGIN_INFO = "relogin_info";
        public static final String COLUMN_STABLE_ID = DatabaseContracts.COLUMN_STABLE_ID;
        public static final String COLUMN_PEER_URI = DatabaseContracts.COLUMN_PEER_URI;
    }

    public static abstract class RolodexContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "rolodex_contact";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";

        public static final String COLUMN_ASSOCIATED_IDENTITY_ID = "associated_identity_id";
        public static final String COLUMN_IDENTITY_PROVIDER = "domain";
        public static final String COLUMN_IDENTITY_PROVIDER_ID = "identity_provider_id";

        public static final String COLUMN_IDENTITY_URI = "identity_uri";
        public static final String COLUMN_OPENPEER_CONTACT_ID = DatabaseContracts.COLUMN_OPENPEER_CONTACT_ID;
        public static final String COLUMN_IDENTITY_CONTACT_ID = "identity_contact_id";

        public static final String COLUMN_CONTACT_NAME = "name";
        public static final String COLUMN_PROFILE_URL = "profile_url";
        public static final String COLUMN_VPROFILE_URL = "vprofile_url";
    }

    public static abstract class IdentityContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "identity_contact";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";

        public static final String COLUMN_ASSOCIATED_IDENTITY_ID = "associated_identity_id";
        // This is acutally hash of identityUri of OPRolodexContact, kinda redundant but keeping it for now
        public static final String COLUMN_PEERFILE_PUBLIC_ID = "peerfile_public_id";
        public static final String COLUMN_IDENTITY_PROOF_BUNDLE = "identity_proof_bundle";
        public static final String COLUMN_PRORITY = "priority";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_LAST_UPDATE_TIME = "last_update_time";
        public static final String COLUMN_EXPIRE = "expire";
    }

    public static abstract class AssociatedIdentityEntry implements BaseColumns {
        public static final String TABLE_NAME = "associated_identity";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";

        public static final String COLUMN_IDENTITY_PROVIDER_ID = "identity_provider_id";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_IDENTITY_URI = DatabaseContracts.COLUMN_IDENTITY_URI;
        // The "selfContact" of the identity
        public static final String COLUMN_SELF_CONTACT_ID = "self_contact_id";
        public static final String COLUMN_IDENTITY_CONTACTS_VERSION = "downloaded_contacts_version";
    }

    public static abstract class AvatarEntry implements BaseColumns {
        public static final String TABLE_NAME = "avatar";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";
        public static final String COLUMN_AVATAR_URI = "avatar_uri";
        public static final String COLUMN_AVATAR_NAME = "name";
        public static final String COLUMN_WIDTH = "width";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_ROLODEX_ID = "rolodex_id";

    }

    public static abstract class OpenpeerContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "openpeer_contact";
        public static final String URI_PATH_LOGIN_USER = "/" + TABLE_NAME+"/loggedin";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";
        // use this URI to retrieve full info of openpeer contact from all three tables(openpeer_contact,rolodex_contact,identity_contact0
        public static final String URI_PATH_INFO_DETAIL = "/" + TABLE_NAME
            + "/detail";
        public static final String URI_PATH_INFO_DETAIL_ID = "/" + TABLE_NAME
            + "/detail/#";

        public static final String COLUMN_PEERURI = "peer_uri";
        public static final String COLUMN_PEERFILE_PUBLIC = "peerfile_public";

        public static final String COLUMN_STABLE_ID = "stable_id";
    }

    public static abstract class ConversationEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversation";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_PARTICIPANTS = "participants";
        public static final String COLUMN_CONVERSATION_ID = "conversation_id";
        public static final String COLUMN_DISABLED = "disabled";
    }

    public static abstract class ConversationEventEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversation_event";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";
        public static final String COLUMN_EVENT = "event";
        public static final String COLUMN = "name";
        public static final String COLUMN_CONVERSATION_ID = "conversation_id";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_PARTICIPANTS = "participants";
        public static final String COLUMN_CONTENT = "content";
    }

    public static abstract class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String URI_PATH_INFO_CONTEXT = "/" + TABLE_NAME
            + "/conversation/*";//URI used for matcher
        public static final String URI_PATH_INFO_CONTEXT_URI_BASE = "/"
            + TABLE_NAME + "/conversation/";//URL used to construct query URI

        public static final String URI_PATH_INFO_CONTEXT_ID = URI_PATH_INFO_CONTEXT + "/#";

        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/";

        // for content provider insert() call
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_CONTEXT_ID = "conversation_id";
        public static final String COLUMN_CBC_ID = DatabaseContracts.COLUMN_CBC_ID;

        // for now only text is supported
        public static final String COLUMN_MESSAGE_TYPE = "type";
        // id of peer contact. use your own id or "self" for outgoing message
        public static final String COLUMN_SENDER_ID = "sender_id";
        // Message content text
        public static final String COLUMN_MESSAGE_TEXT = "text";
        // sent or receive time
        public static final String COLUMN_MESSAGE_TIME = "time";
        // Whether the message has been read/presented, default 0 means not read
        public static final String COLUMN_MESSAGE_READ = "read";

        public static final String COLUMN_MESSAGE_DELIVERY_STATUS = "outgoing_message_status";
        public static final String COLUMN_EDIT_STATUS = "edit_status";
        public static final String COLUMN_INCOMING_MESSAGE_STATUS = "incoming_message_status";
    }

    public static abstract class MessageEventEntry implements BaseColumns {
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_EVENT = "event";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TIME = "time";
        public static final String TABLE_NAME = "message_event";
    }

    public static abstract class CallEntry implements BaseColumns {
        public static final String TABLE_NAME = "call";
        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CONVERSATION_EVENT_ID = "conversation_event_id";
        public static final String COLUMN_CONTVERSATION_ID = "conversation_id";
        public static final String COLUMN_CBC_ID = DatabaseContracts.COLUMN_CBC_ID;
        public static final String COLUMN_CALL_ID = "call_id";
        public static final String COLUMN_PEER_ID = "peer_id";
        public static final String COLUMN_DIRECTION = "direction";
        // The "selfContact" of the CALL
        public static final String COLUMN_ANSWER_TIME = "answer_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_TIME = "time";
    }

    public static abstract class CallEventEntry implements BaseColumns {
        public static final String TABLE_NAME = "call_event";

        public static final String COLUMN_CALL_ID = "call_id";
        public static final String COLUMN_EVENT = "event";
        public static final String COLUMN_TIME = "time";
    }

    public static abstract class ParticipantEntry implements BaseColumns {
        public static final String TABLE_NAME = "participants";

        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";
        public static final String COLUMN_CBC_ID = DatabaseContracts.COLUMN_CBC_ID;
        public static final String COLUMN_CONTACT_ID = "openpeer_contact_id";
    }

    public static abstract class PeerfileEntry implements BaseColumns {
        public static final String TABLE_NAME = "peerfile_public";

        public static final String COLUMN_PEERURI = "peer_uri";
        public static final String COLUMN_PEERFILE_PUBLIC = "peerfile_public";
    }

    public static abstract class IdentityProviderEntry implements BaseColumns {
        public static final String TABLE_NAME = "identity_provider";
        public static final String COLUMN_DOMAIN = "domain";
    }

    public static abstract class WindowViewEntry implements BaseColumns {
        public static final String TABLE_NAME = "conversations";
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/";
        public static final String URI_PATH_INFO_CONTEXT = "/" + TABLE_NAME;

        public static final int INFO_ID_PATH_POSITION = 1;
        public static final String COLUMN_CONVERSATION_TYPE = "type";
        public static final String COLUMN_CBC_ID = DatabaseContracts.COLUMN_CBC_ID;
        public static final String COLUMN_CONVERSATION_ID = "conversation_id";
        public static final String COLUMN_AVATAR_URI = "avatar_uri";
        // This is window id based on participants
        public static final String COLUMN_LAST_READ_MSG_ID = "lrm_id";
        public static final String COLUMN_LAST_MESSAGE = "last_message";
        public static final String COLUMN_LAST_MESSAGE_TIME = "last_message_time";
        public static final String COLUMN_USER_ID = "openpeer_contact_id";
        public static final String COLUMN_PARTICIPANT_NAMES = "name";
        public static final String COLUMN_UNREAD_COUNT = "unread_count";
        public static final String COLUMN_ROLODEX_ID = "rolodex_id";
    }

    /**
     * View to get all the information of a contact
     */
    public static abstract class ContactsViewEntry implements BaseColumns {
        public static final String TABLE_NAME = "op_contacts";
        public static final String URI_PATH_CONTACTS = "/contacts";
        public static final String URI_PATH_CONTACTS_ID = "/contacts/#";

        public static final String URI_PATH_OP_CONTACTS = "/contacts/openpeer";
        public static final String URI_PATH_OP_CONTACTS_ID = "/contacts/openpeer/#";

        public static final String URI_PATH_INFO = "/" + TABLE_NAME;
        public static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/#";

        public static final String COLUMN_USER_ID = "user_id";
        // HashCode of identityUri.
        public static final String COLUMN_ASSOCIATED_IDENTITY_ID = "associated_identity_id";
        // public static final String COLUMN_IDENTITY_CONTACT_ID =
        // "identity_contact_id";
        public static final String COLUMN_IDENTITY_URI = "identity_uri";
        public static final String COLUMN_IDENTITY_PROVIDER = "identity_provider";
        public static final String COLUMN_AVATAR_URI = "avatar_uri";

        public static final String COLUMN_CONTACT_NAME = "name";
        public static final String COLUMN_URL = "profile_url";
        public static final String COLUMN_VPROFILE_URL = "vprofile_url";

        // colums for identity contact
        public static final String COLUMN_STABLE_ID = "stable_id";
        public static final String COLUMN_PEERFILE_PUBLIC = "peerfile_public";
        public static final String COLUMN_IDENTITY_PROOF_BUNDLE = "identity_proof_bundle";
        public static final String COLUMN_PRORITY = "priority";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_LAST_UPDATE_TIME = "last_update_time";
        public static final String COLUMN_EXPIRE = "expire";

    }

}