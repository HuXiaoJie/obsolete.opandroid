package com.openpeer.sdk.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.javaapi.OPSystemMessage;
import com.openpeer.sdk.app.OPDataManager;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * {"system":{"callStatus":{"$id":"adf","status":"placed","mediaType":"audio",
 * "callee":"peer://opp.me/kadjfadkfj","error":{"$id":404}}}
 */
public class SystemMessage<T> {

    T system;

    public T getSystemObject() {
        return system;
    }

    public SystemMessage() {
    }

    public SystemMessage(T t) {
        system = t;
    }

    public static OPMessage getContactsRemovedSystemMessage(String[] removedContacts){
        ContactsRemovedSystemMessage contactsRemovedSystemMessage= new ContactsRemovedSystemMessage(removedContacts);
        SystemMessage<ContactsRemovedSystemMessage> systemMessage =
            new SystemMessage<ContactsRemovedSystemMessage>(contactsRemovedSystemMessage);

        OPMessage message = new OPMessage(
            OPDataManager.getInstance().getSharedAccount().getSelfContactId(),
            OPSystemMessage.getMessageType(),
            systemMessage.toJson(),
            System.currentTimeMillis(),
            UUID.randomUUID().toString());
        return message;
    }
    public static SystemMessage parseSystemMessage(String jsonBlob) {
        return getGson().fromJson(jsonBlob,
                                  SystemMessage.class);
    }

    public String toJson() {
        return GsonFactory.getGson().toJson(this);
    }

    static class SystemMessageDeserializer implements JsonDeserializer<SystemMessage> {
        @Override
        public SystemMessage deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context) throws
            JsonParseException {
            SystemMessage message = new SystemMessage();
            JsonObject object = json.getAsJsonObject().getAsJsonObject("system");
            System.out.println("object " + object.toString());
            if (object.has("callStatus")) {

                CallSystemMessage callSystemMessage = context.deserialize(object,
                                                                          CallSystemMessage.class);
                message.system = callSystemMessage;
            } else if (object.has("contactsRemoved")) {
                ContactsRemovedSystemMessage contactsRemovedSystemMessage =
                    context.deserialize(object, ContactsRemovedSystemMessage.class);
                message.system = contactsRemovedSystemMessage;
            }
            return message;
        }
    }

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SystemMessage.class, new SystemMessageDeserializer());
            gson = gsonBuilder.create();
        }
        return gson;
    }
}
