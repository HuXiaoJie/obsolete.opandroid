package com.openpeer.sample.delegates;

import android.graphics.Bitmap;
import android.text.format.Time;
import android.util.Log;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.ContactConnectionStates;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.OPApplication;
import com.openpeer.sample.PhotoHelper;
import com.openpeer.sample.conversation.ConversationSwitchSystemMessage;
import com.openpeer.sample.conversation.FileShareSystemMessage;
import com.openpeer.sample.events.ConversationComposingStatusChangeEvent;
import com.openpeer.sample.events.ConversationContactsChangeEvent;
import com.openpeer.sample.events.ConversationSwitchEvent;
import com.openpeer.sample.events.ConversationTopicChangeEvent;
import com.openpeer.sample.util.FileUtil;
import com.openpeer.sdk.model.CallEvent;
import com.openpeer.sdk.model.CallSystemMessage;
import com.openpeer.sdk.model.HOPAccount;
import com.openpeer.sdk.model.HOPCall;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPConversationDelegate;
import com.openpeer.sdk.model.HOPConversationManager;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPSystemMessage;
import com.openpeer.sdk.utils.JSONUtils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HOPConversationDelegateImpl implements HOPConversationDelegate {
    public static final String TAG = "ConversationDelegate";
    private static HOPConversationDelegateImpl instance;

    public static HOPConversationDelegateImpl getInstance() {
        if (instance == null) {
            instance = new HOPConversationDelegateImpl();
        }
        return instance;
    }

    private HOPConversationDelegateImpl() {
    }

    @Override
    public void onConversationContactStatusChanged(HOPConversation conversation,
                                                   ComposingStates composingStates,
                                                   HOPContact HOPContact) {
        new ConversationComposingStatusChangeEvent(conversation,
                HOPContact,
                composingStates).post();
    }

    @Override
    public boolean onConversationMessage(HOPConversation conversation, OPMessage message) {
        if (message.getMessageType().equals(OPMessage.TYPE_JSON_SYSTEM_MESSAGE)) {
            Log.d(TAG, "onConversationMessage processing system message" + message.getMessage());

            OPContact opContact = message.getFrom();
            HOPContact sender = HOPDataManager.getInstance().
                    getUserByPeerUri(opContact.getPeerURI());
            String messageText = message.getMessage();
            try {
                JSONObject systemMessage = new JSONObject(messageText).getJSONObject
                    (HOPSystemMessage.KEY_ROOT);
                handleSystemMessage(conversation, sender, systemMessage,
                                    message.getTime().toMillis(false));
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean onConversationPushMessage(HOPConversation conversation, OPMessage message,
                                             HOPContact HOPContact) {
        OPApplication.getPushService().onConversationThreadPushMessage(conversation, message,
                HOPContact);
        return true;
    }


    @Override
    public boolean onConversationContactsChanged(HOPConversation conversation) {
        new ConversationContactsChangeEvent(conversation).post();
        return true;
    }


    @Override
    public void onConversationMessageDeliveryStateChanged(HOPConversation conversation, OPMessage
            message) {

    }

    @Override
    public void onConversationContactConnectionStateChanged(HOPConversation conversation,
                                                            HOPContact HOPContact,
                                                            ContactConnectionStates state) {

    }

    public boolean onConversationTopicChanged(HOPConversation conversation, String newTopic) {
        new ConversationTopicChangeEvent(conversation, newTopic).post();
        return true;
    }

    public static void handleSystemMessage(final HOPConversation conversation, HOPContact sender,
                                           final JSONObject systemMessage,
                                           final long time) {
        try {
////            String messageText = message.getMessage();
//            JSONObject systemMessage = new JSONObject(messageText).getJSONObject
//                    (HOPSystemMessage.KEY_ROOT);


            if (systemMessage.has(HOPSystemMessage.KEY_CALL_STATUS)) {
                JSONObject callSystemMessage = systemMessage
                        .getJSONObject(HOPSystemMessage.KEY_CALL_STATUS);
                handleCallSystemMessage(callSystemMessage,
                        sender,
                        conversation.getId(),
                        time);

            } else if (systemMessage.has(HOPSystemMessage.KEY_CONTACTS_REMOVED)) {
                JSONArray contactsRemovedMessage = systemMessage
                        .getJSONArray(HOPSystemMessage.KEY_CONTACTS_REMOVED);
                String selfPeerUri = HOPAccount.selfContact().getPeerUri();
                for (Object peerUri : (Object[]) JSONUtils.toArray(contactsRemovedMessage)) {
                    if (peerUri.equals(selfPeerUri)) {
                        conversation.setDisabled(true);
                        new ConversationContactsChangeEvent(conversation).post();
                    }
                }
            } else if (systemMessage.has(ConversationSwitchSystemMessage.KEY_CONVERSATION_SWITCH)) {
                JSONObject object = systemMessage.
                        getJSONObject(ConversationSwitchSystemMessage.KEY_CONVERSATION_SWITCH);
                HOPConversation from = HOPConversationManager.getInstance().getConversationById(
                        object.getString(ConversationSwitchSystemMessage.KEY_FROM_CONVERSATION_ID));

                if (from != null && conversation != null) {
                    new ConversationSwitchEvent(from, conversation).post();
                }
            } else if (systemMessage.has(FileShareSystemMessage.KEY_FILE_SHARE)) {
                handleFileShareSystemMessage(conversation, sender, systemMessage, time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void handleCallSystemMessage(JSONObject message, HOPContact user,
                                               long conversationId,
                                               long timestamp) {
        try {
            String callId = message.getString(CallSystemMessage.KEY_ID);
            HOPCall call = HOPCall.findCallById(callId);
            String calleeUrl = message.getString(CallSystemMessage.KEY_CALL_STATUS_CALLEE);
            if (calleeUrl.equals(HOPAccount.selfContact().getPeerUri())) {
                HOPDataManager.getInstance().saveCall(message.getString(CallSystemMessage.KEY_ID),
                        conversationId,
                        user.getUserId(),
                        HOPCall.DIRECTION_INCOMING,
                        message.getString(CallSystemMessage.KEY_CALL_STATUS_MEDIA_TYPE));
                CallEvent event = new CallEvent(callId,
                        message.getString(CallSystemMessage.KEY_CALL_STATUS_STATUS),
                        timestamp);
                HOPDataManager.getInstance().saveCallEvent(callId, conversationId, event);
            } else {
                if (call == null) {// i'm tototally not connected with the peer
                    //couldn't find call in memory. try to save call
                    HOPDataManager.getInstance().saveCall(
                            message.getString(CallSystemMessage.KEY_ID),
                            conversationId,
                            user.getUserId(),
                            HOPCall.DIRECTION_INCOMING,
                            message.getString(CallSystemMessage.KEY_CALL_STATUS_MEDIA_TYPE));
                    CallEvent event = new CallEvent(callId,
                            message.getString(CallSystemMessage.KEY_CALL_STATUS_STATUS),
                            timestamp);
                    HOPDataManager.getInstance().saveCallEvent(callId, conversationId, event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void handleFileShareSystemMessage(final HOPConversation conversation, final HOPContact sender,
                                           final JSONObject systemMessage,
                                           final long time) throws JSONException{
        final JSONObject object = systemMessage.getJSONObject(FileShareSystemMessage
                                                                  .KEY_FILE_SHARE);
        final String objectId = object.getString(FileShareSystemMessage.KEY_OBJECT_id);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SharedPhoto");
        query.whereEqualTo("fileID",objectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject parseObject = objects.get(0);
                    final ParseFile imageFile = (ParseFile) parseObject.get("imageFile");
                    final String imageName = parseObject.getString("imageName");
                    imageFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Image downloaded " + imageName);
                                Bitmap thumbNail = PhotoHelper.createThumbnail(bytes);
                                final String thumbNailPath = PhotoHelper.getThumnailPath
                                    (objectId);
                                try {
                                    FileOutputStream stream = new FileOutputStream
                                        (thumbNailPath);

                                    // Compress image to lower quality scale 1 - 100
                                    thumbNail.compress(Bitmap.CompressFormat.PNG, 100,
                                                       stream);
                                    String imagePath = PhotoHelper.getImageCachePath
                                        (objectId);
                                    FileUtil.saveFile(imagePath, bytes);
                                    OPMessage message = conversation.createMessage(OPMessage.TYPE_INERNAL_FILE_PHOTO,FileShareSystemMessage.createStoreMessageText(
                                        objectId,
                                        "file://" + imagePath,
                                        "file://" + thumbNailPath,
                                        "downloaded"));
                                    message.setSenderId(sender.getUserId());
                                    Time time1 = new Time();
                                    time1.set(time);
                                    message.setTime(time1);
                                    HOPDataManager.getInstance().saveMessage(message,
                                                                             conversation.getId(),
                                                                             conversation.getParticipantInfo());
                                } catch (FileNotFoundException e1) {
                                    e.printStackTrace();
                                }
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer integer) {

                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }

        });
    }
}
