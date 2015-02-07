package com.openpeer.sample.events;

import com.openpeer.sdk.model.OPConversation;

public class ConversationSwitchEvent extends BaseEvent{
    OPConversation fromConversation;
    OPConversation toConversation;

    public ConversationSwitchEvent(OPConversation fromConversation, OPConversation toConversation) {
        this.fromConversation = fromConversation;
        this.toConversation = toConversation;
    }

    public OPConversation getFromConversation() {
        return fromConversation;
    }

    public OPConversation getToConversation() {
        return toConversation;
    }
}
