package com.openpeer.sample.push.parsepush;

import com.openpeer.sample.push.PushExtra;
import com.openpeer.sdk.model.GsonFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class PFPushMessage {
    String alert;
    String to;
    String extras;

    public PFPushMessage() {
    }

    public PFPushMessage(String alert, String extras, String toPeerUri) {
        this.alert = alert;
        this.extras = extras;
        this.to = toPeerUri;
    }

    public String getAlert() {
        return alert;
    }

    public String getExtras() {
        return extras;
    }

    public String toJsonBlob() {
        return GsonFactory.getGson().toJson(this);
    }

    public static PFPushMessage fromJson(String jsonBlob) {
        return GsonFactory.getGson().fromJson(jsonBlob, PFPushMessage.class);
    }

    public JSONObject toJsonObject() {
        try {
            return new JSONObject(toJsonBlob());
        } catch(JSONException e) {
            return null;
        }
    }
}
