package com.openpeer.sdk.model;

import android.text.format.Time;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.javaapi.OPSystemMessage;
import com.openpeer.sdk.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * {"system":{"callStatus":{"$id":"adf","status":"placed","mediaType":"audio",
 * "callee":"peer://opp.me/kadjfadkfj","error":{"$id":404}}}
 */
public class HOPSystemMessage {
    public static final String KEY_ROOT = "system";
    public static final String KEY_CALL_STATUS = "callStatus";
    public static final String KEY_CONTACTS_REMOVED = "contactsRemoved";

    public static OPMessage getContactsRemovedSystemMessage(String removedContacts[]) {
        JSONObject system = contactsRemovedMessage(removedContacts);
        if (system != null) {
            OPMessage message = new OPMessage(
                HOPAccount.selfContactId(),
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

    public static OPMessage getCallSystemMessage(String status, HOPCall call) {
        int callClosedReason = -1;
        switch (status){
        case CallSystemMessage.STATUS_HUNGUP:
            if (Time.isEpoch(call.getAnswerTime())) {
                callClosedReason = 404;
            } else {
                callClosedReason = call.getClosedReason();
            }
            break;
        }

        String mediaType = call.hasVideo() ? CallSystemMessage.MEDIATYPE_VIDEO :
            CallSystemMessage.MEDIATYPE_AUDIO;
        try {
            JSONObject callSystemMessage = getSystemObject(CallSystemMessage(
                    call.getCallID(),
                    status,
                    mediaType,
                    call.getPeer().getPeerUri(),
                    callClosedReason));

            OPMessage message = new OPMessage(
                    HOPAccount.selfContactId(),
                    OPSystemMessage.getMessageType(),
                    callSystemMessage.toString(),
                    System.currentTimeMillis(),
                    UUID.randomUUID().toString());
            return message;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
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
            systemObject.put(KEY_CALL_STATUS, object);
            return systemObject;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static JSONObject getSystemObject(JSONObject object) throws JSONException {
        JSONObject systemObject = new JSONObject();
        systemObject.put(KEY_ROOT, object);
        return systemObject;
    }

    public static OPMessage getSystemMessage(JSONObject system) throws JSONException{
        if (system != null) {
            OPMessage message = new OPMessage(
                HOPAccount.selfContactId(),
                OPSystemMessage.getMessageType(),
                getSystemObject(system).toString(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString());
            return message;
        } else {
            return null;
        }
    }
    public static OPMessage createSystemMessage(String messageText){
        OPMessage message = new OPMessage(
                HOPAccount.selfContactId(),
                OPSystemMessage.getMessageType(),
                messageText,
                System.currentTimeMillis(),
                UUID.randomUUID().toString());
        return message;
    }
}
