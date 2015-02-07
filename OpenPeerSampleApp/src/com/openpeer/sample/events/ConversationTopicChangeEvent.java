package com.openpeer.sample.events;

import com.openpeer.sdk.model.OPConversation;

public class ConversationTopicChangeEvent extends BaseEvent{
    OPConversation conversation;
    String newTopic;

    public ConversationTopicChangeEvent(OPConversation conversation, String newTopic) {
        this.conversation = conversation;
        this.newTopic = newTopic;
    }

    public OPConversation getConversation() {
        return conversation;
    }

    public String getNewTopic() {
        return newTopic;
    }
}
