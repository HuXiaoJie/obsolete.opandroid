package com.openpeer.sample.conversation;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.model.HOPSystemMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ConversationSwitchSystemMessage {
    public static final String KEY_CONVERSATION_SWITCH = "conversationSwitch";
    public static final String KEY_FROM_CONVERSATION_ID = "from";
    public static final String KEY_TO_CONVERSATION_ID = "to";

    public static OPMessage getConversationSwitchMessage(String fromConversationId,
                                                         String toConversationId) {
        try {
            JSONObject object = new JSONObject();
            object.put(KEY_FROM_CONVERSATION_ID, fromConversationId);
            object.put(KEY_TO_CONVERSATION_ID, toConversationId);
            JSONObject object1 = new JSONObject();
            object1.put(KEY_CONVERSATION_SWITCH, object);
            return HOPSystemMessage.getSystemMessage(object1);
        } catch(JSONException e) {

        }
        return null;
    }
}
