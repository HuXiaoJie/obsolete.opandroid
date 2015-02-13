package com.openpeer.sample.delegates;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.ContactConnectionStates;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPApplication;
import com.openpeer.sample.conversation.ConversationSwitchSystemMessage;
import com.openpeer.sample.events.ConversationComposingStatusChangeEvent;
import com.openpeer.sample.events.ConversationContactsChangeEvent;
import com.openpeer.sample.events.ConversationSwitchEvent;
import com.openpeer.sample.events.ConversationTopicChangeEvent;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.CallEvent;
import com.openpeer.sdk.model.CallSystemMessage;
import com.openpeer.sdk.model.HOPAccount;
import com.openpeer.sdk.model.HOPCall;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPConversationDelegate;
import com.openpeer.sdk.model.HOPConversationManager;
import com.openpeer.sdk.model.HOPSystemMessage;
import com.openpeer.sdk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HOPConversationDelegateImpl implements HOPConversationDelegate {

    private static HOPConversationDelegateImpl instance;

    public static HOPConversationDelegateImpl getInstance() {
        if (instance == null) {
            instance = new HOPConversationDelegateImpl();
        }
        return instance;
    }

    private HOPConversationDelegateImpl() {
    }

    @Override
    public void onConversationContactStatusChanged(HOPConversation conversation,
                                                   ComposingStates composingStates,
                                                   HOPContact HOPContact) {
        new ConversationComposingStatusChangeEvent(conversation,
                                                   HOPContact,
                                                   composingStates).post();
    }

    @Override
    public boolean onConversationMessage(HOPConversation conversation, OPMessage message) {
        if (message.getMessageType().equals(OPMessage.TYPE_JSON_SYSTEM_MESSAGE)) {
            OPContact opContact = message.getFrom();
            HOPContact sender = HOPDataManager.getInstance().
                getUserByPeerUri(opContact.getPeerURI());
            String messageText = message.getMessage();
            try {
                JSONObject systemObject = new JSONObject(messageText).getJSONObject
                    (HOPSystemMessage.KEY_ROOT);

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
    public boolean onConversationPushMessage(HOPConversation conversation, OPMessage message,
                                             HOPContact HOPContact) {
        OPApplication.getPushService().onConversationThreadPushMessage(conversation, message,
                                                                       HOPContact);
        return true;
    }


    @Override
    public boolean onConversationContactsChanged(HOPConversation conversation) {
        new ConversationContactsChangeEvent(conversation).post();
        return true;
    }


    @Override
    public void onConversationMessageDeliveryStateChanged(HOPConversation conversation, OPMessage
        message) {

    }

    @Override
    public void onConversationContactConnectionStateChanged(HOPConversation conversation,
                                                            HOPContact HOPContact,
                                                            ContactConnectionStates state) {

    }

    public boolean onConversationTopicChanged(HOPConversation conversation, String newTopic) {
        new ConversationTopicChangeEvent(conversation, newTopic).post();
        return true;
    }

    public static void handleSystemMessage(HOPConversation conversation, HOPContact sender,
                                           JSONObject systemMessage,
                                           long time) {
        try {
            if (systemMessage.has(HOPSystemMessage.KEY_CALL_STATUS)) {
                JSONObject callSystemMessage = systemMessage
                    .getJSONObject(HOPSystemMessage.KEY_CALL_STATUS);
                handleCallSystemMessage(callSystemMessage,
                                        sender,
                                        conversation.getConversationId(),
                                        time);

            } else if (systemMessage.has(HOPSystemMessage.KEY_CONTACTS_REMOVED)) {
                JSONArray contactsRemovedMessage = systemMessage
                    .getJSONArray(HOPSystemMessage.KEY_CONTACTS_REMOVED);
                String selfPeerUri = HOPAccount.selfContact().getPeerUri();
                for (Object peerUri : (Object[])JSONUtils.toArray(contactsRemovedMessage)) {
                    if (peerUri.equals(selfPeerUri)) {
                        conversation.setDisabled(true);
                        new ConversationContactsChangeEvent(conversation).post();
                    }
                }
            } else if (systemMessage.has(ConversationSwitchSystemMessage.KEY_CONVERSATION_SWITCH)) {
                JSONObject object = systemMessage.
                    getJSONObject(ConversationSwitchSystemMessage.KEY_CONVERSATION_SWITCH);
                HOPConversation from = HOPConversationManager.getInstance().getConversationById(
                    object.getString(ConversationSwitchSystemMessage.KEY_FROM_CONVERSATION_ID));

                if (from != null && conversation != null) {
                    new ConversationSwitchEvent(from, conversation).post();
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public static void handleCallSystemMessage(JSONObject message, HOPContact user,
                                               String conversationId,
                                               long timestamp) {
        try {
            String callId = message.getString(CallSystemMessage.KEY_ID);
            HOPCall call = HOPCall.findCallById(callId);
            String calleeUrl = message.getString(CallSystemMessage.KEY_CALL_STATUS_CALLEE);
            if (calleeUrl.equals(HOPAccount.selfContact().getPeerUri())) {
                HOPDataManager.getInstance().saveCall(message.getString(CallSystemMessage.KEY_ID),
                                                      conversationId,
                                                      user.getUserId(),
                                                      HOPCall.DIRECTION_INCOMING,
                                                      message.getString(CallSystemMessage
                                                                            .KEY_CALL_STATUS_MEDIA_TYPE));
                CallEvent event = new CallEvent(callId,
                                                message.getString(CallSystemMessage
                                                                      .KEY_CALL_STATUS_STATUS),
                                                timestamp);
                HOPDataManager.getInstance().saveCallEvent(callId, conversationId, event);
            } else {
                if (call == null) {// i'm tototally not connected with the peer
                    //couldn't find call in memory. try to save call
                    HOPDataManager.getInstance().saveCall(
                        message.getString(CallSystemMessage.KEY_ID),
                        conversationId,
                        user.getUserId(),
                        HOPCall.DIRECTION_INCOMING,
                        message.getString(CallSystemMessage.KEY_CALL_STATUS_MEDIA_TYPE));
                    CallEvent event = new CallEvent(callId,
                                                    message.getString(CallSystemMessage
                                                                          .KEY_CALL_STATUS_STATUS),
                                                    timestamp);
                    HOPDataManager.getInstance().saveCallEvent(callId, conversationId, event);
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
