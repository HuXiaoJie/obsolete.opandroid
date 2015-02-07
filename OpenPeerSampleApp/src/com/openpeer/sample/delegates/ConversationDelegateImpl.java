package com.openpeer.sample.delegates;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.ContactConnectionStates;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPApplication;
import com.openpeer.sample.events.ConversationComposingStatusChangeEvent;
import com.openpeer.sample.events.ConversationContactsChangeEvent;
import com.openpeer.sample.events.ConversationSwitchEvent;
import com.openpeer.sample.events.ConversationTopicChangeEvent;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.CallManager;
import com.openpeer.sdk.model.CallSystemMessage;
import com.openpeer.sdk.model.ConversationDelegate;
import com.openpeer.sdk.model.ConversationManager;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.SystemMessage;
import com.openpeer.sdk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConversationDelegateImpl implements ConversationDelegate {

    private static ConversationDelegateImpl instance;

    public static ConversationDelegateImpl getInstance() {
        if (instance == null) {
            instance = new ConversationDelegateImpl();
        }
        return instance;
    }

    private ConversationDelegateImpl() {
    }

    @Override
    public void onConversationContactStatusChanged(OPConversation conversation,
                                                   ComposingStates composingStates,
                                                   OPUser contact) {
        new ConversationComposingStatusChangeEvent(conversation,
                                                   contact,
                                                   composingStates).post();
    }

    @Override
    public boolean onConversationMessage(OPConversation conversation, OPMessage message) {
        if (message.getMessageType().equals(OPMessage.TYPE_JSON_SYSTEM_MESSAGE)) {
            OPContact opContact = message.getFrom();
            OPUser sender = OPDataManager.getInstance().
                getUserByPeerUri(opContact.getPeerURI());
            String messageText = message.getMessage();
            try {
                JSONObject systemObject = new JSONObject(messageText).getJSONObject
                    (SystemMessage.KEY_ROOT);

                handleSystemMessage(conversation, sender, systemObject,
                                    message.getTime().toMillis(false));
            } catch(JSONException e) {
                OPLogger.error(OPLogLevel.LogLevel_Basic, "Error:invalid system message " +
                    message.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean onConversationPushMessage(OPConversation conversation, OPMessage message,
                                             OPUser contact) {
        OPApplication.getPushService().onConversationThreadPushMessage(conversation, message,
                                                                        contact);
        return true;
    }


    @Override
    public boolean onConversationContactsChanged(OPConversation conversation) {
        new ConversationContactsChangeEvent(conversation).post();
        return true;
    }


    @Override
    public void onConversationMessageDeliveryStateChanged(OPConversation conversation, OPMessage
        message) {

    }

    @Override
    public void onConversationContactConnectionStateChanged(OPConversation conversation,
                                                            OPUser contact,
                                                            ContactConnectionStates state) {

    }

    public boolean onConversationTopicChanged(OPConversation conversation, String newTopic) {
        new ConversationTopicChangeEvent(conversation, newTopic).post();
        return true;
    }

    public boolean onCallSystemMessageReceived(OPConversation conversation,
                                               CallSystemMessage message,
                                               OPUser sender) {
        return false;
    }

    public void handleSystemMessage(OPConversation conversation, OPUser sender, JSONObject systemMessage,
                                    long time) {
        try {
            if (systemMessage.has(SystemMessage.KEY_CALL_STATUS)) {
                JSONObject callSystemMessage = systemMessage
                    .getJSONObject(SystemMessage.KEY_CALL_STATUS);
                CallManager.getInstance().
                    handleCallSystemMessage(callSystemMessage,
                                            sender,
                                            conversation.getConversationId(),
                                            time);

            } else if (systemMessage.has(SystemMessage.KEY_CONTACTS_REMOVED)) {
                JSONArray contactsRemovedMessage = systemMessage
                    .getJSONArray(SystemMessage.KEY_CONTACTS_REMOVED);
                String selfPeerUri = OPDataManager.getInstance().getCurrentUser().getPeerUri();
                for (String peerUri : (String[]) JSONUtils.toArray(contactsRemovedMessage)) {
                    if (peerUri.equals(selfPeerUri)) {
                        conversation.setDisabled(true);
                        new ConversationContactsChangeEvent(conversation).post();
                    }
                }
            } else if (systemMessage.has(SystemMessage.KEY_CONVERSATION_SWITCH)) {
                JSONObject object = systemMessage.
                    getJSONObject(SystemMessage.KEY_CONVERSATION_SWITCH);
                OPConversation from = ConversationManager.getInstance().getConversationById(
                    object.getString(SystemMessage.KEY_FROM_CONVERSATION_ID));

                if (from != null && conversation != null) {
                    new ConversationSwitchEvent(from, conversation).post();
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

}
