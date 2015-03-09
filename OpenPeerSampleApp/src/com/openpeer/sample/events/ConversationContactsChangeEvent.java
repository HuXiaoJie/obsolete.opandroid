package com.openpeer.sample.events;

import com.openpeer.sdk.model.HOPConversation;

public class ConversationContactsChangeEvent extends BaseEvent{
    HOPConversation conversation;

    public ConversationContactsChangeEvent(HOPConversation conversation) {
        this.conversation = conversation;
    }

    public HOPConversation getConversation() {
        return conversation;
    }
}
