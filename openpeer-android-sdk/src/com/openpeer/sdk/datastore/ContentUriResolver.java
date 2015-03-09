package com.openpeer.sdk.datastore;

import android.net.Uri;

public interface ContentUriResolver {
    public Uri getContentUri(String path);
    public void onSignout();
}
