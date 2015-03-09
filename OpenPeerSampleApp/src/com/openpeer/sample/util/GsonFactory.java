package com.openpeer.sample.util;

import com.google.gson.Gson;

/**
 * Created by brucexia on 2014-12-22.
 */
public class GsonFactory {
    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
