package com.openpeer.sample.events;

import com.openpeer.sdk.model.OPConversation;

public class ConversationContactsChangeEvent extends BaseEvent{
    OPConversation conversation;

    public ConversationContactsChangeEvent(OPConversation conversation) {
        this.conversation = conversation;
    }

    public OPConversation getConversation() {
        return conversation;
    }
}
