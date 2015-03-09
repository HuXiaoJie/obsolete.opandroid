/*
 * ******************************************************************************
 *  *
 *  *  Copyright (c) 2014 , Hookflash Inc.
 *  *  All rights reserved.
 *  *
 *  *  Redistribution and use in source and binary forms, with or without
 *  *  modification, are permitted provided that the following conditions are met:
 *  *
 *  *  1. Redistributions of source code must retain the above copyright notice, this
 *  *  list of conditions and the following disclaimer.
 *  *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  *  this list of conditions and the following disclaimer in the documentation
 *  *  and/or other materials provided with the distribution.
 *  *
 *  *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  *
 *  *  The views and conclusions contained in the software and documentation are those
 *  *  of the authors and should not be interpreted as representing official policies,
 *  *  either expressed or implied, of the FreeBSD Project.
 *  ******************************************************************************
 */

package com.openpeer.sample.push.parsepush;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPApplication;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPAccount;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sample.push.PushServiceInterface;
import com.openpeer.sdk.model.HOPSystemMessage;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PFPushService implements PushServiceInterface {
    public static final String TAG = PFPushService.class.getSimpleName();
    public static final String KEY_PARSE_APP_ID = "parseAppId";
    public static final String KEY_PARSE_CLIENT_KEY = "parseClientKey";
    static final String KEY_PEER_URI = "peerUri";
    static final String KEY_OS_VERSION = "osVersion";
    private static PFPushService instance;
    static long PUSH_EXPIRATION_DEFAULT = 30 * 24 * 60 * 60;

    public boolean isInitialized() {
        return mInitialized;
    }

    private boolean mInitialized;

    public static PFPushService getInstance() throws RuntimeException {
        if (instance == null) {

            instance = new PFPushService();
            String parseAppId = OPApplication.getMetaInfo(KEY_PARSE_APP_ID);
            String parseClientKey = OPApplication.getMetaInfo(KEY_PARSE_CLIENT_KEY);
            if (TextUtils.isEmpty(parseAppId)) {
                throw new RuntimeException("Parse application id is not defined");
            } else if (TextUtils.isEmpty(parseClientKey)) {
                throw new RuntimeException("Parse client key is not defined");
            }
            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
            ParseCrashReporting.enable(OPApplication.getInstance());
            Parse.initialize(OPApplication.getInstance(),
                             parseAppId,
                             parseClientKey);

        }
        return instance;
    }

    public boolean init() {
        final HOPContact currentUser = HOPAccount.selfContact();
        if (currentUser != null) {
            Log.d("PFPushService", "init calling parse init for " + currentUser.getPeerUri());
            ParseInstallation.getCurrentInstallation().put(KEY_PEER_URI, currentUser.getPeerUri());
            ParseInstallation.getCurrentInstallation().put(KEY_OS_VERSION,
                                                           "" + Build.VERSION.SDK_INT);
            ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("PFPushService", "init done " + currentUser.getPeerUri());
                        mInitialized = true;
                        PFPushReceiver.downloadMessages();
                    } else {
                        e.printStackTrace();
                    }
                }
            });

            return true;
        }
        return false;
    }

    private PFPushService() {
    }

    @Override
    public void onConversationThreadPushMessage(final HOPConversation conversation,
                                                final OPMessage message,
                                                final HOPContact HOPContact) {
        if (!mInitialized) {
            init();
        }
        List<HOPContact> participants = conversation.getParticipants();
        String peerURIs = "";

        String peerUris[] = new String[participants.size() - 1];
        //We only put the peerURIs other than myself and the recipient
        if (participants.size() > 1) {
            int i = 0;
            for (HOPContact user : participants) {
                if (!user.equals(HOPContact)) {
                    peerUris[i] = user.getPeerUri();
                    i++;
                }
            }
            peerURIs = TextUtils.join(",", peerUris);
        }

//        PushExtra pushExtra = new PushExtra
//            (OPDataManager.getInstance().getCurrentUser().getPeerUri(),
//             OPDataManager.getInstance().getCurrentUser().getName(),
//             peerURIs,
//             message.getMessageType(),
//             message.getMessageId(),
//             message.getMessageId(),
//             conversation.getType().toString(),
//             conversation.getConversationId(),
//             OPDataManager.getInstance().getSharedAccount().getLocationID(),
//             message.getTime().toMillis(false) / 1000 + "");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(PFPushMessage.KEY_TO, HOPContact.getPeerUri());
        params.put(PFPushMessage.KEY_PEER_URI, HOPAccount.selfContact().getPeerUri());
        params.put(PFPushMessage.KEY_SENDER_NAME, HOPAccount.selfContact().getName());
        params.put(PFPushMessage.KEY_PEER_URIS, peerURIs);
        params.put(PFPushMessage.KEY_MESSAGE_ID, message.getMessageId());
        params.put(PFPushMessage.KEY_CONVERSATION_TYPE, conversation.getType().name());
        params.put(PFPushMessage.KEY_CONVERSATION_ID, conversation.getConversationId());
        params.put(PFPushMessage.KEY_LOCATION, HOPAccount.currentAccount().getLocationID());
        params.put(PFPushMessage.KEY_DATE, message.getTime().toMillis(false) / 1000);
        String messageType = message.getMessageType();
        params.put(PFPushMessage.KEY_MESSAGE_TYPE, messageType);

        switch (messageType){
        case OPMessage.TYPE_TEXT:{
            params.put(PFPushMessage.KEY_ALERT, message.getMessage());
        }
        break;
        case OPMessage.TYPE_JSON_SYSTEM_MESSAGE:{
            try {
                JSONObject jsonObject = new JSONObject(message.getMessage().replace("\"$id\"",
                                                                                    "\"id\""));
                JSONObject systemObject = jsonObject.getJSONObject(HOPSystemMessage.KEY_ROOT);
                params.put("system", systemObject);
//                if (systemObject.has(SystemMessage.KEY_CALL_STATUS)) {
//                    systemObject=systemObject.getJSONObject(SystemMessage.KEY_CALL_STATUS);
//                    params.put(PFPushMessage.KEY_MESSAGE_TYPE,
//                               PFPushMessage.MESSAGE_TYPE_CALL_STATE);
//                    params.put(PFPushMessage.KEY_CALL_STATE,
//                               systemObject.getString(SystemMessage.KEY_CALL_STATUS_STATUS));
//                    params.put(PFPushMessage.KEY_CALL_MEDIA_TYPE,
//                               systemObject.getString(SystemMessage.KEY_CALL_STATUS_MEDIA_TYPE));
//                    params.put(PFPushMessage.KEY_CALL_ID,
//                               systemObject.getString(SystemMessage.KEY_CALL_STATUS_CALL_ID));
//                } else if (systemObject.has(SystemMessage.KEY_CONTACTS_REMOVED)) {
//                    params.put(PFPushMessage.KEY_MESSAGE_TYPE,
//                               PFPushMessage.MESSAGE_TYPE_CONTACTS_REMOVED);
//                    // TODO: add others
//                }

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        break;
        }

        ParseCloud.callFunctionInBackground(
            "sendPushToUser",
            params,
            new FunctionCallback<Object>() {
                public void done(Object success, ParseException e) {
                    if (e == null) {
                        Log.d("ParsePush", "success " + success);
                        // Push sent successfully
                        HOPDataManager.getInstance()
                            .updateMessageDeliveryStatus(
                                message.getMessageId(),
                                conversation.getId(),
                                MessageDeliveryStates.MessageDeliveryState_Sent);

                    } else {
                        //TODO: proper error handling
                        e.printStackTrace();
                    }
                }
            });
    }

    public void onSignout() {
        ParseInstallation.getCurrentInstallation().put(KEY_PEER_URI, "");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
