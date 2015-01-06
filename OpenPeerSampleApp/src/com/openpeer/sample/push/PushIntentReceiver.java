/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.sample.push;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.MessageEditState;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.ParticipantInfo;
import com.openpeer.sdk.utils.OPModelUtils;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.actions.OpenExternalUrlAction;
import com.urbanairship.push.GCMMessageHandler;
import com.urbanairship.push.PushManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PushIntentReceiver extends BroadcastReceiver {
    private static final String logTag = "PushIntentReceiver";
    public static final String EXTRA_MESSAGE_ID_KEY = "_uamid";

    // A set of actions that launch activities when a push is opened. Update
    // with any custom actions that also start activities when a push is opened.
    private static String[] ACTIVITY_ACTIONS = new String[] {
            DeepLinkAction.DEFAULT_REGISTRY_NAME,
            OpenExternalUrlAction.DEFAULT_REGISTRY_NAME,
            LandingPageAction.DEFAULT_REGISTRY_NAME
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(logTag, "Received intent: " + intent.toString());

        String action = intent.getAction();

        if (action == null) {
            return;
        }

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {

            int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

            Log.i(logTag, "Received push notification. Alert: "
                    + intent.getStringExtra(PushManager.EXTRA_ALERT)
                    + " [NotificationID=" + id + "]");

            logPushExtras(intent);

            //TODO: Now notify observer

        } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

        } else if (action.equals(GCMMessageHandler.ACTION_GCM_DELETED_MESSAGES)) {
            Log.i(logTag,
                    "The GCM service deleted "
                            + intent.getStringExtra(GCMMessageHandler.EXTRA_GCM_TOTAL_DELETED)
                            + " messages."
                    );
        } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            String apid = intent.getStringExtra(PushManager.EXTRA_APID);
            Log.i(logTag, "Push registration finished " + apid);
            if (apid != null
                    && OPDataManager.getInstance().getSharedAccount() != null
                    && OPDataManager.getInstance().getSharedAccount()
                            .getState() == AccountStates.AccountState_Ready) {
                OPPushManager.getInstance().associateDeviceToken(
                        OPDataManager.getInstance().getSharedAccount()
                                .getPeerUri(),
                        apid,
                        new Callback<HackApiService.HackAssociateResult>() {
                            @Override
                            public void success(
                                    HackApiService.HackAssociateResult hackAssociateResult,
                                    Response response) {

                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        }
                        );
            }
        }

        // Notify any app-specific listeners using the local broadcast receiver to avoid
        // leaking any sensitive information. This sends out all push and location intents
        // to the rest of the application.
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Log the values sent in the payload's "extra" dictionary.
     *
     * @param intent
     *            A PushManager.ACTION_NOTIFICATION_OPENED or ACTION_PUSH_RECEIVED intent.
     */
    private void logPushExtras(Intent intent) {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {

            // ignore standard C2DM extra keys
            List<String> ignoredKeys = (List<String>) Arrays.asList(
                    "collapse_key",// c2dm collapse key
                    "from",// c2dm sender
                    PushManager.EXTRA_NOTIFICATION_ID,// int id of generated notification (ACTION_PUSH_RECEIVED only)
                    PushManager.EXTRA_PUSH_ID,// internal UA push id
                    PushManager.EXTRA_ALERT);// ignore alert
            if (ignoredKeys.contains(key)) {
                continue;
            }
            Log.i(logTag,
                    "Push Notification Extra: [" + key + " : "
                            + intent.getStringExtra(key) + "]"
                    );
        }
    }

}
