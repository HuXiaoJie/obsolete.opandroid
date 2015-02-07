package com.openpeer.sdk.model;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.javaapi.OPSystemMessage;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * {"system":{"callStatus":{"$id":"adf","status":"placed","mediaType":"audio",
 * "callee":"peer://opp.me/kadjfadkfj","error":{"$id":404}}}
 */
public class SystemMessage {
    public static final String KEY_ROOT = "system";
    public static final String KEY_CALL_STATUS = "callStatus";
    public static final String KEY_CONTACTS_REMOVED = "contactsRemoved";
    public static final String KEY_CONVERSATION_SWITCH = "conversationSwitch";

    public static final String KEY_FROM_CONVERSATION_ID = "from";
    public static final String KEY_TO_CONVERSATION_ID = "to";

    public static OPMessage getContactsRemovedSystemMessage(String removedContacts[]) {
        JSONObject system = contactsRemovedMessage(removedContacts);
        if (system != null) {
            OPMessage message = new OPMessage(
                OPDataManager.getInstance().getCurrentUserId(),
                OPSystemMessage.getMessageType(),
                system.toString(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString());
            return message;
        } else {
            return null;
        }
    }

    public static JSONObject contactsRemovedMessage(String removedContacts[]) {
        try {
            JSONArray array = JSONUtils.fromArray(removedContacts);
            JSONObject object = new JSONObject();
            object.put(KEY_CONTACTS_REMOVED, array);
            JSONObject system = new JSONObject();
            system.put(KEY_ROOT, object);
            return system;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject CallSystemMessage(String id,
                                               String status,
                                               String mediaType,
                                               String callee,
                                               int reason) {
        try {
            JSONObject object = new JSONObject();
            object.put(CallSystemMessage.KEY_ID, id);
            object.put(CallSystemMessage.KEY_CALL_STATUS_STATUS, status);
            object.put(CallSystemMessage.KEY_CALL_STATUS_MEDIA_TYPE, mediaType);
            object.put(CallSystemMessage.KEY_CALL_STATUS_CALLEE, callee);
            if (reason != -1) {
                JSONObject errorObject = new JSONObject();
                errorObject.put(CallSystemMessage.KEY_ID, reason);
                object.put(CallSystemMessage.KEY_ERROR, errorObject);
            }
            JSONObject systemObject = new JSONObject();
            systemObject.put(KEY_ROOT, object);
            return systemObject;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OPMessage getConversationSwitchMessage(String fromConversationId,
                                                         String toConversationId) {
        try {
            JSONObject object = new JSONObject();
            object.put(KEY_FROM_CONVERSATION_ID, fromConversationId);
            object.put(KEY_TO_CONVERSATION_ID, toConversationId);
            JSONObject object1 = new JSONObject();
            object1.put(KEY_CONVERSATION_SWITCH, object);
            return getSystemMessage(getSystemObject(object1));
        } catch(JSONException e) {

        }
        return null;
    }

    static JSONObject getSystemObject(JSONObject object) throws JSONException {
        JSONObject systemObject = new JSONObject();
        systemObject.put(KEY_ROOT, object);
        return systemObject;
    }

    static OPMessage getSystemMessage(JSONObject system) {
        if (system != null) {
            OPMessage message = new OPMessage(
                OPDataManager.getInstance().getCurrentUserId(),
                OPSystemMessage.getMessageType(),
                system.toString(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString());
            return message;
        } else {
            return null;
        }
    }
}
