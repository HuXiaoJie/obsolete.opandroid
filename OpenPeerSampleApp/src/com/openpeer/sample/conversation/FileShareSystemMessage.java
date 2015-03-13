package com.openpeer.sample.conversation;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPSystemMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FileShareSystemMessage {
    public static final String KEY_FILE_SHARE = "fileShare";
    public static final String KEY_OBJECT_id = "objectId";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_SIZE = "size";
    public static final String KEY_IMAGE_INFO = "imageInfo";
    public static final String KEY_IMAGE_WIDTH = "width";
    public static final String KEY_IMAGE_HEIGHT = "height";

    public static final String MESSAGE_FORMAT = "{\"system\":{\"fileShare\":{\"objectId\":\"%s\"," +
            "\"fileType\":\"image\",\"size\":%d,\"imageInfo\":{\"width\":%d,\"height\":%d}}}}";

    public static final String MESSAGE_STORE_FORMAT = "{\"objectId\":\"%s\",\"uri\":\"%s\"," +
            "\"thumbNail\":\"%s\"," +
            "\"status\":\"%s\"}";

    public static String createStoreMessageText(String objectId, String uri, String thumbNailUri,
                                                String status) {
        return String.format(MESSAGE_STORE_FORMAT, objectId, uri, thumbNailUri, status);
    }

    public static OPMessage createStoreMessage(String objectId, String uri, String thumbNailUri,
                                               String status) {
        String messageText = String.format(MESSAGE_STORE_FORMAT, objectId, uri, thumbNailUri,
                status);
        com.openpeer.javaapi.OPMessage message = HOPConversation.createMessage(com.openpeer.javaapi
                .OPMessage.TYPE_INERNAL_FILE_PHOTO, messageText);
        return message;
    }

    public static final OPMessage createFileShareSystemMessage(String objectId, int bytesCount,
                                                               int width, int height) {
        String messageText = String.format(MESSAGE_FORMAT, objectId, bytesCount, width, height);
        return HOPSystemMessage.createSystemMessage(messageText);
    }
}
