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
package com.openpeer.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.conversation.CallActivity;
import com.openpeer.sample.conversation.ConversationActivity;
import com.openpeer.sample.util.CallUtil;
import com.openpeer.sample.util.SettingsHelper;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.OPSession;
import com.openpeer.sdk.model.OPUser;

public class OPNotificationBuilder {
	private static String TAG = OPNotificationBuilder.class.getSimpleName();

	private static final int NOTIFICATION_ID_BASE_CALL = 100000;
	private static final int NOTIFICATION_ID_BASE_MESSAGE = 200000;

	public static void showNotificationForCall(OPCall call) {
		Intent launchIntent = null;
		Context context = OPApplication.getInstance();
		// TODO build proper strings

		String message = CallUtil.getCallStateStringResId(call.getState());

		Notification.Builder builder = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(call.getPeerUser().getName())
				.setContentText(message)
				.setSmallIcon(R.drawable.ic_action_call_light);
		// Create the notification
		launchIntent = new Intent(context, CallActivity.class);
		String peerUri = call.getPeer().getPeerURI();
		launchIntent.putExtra(IntentData.ARG_CONVERSATION_ACTION, IntentData.ACTION_CALL);
		launchIntent.putExtra(IntentData.ARG_PEER_URI, peerUri);
		// Set the intent to perform when tapped

		launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(contentIntent);

		int notificationId = (int) (call.getStableID() + NOTIFICATION_ID_BASE_CALL);
		Notification notification = builder.build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId, notification);
	}

	public static void showNotificationForMessage(OPSession session, OPMessage message) {
		Context context = OPApplication.getInstance();
		Notification notification = buildNotificationForMessage(session.getParticipantIDs(), message);
		int notificationId = (int) session.getCurrentWindowId() + NOTIFICATION_ID_BASE_MESSAGE;
		// Show the notification
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId, notification);
	}

	public static Notification buildNotificationForMessage(long participantIds[], OPMessage message) {
		Context context = OPApplication.getInstance();
		Intent launchIntent = null;
		// TODO build proper strings
		String title = OPApplication.getInstance().getString(R.string.label_new_message_received, message.getFromUser().getName());

		Notification.Builder builder = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(message.getMessage())
				.setSmallIcon(R.drawable.ic_launcher);
		if (SettingsHelper.isSoundNotificationOnForNewMessage()) {
			builder.setSound(SettingsHelper.getNotificationSound());
		}
		// Create the notification
		launchIntent = new Intent(context, ConversationActivity.class);
		launchIntent.putExtra(IntentData.ARG_CONVERSATION_ACTION, IntentData.ACTION_CHAT);
		launchIntent.putExtra(IntentData.ARG_PEER_USER_IDS, participantIds);
		// Set the intent to perform when tapped
		launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// notification.setLatestEventInfo(context, appName, message, contentIntent);

		builder.setContentIntent(contentIntent);

		// int notificationId = (int) (session.getCurrentWindowId() + NOTIFICATION_ID_BASE);

		return builder.build();
	}

	public static void cancelNotificationForChat(int windowId) {
		NotificationManager notificationManager = (NotificationManager) OPApplication.getInstance().getSystemService(
				Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID_BASE_MESSAGE + windowId);
	}

	public static void cancelNotificationForCall(OPCall call) {
		NotificationManager notificationManager = (NotificationManager) OPApplication.getInstance().getSystemService(
				Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID_BASE_CALL + (int) call.getStableID());
	}
    public static void cancelAllUponSignout(){
        NotificationManager notificationManager = (NotificationManager) OPApplication.getInstance().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
