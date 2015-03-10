package com.openpeer.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class PhotoHelper {

    protected final static int THUMBNAIL_SIZE_PX = 150;
    public static final int ACTIVITY_REQUEST_CODE_TAKE_PICTURE = 10000;
    public static final int ACTIVITY_REQUEST_CODE_GET_PICTURE_FROM_STORAGE =
            ACTIVITY_REQUEST_CODE_TAKE_PICTURE + 1;
    public final static int ROTATE_90 = 90;
    public final static int ROTATE_180 = 180;
    public final static int ROTATE_270 = 270;
    public final static int ROTATE_360 = 360;
    public static final String PHOTO_PATH = "photo_%s.jpg";
    private static PhotoHelper instance;
    private Uri lastPhotoUri;

    private PhotoHelper() {
        //empty
    }

    public static PhotoHelper getInstance() {
        if (instance == null) {
            instance = new PhotoHelper();
        }
        return instance;
    }

    public void showPhotoAlert(final Activity activity) {

        Log.d("activity is: ", activity.toString());
        final boolean hasCamera = activity.getPackageManager().hasSystemFeature(PackageManager
                .FEATURE_CAMERA);
        DialogInterface.OnClickListener takePhoto = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (hasCamera) {
                    lastPhotoUri = preparePhotoUri(activity);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, lastPhotoUri);
                    activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE_TAKE_PICTURE);
                } else {
                    Toast.makeText(activity, "Device has no camera", Toast.LENGTH_SHORT).show();
                }
            }
        };
        DialogInterface.OnClickListener getPhotoFromAlbum = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                activity.startActivityForResult(photoPickerIntent,
                        ACTIVITY_REQUEST_CODE_GET_PICTURE_FROM_STORAGE);
            }
        };
        showTwoVariantsAlert(activity, activity.getString(R.string.label_take_photo),
                activity.getString(R.string.label_choose_existing), takePhoto, getPhotoFromAlbum,
                null);
    }

    public Uri preparePhotoUri(Activity activity) {
        File photoDir = Environment.getExternalStorageDirectory();
        File photo = new File(photoDir, String.format(PHOTO_PATH,
                String.valueOf(Calendar.getInstance().getTimeInMillis())));
        Uri photoUri = Uri.fromFile(photo);

        return photoUri;
    }

    public Uri getTempUri(Activity activity) {
        File photoDir = Environment.getExternalStorageDirectory();
        File photo = new File(photoDir, String.format(PHOTO_PATH,
                "temp"));
        Uri photoUri = Uri.fromFile(photo);

        return photoUri;
    }

    public Bitmap savePhoto(Uri uri, boolean isFromStorage) {
        if (uri != null) {
            Bitmap imageBitmap = null;
            String path = isFromStorage ? getPath(uri) : uri.getPath();
            if (path == null) {
                return null;
            }
            imageBitmap = getScaledBitmapFromUrl(path, THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX,
                    isFromStorage);
            Bitmap rotatedBitmap = getRotatedCameraBitmap(path, imageBitmap);
            return rotatedBitmap;
        }
        return null;
    }

    public boolean savePhoto(Bitmap bitmap, Uri path) {
        try {
            FileOutputStream out = new FileOutputStream(path.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static Bitmap getScaledBitmapFromUrl(String imageUrl, int requiredWidth,
                                                 int requiredHeight,
                                                 boolean isFromStorage) {
        URL url;
        try {
            url = new URL("file://" + imageUrl);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
            options.inJustDecodeBounds = false;

            Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null,
                    options);
            return bm;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public Bitmap getRotatedCameraBitmap(String filename, Bitmap bitmap) {
        try {
            ExifInterface ei = new ExifInterface(filename);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, ROTATE_90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, ROTATE_180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, ROTATE_270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return rotateBitmap;
    }

    public Bitmap squareBitmap(Bitmap bitmap) {
        Bitmap squaredBitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width >= height) {
            squaredBitmap = Bitmap.createBitmap(
                    bitmap,
                    width / 2 - height / 2,
                    0,
                    height,
                    height
            );
        } else {
            squaredBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    height / 2 - width / 2,
                    width,
                    width
            );
        }
        return squaredBitmap;
    }

    //getting path of uri with complicated scheme
    @SuppressLint("NewApi")
    public String getPath(Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Context context = OPApplication.getInstance();

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse
                        ("content://downloads/public_downloads"), Long
                        .valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection,
                    selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void showTwoVariantsAlert(Context context, String button1Text, String button2Text,
                                      DialogInterface.OnClickListener button1Action,
                                      DialogInterface.OnClickListener button2Action,
                                      String message) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        if (message != null) {
            dialog.setMessage(message);
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, button1Text, button1Action);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, button2Text, button2Action);
        dialog.show();
    }

    public Uri getLastPhotoUri() {
        return lastPhotoUri;
    }
}
