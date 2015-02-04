package com.openpeer.sample.push.parsepush;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.BackgroundingManager;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sample.conversation.ConversationActivity;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.CallSystemMessage;
import com.openpeer.sdk.model.ConversationManager;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.MessageEditState;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.ParticipantInfo;
import com.openpeer.sdk.utils.OPModelUtils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PFPushReceiver extends ParsePushBroadcastReceiver {
    static final String TAG = PFPushReceiver.class.getSimpleName();
    public static final String KEY_ALERT = "alert";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_BADGE = "badge";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        Bundle extras = intent.getExtras();

        if (extras == null || extras.size() == 0) {
            OPLogger.error(OPLogLevel.LogLevel_Basic, "PFPushReceiver received empty extras");
            return;
        }
        String data = extras.getString(KEY_PUSH_DATA);
        if (TextUtils.isEmpty(data)) {
            OPLogger.error(OPLogLevel.LogLevel_Basic, "PFPushReceiver received empty data");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            String conversationType = jsonObject.getString(PFPushMessage.KEY_CONVERSATION_TYPE);
            String conversationId = jsonObject.getString(PFPushMessage.KEY_CONVERSATION_ID);
            String messageId = jsonObject.getString(PFPushMessage.KEY_MESSAGE_ID);
            String senderUri = jsonObject.getString(PFPushMessage.KEY_PEER_URI);
            long date = jsonObject.getLong(PFPushMessage.KEY_DATE) * 1000l;
            // If message is already received, ignore notification
            if (null != OPDataManager.getInstance().getMessage(messageId)) {
                Log.e(TAG, "received push for message that is already received "
                    + messageId);
                return;
            }
            OPUser sender = OPDataManager.getInstance().getUserByPeerUri(senderUri);
            if (sender == null) {
                Log.e("test", "Couldn't find user for peer " + senderUri);
                return;
            }

            String peerURIsString = jsonObject.getString(PFPushMessage.KEY_PEER_URIS);

            ParticipantInfo participantInfo = getParticipantInfo(sender, peerURIsString);
            //Make sure conversation is saved in db.
            OPConversation conversation = ConversationManager.getInstance().getConversation
                (GroupChatMode.valueOf(conversationType), participantInfo, conversationId, true);
            String messageType = jsonObject.getString(PFPushMessage.KEY_MESSAGE_TYPE);

            switch (messageType){
            case OPMessage.TYPE_TEXT:{
                String alert = jsonObject.getString(PFPushMessage.KEY_ALERT);

                OPMessage message = new OPMessage(sender.getUserId(),
                                                  PFPushMessage.MESSAGE_TYPE_TEXT,
                                                  alert,
                                                  date,
                                                  messageId,
                                                  MessageEditState.Normal);
                OPDataManager.getInstance().saveMessage(message,
                                                        conversation.getConversationId(),
                                                        participantInfo);
//                return getTextMessageNotification(jsonObject);
                OPNotificationBuilder.showNotificationForMessage(
                    OPModelUtils.getUserIds(participantInfo.getParticipants()),
                    message,
                    conversationType,
                    conversation.getConversationId());
            }
            break;
            case OPMessage.TYPE_JSON_SYSTEM_MESSAGE:{
                JSONObject systemObject = jsonObject.getJSONObject("system");
                if (systemObject.has("callStatus")) {
                    JSONObject callStatusObject = systemObject.getJSONObject("callStatus");
                    String callStatus = callStatusObject.getString(CallSystemMessage
                                                                       .KEY_CALL_STATUS_STATUS);
                    if (OPDataManager.getInstance().isAccountReady()) {
                        OPUser user = OPDataManager.getInstance().getUserByPeerUri(jsonObject
                                                                                       .getString

                                                                                           (PFPushMessage.KEY_PEER_URI));
                        if (user != null) {
                            user.hintAboutLocation(jsonObject.getString(PFPushMessage
                                                                            .KEY_LOCATION));
                        }
                    }
                    //Only register notification if app is in background
                    if (BackgroundingManager.isInBackground()) {

                    }
                    String callId = callStatusObject.getString("id");
                    OPNotificationBuilder.showNotification(
                        OPNotificationBuilder.getNotificationIdForCall(callId),
                        OPNotificationBuilder.buildPushNotificationForCall(
                            callId,
                            senderUri,
                            jsonObject.getString(PFPushMessage.KEY_SENDER_NAME),
                            callStatusObject.getString(CallSystemMessage.KEY_CALL_STATUS_STATUS),
                            conversationType,
                            conversation.getConversationId(),
                            OPModelUtils.getUserIds(participantInfo.getParticipants())));
                }
            }
            case PFPushMessage.MESSAGE_TYPE_CONTACTS_REMOVED:{
            }
            break;
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        downloadMessages();
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        return null;
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return ConversationActivity.class;
    }

    public static void downloadMessages() {
        ParseQuery parseQuery = new ParseQuery("OPPushMessage");
        parseQuery.whereEqualTo("to", OPDataManager.getInstance().getCurrentUser().getPeerUri());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (e != null) {
                    OPLogger.error(OPLogLevel.LogLevel_Debug,
                                   "downloadMessages failed " + e.getMessage());
                    return;
                } else if (list == null || list.size() == 0) {
                    OPLogger.debug(OPLogLevel.LogLevel_Debug, "downloadMessages empty ");
                    return;
                }
                for (ParseObject object : list) {
                    //save messages
                    String senderUri = object.getString(PFPushMessage.KEY_PEER_URI);
                    OPUser sender = OPDataManager.getInstance().getUserByPeerUri(senderUri);
                    if (sender == null) {
                        Log.e("test", "Couldn't find user for peer " + senderUri);
                        continue;
                    }
                    String peerURIsString = object.getString(PFPushMessage.KEY_PEER_URIS);
                    ParticipantInfo participantInfo = getParticipantInfo(sender, peerURIsString);
                    OPMessage message = new OPMessage(sender.getUserId(),
                                                      object.getString(PFPushMessage
                                                                           .KEY_MESSAGE_TYPE),
                                                      object.getString(KEY_ALERT),
                                                      object.getLong(PFPushMessage.KEY_DATE)*1000,
                                                      object.getString(PFPushMessage
                                                                           .KEY_MESSAGE_ID));
                    OPConversation conversation = ConversationManager.getInstance().getConversation(
                        GroupChatMode.valueOf(object.getString(PFPushMessage
                                                                   .KEY_CONVERSATION_TYPE)),
                        participantInfo,
                        object.getString(PFPushMessage.KEY_CONVERSATION_ID),
                        true
                    );
                    OPDataManager.getInstance().saveMessage(message,
                                                            conversation.getConversationId(),
                                                            participantInfo);
                }
                ParseObject.deleteAllInBackground(list, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("ParsePush", "downloadMessages deleted " + list.size() + " " +
                                "messages");
                        } else {
                            e.printStackTrace();
                        }
                        ParseInstallation.getCurrentInstallation().put(KEY_BADGE, 0);
                        ParseInstallation.getCurrentInstallation().saveEventually();
                    }
                });
            }
        });
    }

    static ParticipantInfo getParticipantInfo(OPUser sender, String peerURIsString) {
        List<OPUser> users = new ArrayList<>();
        users.add(sender);
        if (!TextUtils.isEmpty(peerURIsString)) {
            String peerURIs[] = TextUtils.split(peerURIsString, ",");
            for (String uri : peerURIs) {
                OPUser user = OPDataManager.getInstance().getUserByPeerUri(uri);
                if (user == null) {
                    //TODO: error handling
                    Log.e(TAG, "peerUri user not found " + uri);
                    continue;
                } else {
                    users.add(user);
                }
            }
        }
        ParticipantInfo participantInfo = new ParticipantInfo(OPModelUtils
                                                                  .getWindowId(users),
                                                              users);
        return participantInfo;
    }

}
