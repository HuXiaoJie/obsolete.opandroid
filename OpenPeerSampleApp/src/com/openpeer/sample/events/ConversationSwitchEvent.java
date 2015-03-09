package com.openpeer.sample.events;

import com.openpeer.sdk.model.HOPConversation;

public class ConversationSwitchEvent extends BaseEvent{
    HOPConversation fromConversation;
    HOPConversation toConversation;

    public ConversationSwitchEvent(HOPConversation fromConversation, HOPConversation toConversation) {
        this.fromConversation = fromConversation;
        this.toConversation = toConversation;
    }

    public HOPConversation getFromConversation() {
        return fromConversation;
    }

    public HOPConversation getToConversation() {
        return toConversation;
    }
}
