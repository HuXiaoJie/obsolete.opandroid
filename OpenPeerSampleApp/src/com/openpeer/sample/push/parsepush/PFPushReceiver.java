package com.openpeer.sample.push.parsepush;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sample.conversation.ConversationActivity;
import com.openpeer.sample.push.PushExtra;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.ConversationManager;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.MessageEditState;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.ParticipantInfo;
import com.openpeer.sdk.utils.OPModelUtils;
import com.parse.ParsePushBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class PFPushReceiver extends ParsePushBroadcastReceiver {
    static final String TAG = PFPushReceiver.class.getSimpleName();

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
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

        Bundle extras = intent.getExtras();

        if (extras == null) {
            return null;
        }
        String data = extras.getString(KEY_PUSH_DATA);
        Log.d(TAG, "getNotification " + data);
        PFPushMessage pushMessage = PFPushMessage.fromJson(data);
        PushExtra pushExtra = PushExtra.fromString(pushMessage.getExtras());
        String alert = pushMessage.getAlert();
        String senderUri = pushExtra.getPeerURI();
        String messageId = pushExtra.getMessageId();
        String messageType = pushExtra.getMessageType();
        messageType = TextUtils.isEmpty(messageType) ? OPMessage.OPMessageType.TYPE_TEXT :
            messageType;
        String conversationType = pushExtra.getConversationType();
        String conversationId = pushExtra.getConversationId();
        // If message is already received, ignore notification
        if (null != OPDataManager.getInstance().getMessage(messageId)) {
            Log.e(TAG, "received push for message that is already received "
                + messageId);
            return null;
        }
        OPUser sender = OPDataManager.getInstance().getUserByPeerUri(senderUri);
        if (sender == null) {
            Log.e("test", "Couldn't find user for peer " + senderUri);
            return null;
        }
        OPMessage message = new OPMessage(sender.getUserId(),
                                          messageType,
                                          alert,
                                          Long.parseLong(pushExtra.getDate()) * 1000l,
                                          messageId,
                                          MessageEditState.Normal);
        String peerURIsString = pushExtra.getPeerURIs();
        List<OPUser> users = new ArrayList<>();
        users.add(sender);
        if (!TextUtils.isEmpty(peerURIsString)) {
            String peerURIs[] = TextUtils.split(peerURIsString, ",");
            for (String uri : peerURIs) {
                OPUser user = OPDataManager.getInstance().getUserByPeerUri(uri);
                if (user == null) {
                    //TODO: error handling
                    Log.e(TAG, "peerUri user not found " + uri);
                    return null;
                } else {
                    users.add(user);
                }
            }
        }
        ParticipantInfo participantInfo = new ParticipantInfo(OPModelUtils.getWindowId(users),
                                                              users);
        //Make sure conversation is saved in db.
        OPConversation conversation = ConversationManager.getInstance().getConversation
            (GroupChatMode.valueOf(conversationType), participantInfo, conversationId, true);
        OPDataManager.getInstance().saveMessage(message, conversation.getConversationId(), participantInfo);
        return OPNotificationBuilder.buildNotificationForMessage(
            OPModelUtils.getUserIds(participantInfo.getParticipants()),
            message,
            conversationType,
            conversation.getConversationId());
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return ConversationActivity.class;
    }
}
