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


import com.openpeer.sample.conversation.FileShareSystemMessage;
import com.openpeer.sample.events.FileUploadEvent;
import com.openpeer.sdk.model.HOPConversationManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        final String fileName = uri.getLastPathSegment();
        String path = PhotoHelper.getPath(uri);
        Bitmap bitmap = PhotoHelper.getBitmap(path);
        final int imageSize = bitmap.getByteCount();
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] data = stream.toByteArray();
        final ParseFile parseFile = new ParseFile(data);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "image upload done");
                final ParseObject imgupload = new ParseObject("ImageUpload");

                // Create a column named "ImageName" and set the string
                imgupload.put("ImageName", fileName);

                // Create a column named "ImageFile" and insert the image
                imgupload.put("ImageFile", parseFile);

                // Create the class and the columns
                imgupload.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        new FileUploadEvent(100, fileName, imgupload.getObjectId());
                        if (!TextUtils.isEmpty(conversationId)) {
                            //Send system message
                            HOPConversationManager.getInstance().
                                    getConversationById(conversationId).sendMessage(
                                    FileShareSystemMessage.createFileShareSystemMessage(imgupload
                                                    .getObjectId(),
                                            imageSize, width, height), false);
                        }
                    }
                });
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer value) {
                new FileUploadEvent(value, fileName, null);
            }
        });
    }

}
