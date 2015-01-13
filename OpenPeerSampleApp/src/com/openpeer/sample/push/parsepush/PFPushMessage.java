package com.openpeer.sample.push.parsepush;

import com.openpeer.sample.push.PushExtra;
import com.openpeer.sdk.model.GsonFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class PFPushMessage {
    String alert;
    PushExtra extras;

    public PFPushMessage() {
    }

    public PFPushMessage(String alert, PushExtra extras) {
        this.alert = alert;
        this.extras = extras;
    }

    public String getAlert() {
        return alert;
    }

    public PushExtra getExtras() {
        return extras;
    }

    public String toJsonBlob() {
        return GsonFactory.getGson().toJson(this);
    }

    public static PFPushMessage fromJson(String jsonBlob) {
        return GsonFactory.getGson().fromJson(jsonBlob, PFPushMessage.class);
    }
    public JSONObject toJsonObject(){
        try {
            return new JSONObject(toJsonBlob());
        } catch(JSONException e) {
            return null;
        }
    }
}
