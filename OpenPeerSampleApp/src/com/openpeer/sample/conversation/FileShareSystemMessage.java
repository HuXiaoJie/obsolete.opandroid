package com.openpeer.sample.conversation;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.model.HOPSystemMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FileShareSystemMessage {
    public static final String KEY_FILE_SHARE = "fileShare";
    public static final String KEY_OBJECT_id= "objectId";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_SIZE = "size";
    public static final String KEY_IMAGE_INFO= "imageInfo";
    public static final String KEY_IMAGE_WIDTH= "width";
    public static final String KEY_IMAGE_HEIGHT= "height";

    public static final String MESSAGE_FORMAT = "{\"system\":{\"fileShare\":{\"objectId\":\"%s\"," +
            "\"fileType\":\"image\",\"size\":%d,\"imageInfo\":{\"width\":%d,\"height\":%d}}}";

    public static final OPMessage createFileShareSystemMessage(String objectId,
                                                         int width, int height, int bytesCount) {
        String messageText = String.format(MESSAGE_FORMAT, objectId, bytesCount, width, height);
        return HOPSystemMessage.createSystemMessage(messageText);
    }
}
