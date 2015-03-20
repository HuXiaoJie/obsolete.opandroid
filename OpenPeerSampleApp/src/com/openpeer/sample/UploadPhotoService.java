package com.openpeer.sample;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.conversation.FileShareSystemMessage;
import com.openpeer.sample.events.FileUploadEvent;
import com.openpeer.sample.util.FileUtil;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPConversationManager;
import com.openpeer.sdk.model.HOPDataManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class UploadPhotoService extends IntentService {

    public static final String TAG = UploadPhotoService.class.getSimpleName();

    public static final String ACTION_UPLOAD = "com.openpeer.upload";

    public static final String TAG_FILE_NAME = "fileName";
    public static final String TAG_CONERSATION_ID = "conversationId";
    public static final String TAG_TEXT = "text";

    public UploadPhotoService() {
        super("PhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        Uri uri = intent.getData();
//        Uri uri = (Uri) bundle.getSerializable(TAG_FILE_NAME);
        String conversationId = bundle.getString(TAG_CONERSATION_ID);
        upload(uri, conversationId);
    }

    void upload(final Uri uri, final String conversationId) {
        Log.d(TAG, "upload uri " + uri + " conversationId " + conversationId);
        final HOPConversation conversation = HOPConversationManager.getInstance().
                getConversationById(conversationId);
        String imageCacheFileName = "" + uri.hashCode();
        final String path = PhotoHelper.getImageCachePath(imageCacheFileName);
        final String thumbNailPath = PhotoHelper.getThumnailPath(imageCacheFileName);
        final OPMessage message = FileShareSystemMessage.createStoreMessage("", uri.toString(),
                "file:" + thumbNailPath, "uploading");

        Bitmap thumbNail = PhotoHelper.createThumbnail(uri);
        byte[] thumbNailData;
        ByteArrayOutputStream thumbNailStream = new ByteArrayOutputStream();
        thumbNail.compress(Bitmap.CompressFormat.PNG, 100, thumbNailStream);
        thumbNailData = thumbNailStream.toByteArray();
        FileUtil.saveFile(thumbNailPath, thumbNailData);
        HOPDataManager.getInstance().saveMessage(message, conversation.getId(),
                conversation.getParticipantInfo());
        // Compress image to lower quality scale 1 - 100
        Bitmap bitmap = PhotoHelper.getBitmap(uri);
        final int imageSize = bitmap.getByteCount();
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        final byte[] data = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ParseFile thumbNailFile = new ParseFile(thumbNailData);
        try {
            thumbNailFile.save();
        } catch (ParseException e) {
            e.printStackTrace();
            message.setMessage(String.format(FileShareSystemMessage
                            .MESSAGE_STORE_FORMAT, "",
                    "file://" + path,
                    "file://" + thumbNailPath,
                    "failed"));
            HOPDataManager.getInstance().updateMessage(message, conversation);
        }
        final ParseFile parseFile = new ParseFile(data);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "image upload done");
                final ParseObject imgupload = new ParseObject("SharedPhoto");

                // Create a column named "ImageName" and set the string
                imgupload.put("imageName", uri.toString());

                // Create a column named "ImageFile" and insert the image
                imgupload.put("imageFile", parseFile);
                imgupload.put("fileID", message.getMessageId());

                // Create the class and the columns
                imgupload.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            new FileUploadEvent(100, uri.toString(),
                                    imgupload.getObjectId()).post();
                            if (!TextUtils.isEmpty(conversationId)) {
                                //Send system message
                                HOPConversationManager.getInstance().
                                        getConversationById(conversationId).sendMessage(
                                        FileShareSystemMessage.createFileShareSystemMessage
                                                (message.getMessageId(),
                                                        imageSize, width, height), false);
                                message.setMessage(String.format(FileShareSystemMessage
                                                .MESSAGE_STORE_FORMAT, imgupload.getObjectId(),
                                        "file://" + path,
                                        "file://" + thumbNailPath,
                                        "uploaded"));
                                HOPDataManager.getInstance().updateMessage(message, conversation);
                                FileUtil.saveFile(path, data);
                            }
                        } else {
                            message.setMessage(String.format(FileShareSystemMessage
                                            .MESSAGE_STORE_FORMAT, imgupload.getObjectId(),
                                    "file://" + path,
                                    "file://" + thumbNailPath,
                                    "failed"));
                            HOPDataManager.getInstance().updateMessage(message, conversation);
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer value) {
                new FileUploadEvent(value, uri.toString(), null).post();
            }
        });
    }

}
