package com.openpeer.sdk.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.openpeer.javaapi.CallClosedReasons;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.javaapi.OPSystemMessage;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * {"system":{"callStatus":{"$id":"adf","status":"placed","mediaType":"audio",
 * "callee":"peer://opp.me/kadjfadkfj","error":{"$id":404}}}
 */
public class SystemMessage {
    public static final String KEY_ROOT = "system";
    public static final String KEY_CALL_STATUS = "callStatus";
    public static final String KEY_CONTACTS_REMOVED = "contactsRemoved";

    public static final String KEY_CALL_STATUS_STATUS = "status";
    public static final String KEY_CALL_STATUS_MEDIA_TYPE = "mediaType";
    public static final String KEY_CALL_STATUS_CALLEE = "callee";
    public static final String KEY_ID = "$id";
    public static final String KEY_ERROR = "error";


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
            object.put(KEY_ID, id);
            object.put(KEY_CALL_STATUS_STATUS, status);
            object.put(KEY_CALL_STATUS_MEDIA_TYPE, mediaType);
            object.put(KEY_CALL_STATUS_CALLEE, callee);
            if (reason != -1) {
                JSONObject errorObject = new JSONObject();
                errorObject.put(KEY_ID, reason);
                object.put(KEY_ERROR, errorObject);
            }
            JSONObject systemObject = new JSONObject();
            systemObject.put(KEY_ROOT, object);
            return systemObject;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
