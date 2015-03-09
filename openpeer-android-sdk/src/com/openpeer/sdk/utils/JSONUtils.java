package com.openpeer.sdk.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;

public class JSONUtils {
    public static JSONArray fromArray(Object array) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                jsonArray.put(Array.get(array, i));
            }
            return jsonArray;
        } else {
            throw new JSONException(
                "JSONArray initial value should be a string or collection or array.");
        }
    }

    public static Object toArray(JSONArray jsonArray) throws JSONException {
        int length = jsonArray.length();
        Object[] array = new Object[length];
        for (int i = 0; i < length; i++) {
            array[i] = jsonArray.get(i);
        }
        return array;
    }

}
