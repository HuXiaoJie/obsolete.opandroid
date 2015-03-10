package com.openpeer.sample;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class UploadPhotoService extends IntentService {

    public static final String TAG = UploadPhotoService.class.getSimpleName();

    public static final String ACTION_UPLOAD="com.openpeer.sample.upload";
    public static final String SERVICE_CALLBACK = "agilie.fandine.service.SERVICE_CALLBACK";

    public static final String TAG_FILE_NAMES = "tag_file_names";
    public static final String TAG_TEXT = "tag_text";

    private ResultReceiver mCallback;
    private Intent mOriginalRequestIntent;
    private ArrayList<String> photosList;
    private ArrayList<String> fileNames;
    private int upLoadedPhotos = 0;

    private String orderId;
    private String restaurantId;
    private String mealId;
    private int rating;
    private String text;

    private SecureRandom random = new SecureRandom();

    public UploadPhotoService() {
        super("PhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mOriginalRequestIntent = intent;

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        mCallback = bundle.getParcelable(SERVICE_CALLBACK);
        photosList = (ArrayList<String>) bundle.getSerializable(TAG_FILE_NAMES);
        fileNames = new ArrayList<String>();

        if (photosList.size() > 0) {
            for (final String photoName : photosList) {
                String fileName = nextFileName();
                fileNames.add(fileName);
            }
        }
    }

    public String nextFileName() {
        return new BigInteger(130, random).toString(32) + ".jpg";
    }
}
