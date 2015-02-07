package com.openpeer.sample.events;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;

public class ConversationComposingStatusChangeEvent extends BaseEvent{
    OPConversation conversation;
    OPUser user;
    ComposingStates nState;

    public ConversationComposingStatusChangeEvent(OPConversation conversation, OPUser user,
                                                  ComposingStates nState) {
        this.conversation = conversation;
        this.user = user;
        this.nState = nState;
    }

    public OPConversation getConversation() {
        return conversation;
    }

    public OPUser getUser() {
        return user;
    }

    public ComposingStates getnState() {
        return nState;
    }
}
