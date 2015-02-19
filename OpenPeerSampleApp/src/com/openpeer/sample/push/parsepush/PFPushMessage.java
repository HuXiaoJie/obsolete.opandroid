package com.openpeer.sample.push.parsepush;

public class PFPushMessage {
    public static final String MESSAGE_TYPE_TEXT="text";
    public static final String MESSAGE_TYPE_CALL_STATE="system/call";
    public static final String MESSAGE_TYPE_CONTACTS_REMOVED="system/contactsRemoved";

    public static final String KEY_ALERT="alert";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_TO="to";
    public static final String KEY_PEER_URI="peerURI";
    public static final String KEY_SENDER_NAME="senderName";
    public static final String KEY_PEER_URIS="peerURIs";
    public static final String KEY_LOCATION="location";
    public static final String KEY_MESSAGE_TYPE="messageType";
    public static final String KEY_MESSAGE_ID="messageId";
    public static final String KEY_REPLACES_MESSAGE_ID="replacesMessageId";
    public static final String KEY_DATE ="date";
    public static final String KEY_CONVERSATION_ID="conversationId";
    public static final String KEY_CONVERSATION_TYPE="conversationType";
    public static final String KEY_SOUND="sound";

    //for calls
    public static final String KEY_SYSTEM_MESSAGE_TYPE="systemMessageType";
    public static final String KEY_CALL_STATE="callState";
    public static final String KEY_CALL_ID="callId";
    public static final String KEY_CALL_MEDIA_TYPE="mediaType";
}
