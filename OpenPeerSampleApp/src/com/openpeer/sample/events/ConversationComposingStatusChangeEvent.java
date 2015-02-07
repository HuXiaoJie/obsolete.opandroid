package com.openpeer.sample.events;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.HOPConversation;

public class ConversationComposingStatusChangeEvent extends BaseEvent{
    HOPConversation conversation;
    HOPContact user;
    ComposingStates nState;

    public ConversationComposingStatusChangeEvent(HOPConversation conversation, HOPContact user,
                                                  ComposingStates nState) {
        this.conversation = conversation;
        this.user = user;
        this.nState = nState;
    }

    public HOPConversation getConversation() {
        return conversation;
    }

    public HOPContact getUser() {
        return user;
    }

    public ComposingStates getnState() {
        return nState;
    }
}
