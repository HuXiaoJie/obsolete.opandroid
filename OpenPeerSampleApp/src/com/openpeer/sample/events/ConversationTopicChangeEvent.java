package com.openpeer.sample.events;

import com.openpeer.sdk.model.HOPConversation;

public class ConversationTopicChangeEvent extends BaseEvent{
    HOPConversation conversation;
    String newTopic;

    public ConversationTopicChangeEvent(HOPConversation conversation, String newTopic) {
        this.conversation = conversation;
        this.newTopic = newTopic;
    }

    public HOPConversation getConversation() {
        return conversation;
    }

    public String getNewTopic() {
        return newTopic;
    }
}
