package com.openpeer.sample.delegates;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.events.ConversationComposingStatusChangeEvent;
import com.openpeer.sample.events.ConversationContactsChangeEvent;
import com.openpeer.sample.events.ConversationSwitchEvent;
import com.openpeer.sample.events.ConversationTopicChangeEvent;
import com.openpeer.sdk.model.CallSystemMessage;
import com.openpeer.sdk.model.ConversationDelegate;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;

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
    public void onContactComposingStateChanged(OPConversation conversation,
                                               ComposingStates composingStates,
                                               OPUser contact) {
       new ConversationComposingStatusChangeEvent(conversation,
                                                                              contact,
                                                                              composingStates).post();
    }

    @Override
    public boolean onNewMessage(OPConversation conversation, OPMessage message) {
        return false;
    }

    @Override
    public boolean onPushMessageRequired(OPConversation conversation, OPMessage message) {
        return false;
    }


    @Override
    public boolean onContactsChanged(OPConversation conversation) {
        new ConversationContactsChangeEvent(conversation).post();
        return true;
    }

    @Override
    public boolean onConversationTopicChanged(OPConversation conversation, String newTopic) {
        new ConversationTopicChangeEvent(conversation, newTopic).post();
        return true;
    }

    @Override
    public boolean onConversationSwitch(OPConversation fromConversation, OPConversation
        toConversation) {
        new ConversationSwitchEvent(fromConversation, toConversation).post();
        return false;
    }

    @Override
    public boolean onCallSystemMessageReceived(OPConversation conversation,
                                               CallSystemMessage message,
                                               OPUser sender) {
        return false;
    }
}
