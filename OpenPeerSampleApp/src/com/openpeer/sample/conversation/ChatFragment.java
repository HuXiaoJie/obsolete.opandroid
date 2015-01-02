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
package com.openpeer.sample.conversation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.javaapi.OPMessage.OPMessageType;
import com.openpeer.sample.BaseActivity;
import com.openpeer.sample.BaseFragment;
import com.openpeer.sample.IntentData;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sample.R;
import com.openpeer.sample.contacts.ProfilePickerActivity;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.app.OPSdkConfig;
import com.openpeer.sdk.datastore.DatabaseContracts.MessageEntry;
import com.openpeer.sdk.datastore.OPContentProvider;
import com.openpeer.sdk.datastore.OPModelCursorHelper;
import com.openpeer.sdk.model.ConversationManager;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPConversationEvent;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.ParticipantInfo;
import com.openpeer.sdk.model.SessionListener;
import com.openpeer.sdk.utils.NoDuplicateArrayList;
import com.openpeer.sdk.utils.OPModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatFragment extends BaseFragment implements
    LoaderManager.LoaderCallbacks<Cursor>, SessionListener {

    private static final int DEFAULT_NUM_MESSAGES_TO_LOAD = 30;

    private static final String TAG = ChatFragment.class.getSimpleName();
    private ListView mMessagesList;
    private TextView mComposeBox;
    private View mSendButton;
    private MessagesAdaptor mAdapter;
    Loader mLoader;

    private OPConversation mSession;
    private CallInfoView mCallInfoView;
    boolean mTyping;
    private OPMessage mEditingMessage;

    String mConversationId;
    String mType;
    ParticipantInfo mParticipantInfo;
    public static ChatFragment newTestInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        long[] userIds;
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            userIds = args.getLongArray(IntentData.ARG_PEER_USER_IDS);
            mConversationId = args.getString(IntentData.ARG_CONVERSATION_ID);
            mType = args.getString(IntentData.ARG_CONVERSATION_TYPE);
        } else {
            userIds = savedInstanceState
                .getLongArray(IntentData.ARG_PEER_USER_IDS);
            mConversationId = savedInstanceState.getString(IntentData.ARG_CONVERSATION_ID);
            mType = savedInstanceState.getString(IntentData.ARG_CONVERSATION_TYPE);
        }
        long cbcId = OPModelUtils.getWindowId(userIds);
        OPNotificationBuilder.cancelNotificationForChat((int) cbcId);
        List<OPUser> participants = OPDataManager.getDatastoreDelegate().getUsers(userIds);
        mParticipantInfo = new ParticipantInfo(cbcId,participants);
        setHasOptionsMenu(true);
        if(OPDataManager.getInstance().isAccountReady()){
            setup();
        }
    }

    void setup(){
        mSession = ConversationManager.getInstance().getConversation(GroupChatMode.valueOf(mType),
                                                                     mParticipantInfo,
                                                                     mConversationId, true);

        mConversationId = mSession.getConversationId();
        mSession.registerListener(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSession!=null) {
            mSession.unregisterListener(this);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_chat, null);

        return setupView(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OPDataManager.getInstance().isAccountReady()) {
            return;
        }
        // All following stuff can only be done if the account is in ready state

        // TODO: proper look up
        // OPCall call = mSession.getCurrentCall();
        OPCall call = mSession.getCurrentCall();
        if (call != null && (call.getState() == CallStates.CallState_Open
            || call.getState() == CallStates.CallState_Active)) {
            Log.d(TAG, "now show call info");
            mCallInfoView.setVisibility(View.VISIBLE);
            mCallInfoView.bindCall(call);

        } else {
            mCallInfoView.setVisibility(View.GONE);
        }

        mSession.setComposingStatus(ComposingStates.ComposingState_Active);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCallInfoView.isShown()) {
            mCallInfoView.unbind();
        }
        if (!OPDataManager.getInstance().isAccountReady()) {
            return;
        }
        mSession.setComposingStatus(ComposingStates.ComposingState_Inactive);
    }

    void updateUsersView() {
        List<OPUser> users=mParticipantInfo.getParticipants();
        if (users.size() == 1) {
            getActivity().getActionBar().setTitle(users.get(0).getName());
        } else {
            String names[] = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                names[i] = users.get(i).getName();
            }
            getActivity().getActionBar().setTitle(TextUtils.join(",", names));
        }
    }

    View setupView(View view) {
        mCallInfoView = (CallInfoView) view.findViewById(R.id.call_info);
        View emptyView = view.findViewById(R.id.empty_view);
        mMessagesList = (ListView) view.findViewById(R.id.listview);
        mMessagesList.setEmptyView(emptyView);

        registerForContextMenu(mMessagesList);
        mMessagesList
            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2,
                                        long arg3) {
                    if (arg1 instanceof SelfMessageView
                        && ((SelfMessageView) arg1).canEditMessage()) {
                        mEditingMessage = ((SelfMessageView) arg1)
                            .getMessage();

                        mComposeBox.setText(mEditingMessage.getMessage());
                    }
                }
            });
        mAdapter = new MessagesAdaptor(getActivity(), null);
        mMessagesList.setAdapter(mAdapter);
        View layout = view.findViewById(R.id.layout_compose);
        mComposeBox = (TextView) layout.findViewById(R.id.text);
        mSendButton = layout.findViewById(R.id.send);

        updateUsersView();
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!OPDataManager.getInstance().isAccountReady()) {
                    BaseActivity.showInvalidStateWarning(getActivity());
                    return;
                }
                Log.d("TODO", "call actual send function");
                if (mComposeBox.getText() == null
                    || mComposeBox.getText().length() == 0) {
                    return;
                }
                OPMessage msg = null;
                // we use 0 for home user
                msg = new OPMessage(OPDataManager.getInstance()
                                        .getSharedAccount().getSelfContactId(),
                                    OPMessageType.TYPE_TEXT,
                                    mComposeBox.getText().toString(),
                                    System.currentTimeMillis(),
                                    OPMessage.generateUniqueId()
                );

                if (mEditingMessage != null) {
                    msg.setReplacesMessageId(mEditingMessage.getMessageId());
                    mEditingMessage = null;
                }
                mComposeBox.setText("");

                mSession.sendMessage(msg, false);
                if (mLoader == null) {
                    getLoaderManager().initLoader(URL_LOADER, null, ChatFragment.this);
                }
                mTyping = false;

                mSession.setComposingStatus(ComposingStates.ComposingState_Active);

            }
        });

        mComposeBox.addTextChangedListener(new TextWatcher() {
            Timer pauseTimer;// = new Timer();
            long PAUSE_DELAY = 30 * 1000;
            TimerTask task;

            @Override
            public void afterTextChanged(Editable arg0) {
                pauseTimer = new Timer();
                task = new TimerTask() {

                    @Override
                    public void run() {
                        mTyping = false;
                        mSession.setComposingStatus(ComposingStates.ComposingState_Paused);
                    }

                };
                pauseTimer.schedule(task, PAUSE_DELAY);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (pauseTimer != null) {

                    pauseTimer.cancel();
                    pauseTimer = null;
                    task = null;
                }
                if (!mTyping) {
                    mTyping = true;

                    mSession.setComposingStatus(ComposingStates.ComposingState_Composing);
                }
            }

        });

        getLoaderManager().initLoader(URL_LOADER, null, this);

        return view;
    }

    class MessagesAdaptor extends CursorAdapter {
        private final static int VIEWTYPE_SELF_MESSAGE_VIEW = 0;
        private final static int VIEWTYPE_RECIEVED_MESSAGE_VIEW = 1;
        private final static int VIEWTYPE_STATUS_VIEW = 2;
        private final static int VIEWTYPE_CALL_VIEW = 3;
        private final static int VIEWTYPE_CONVERSATION_EVENT_VIEW = 4;

        int myLastReadMessagePosition = -1;
        int myLastDeliveredMessagePosition = -1;
        int myLastSentMessagePosition = -1;
        private NoDuplicateArrayList<OPUser> composingStates = new NoDuplicateArrayList<OPUser>();

        private boolean isStatus(int position) {
            return position > super.getCount() - 1;
        }

        public MessagesAdaptor(Context context, Cursor c) {
            super(context, c);
        }

        public void notifyDataSetChanged(ComposingStates state, OPUser contact) {
            if (state == ComposingStates.ComposingState_Composing) {
                composingStates.add(contact);
            } else {
                composingStates.remove(contact);
            }
            notifyDataSetChanged();

        }

        @Override
        public void notifyDataSetChanged() {
            // TODO Auto-generated method stub
            super.notifyDataSetChanged();
            mMessagesList.setSelection(mMessagesList.getCount() - 1);

        }

        @Override
        public Object getItem(int position) {
            if (isStatus(position)) {
                return composingStates.get(position - super.getCount());
            } else {
                return super.getItem(position);
            }
        }

        @Override
        public long getItemId(int position) {
            if (isStatus(position)) {
                return -position;
            } else {
                return super.getItemId(position);
            }
        }

        @Override
        public int getCount() {
            return super.getCount() + composingStates.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isStatus(position)) {
                return VIEWTYPE_STATUS_VIEW;
            }

            Cursor cursor = (Cursor) getItem(position);
            return getItemViewType(cursor);
        }

        public int getItemViewType(Cursor cursor) {
            String type = cursor.getString(cursor
                                               .getColumnIndex(MessageEntry.COLUMN_MESSAGE_TYPE));
            if (OPMessage.OPMessageType.TYPE_INERNAL_CALL_AUDIO.equals(type)
                || OPMessage.OPMessageType.TYPE_INERNAL_CALL_VIDEO.equals(type)) {
                return VIEWTYPE_CALL_VIEW;
            }
            if (OPConversationEvent.EventTypes.ContactsChange.toString().equals(type)) {
                return VIEWTYPE_CONVERSATION_EVENT_VIEW;
            }

            long sender_id = cursor.getLong(cursor
                                                .getColumnIndex(MessageEntry.COLUMN_SENDER_ID));
            if (sender_id == OPDataManager.getInstance().getSharedAccount()
                .getSelfContactId()) {
                return VIEWTYPE_SELF_MESSAGE_VIEW;
            }
            return VIEWTYPE_RECIEVED_MESSAGE_VIEW;

        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (isStatus(position)) {
                if (convertView == null) {
                    convertView = new ComposingStatusView(parent.getContext());
                }
                ((ComposingStatusView) convertView).update(
                    (OPUser) getItem(position), null);

                return convertView;

            } else {
                Cursor cursor = (Cursor) getItem(position);
                if (cursor != null) {

                    if (convertView == null) {
                        convertView = newView(mContext, cursor, parent);
                    }
                    if (convertView instanceof CallItemView) {
                        CallItem callItem = CallItem.fromCursor(cursor);
                        ((CallItemView) convertView).update(callItem);
                    } else {
                        OPMessage message = OPModelCursorHelper
                            .messageFromCursor(cursor);
                        if (convertView instanceof SelfMessageView) {
                            if (position == myLastReadMessagePosition
                                || position == myLastDeliveredMessagePosition
                                || position == myLastSentMessagePosition) {
                                ((SelfMessageView) convertView).update(message,
                                                                       true);
                            } else {
                                ((SelfMessageView) convertView).update(message,
                                                                       false);
                            }
                        } else if (convertView instanceof PeerMessageView) {
                            ((PeerMessageView) convertView).update(message);
                        } else if (convertView instanceof ConversationEventView) {
                            ((ConversationEventView) convertView)
                                    .update(message);
                        }
                    }
                }
                return convertView;
            }
        }

        @Override
        protected void onContentChanged() {
            setupDeliveryStatuses(mCursor);
            super.onContentChanged();
        }

        @Override
        public void changeCursor(Cursor cursor) {
            setupDeliveryStatuses(cursor);
            super.changeCursor(cursor);
        }

        // this function will not be called for status view
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup arg2) {
            int viewType = getItemViewType(cursor);
            View view = null;
            switch (viewType){
            case VIEWTYPE_SELF_MESSAGE_VIEW:
                view = (SelfMessageView) LayoutInflater.from(context).inflate(
                    R.layout.item_message_self, null);
                ((SelfMessageView) view).setSession(mSession);
                break;
            case VIEWTYPE_RECIEVED_MESSAGE_VIEW:
                view = new PeerMessageView(context);
                ((PeerMessageView) view).setSession(mSession);

                break;
            case VIEWTYPE_STATUS_VIEW:
                view = new ComposingStatusView(context);
                break;
            case VIEWTYPE_CALL_VIEW:
                view = new CallItemView(context);
                break;
            case VIEWTYPE_CONVERSATION_EVENT_VIEW:
                view = (ConversationEventView) LayoutInflater.from(context)
                        .inflate(R.layout.item_conversation_event, null);

                break;
            }

            return view;
        }

        private void setupDeliveryStatuses(Cursor cursor) {
            myLastReadMessagePosition = -1;
            myLastDeliveredMessagePosition = -1;
            myLastSentMessagePosition = -1;
            if (cursor != null && cursor.getCount() > 0) {
                int position = cursor.getCount() - 1;
                cursor.moveToLast();
                while (!cursor.isBeforeFirst()) {
                    String status = cursor
                        .getString(cursor
                                       .getColumnIndex(MessageEntry
                                                           .COLUMN_MESSAGE_DELIVERY_STATUS));
                    if (!TextUtils.isEmpty(status)) {
                        MessageDeliveryStates messageState = MessageDeliveryStates
                            .valueOf(status);
                        if (messageState == MessageDeliveryStates.MessageDeliveryState_Read) {
                            // if we found the first read message then break out of the loop
                            myLastReadMessagePosition = position;

                            break;
                        } else if (messageState == MessageDeliveryStates.MessageDeliveryState_Sent
                            && myLastReadMessagePosition == -1
                            && myLastDeliveredMessagePosition == -1
                            && myLastSentMessagePosition == -1) {
                            myLastSentMessagePosition = position;
                        } else if (messageState == MessageDeliveryStates
                            .MessageDeliveryState_Delivered
                            && myLastReadMessagePosition == -1
                            && myLastDeliveredMessagePosition == -1) {
                            // if this is the first delivered state we've encountered before we
                            // see a
                            // read message, remember it
                            // and keep looking for the first read message
                            myLastDeliveredMessagePosition = position;

                        }
                    }
                    cursor.moveToPrevious();
                    position--;
                }
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
        case R.id.menu_call:{
            if(mSession==null){
                //TODO: error handling
                return true;
            }
            if (OPDataManager.getInstance().getSharedAccount()
                .getState(0, null) != AccountStates.AccountState_Ready) {
                BaseActivity.showInvalidStateWarning(getActivity());
                return true;
            }
            OPCall call = mSession.getCurrentCall();
            if (mSession.getCurrentCall() != null) {
                Intent intent = new Intent(getActivity(), CallActivity.class);
                intent.putExtra(IntentData.ARG_CALL_ID, call.getCallID());
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        }
        case R.id.menu_audio:
            onCallMenuSelected(false);
            return true;
        case R.id.menu_video:
            onCallMenuSelected(true);
            return true;
        case R.id.menu_add:
            onProfilePickerClick();
            return true;
        case R.id.menu_topic:
            onTopicMenuSelected();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLongArray(IntentData.ARG_PEER_USER_IDS,
                              OPModelUtils.getUserIds(mParticipantInfo.getParticipants()));
        if (!TextUtils.isEmpty(mConversationId)) {
            outState.putString(IntentData.ARG_CONVERSATION_ID, mConversationId);
        }
        outState.putString(IntentData.ARG_CONVERSATION_TYPE, mType);
        outState.putLong(IntentData.ARG_CONVERSATION_TYPE, mParticipantInfo.getCbcId());
        super.onSaveInstanceState(outState);
    }

    // After adding a new participant we'll have to switch chat window
    private void onProfilePickerClick() {
        List<OPUser> users = mParticipantInfo.getParticipants();
        if (users.size() == 1) {
            Intent intent = new Intent(getActivity(), ProfilePickerActivity.class);
            intent.putExtra(IntentData.ARG_PEER_USER_IDS,
                            OPModelUtils.getUserIds(mParticipantInfo.getParticipants()));
            startActivityForResult(intent,
                                   IntentData.REQUEST_CODE_ADD_CONTACTS);
        } else {
            Intent intent = new Intent(getActivity(), ParticipantsManagementActivity.class);
            intent.putExtra(IntentData.ARG_PEER_USER_IDS,
                            OPModelUtils.getUserIds(mParticipantInfo.getParticipants()));
            startActivityForResult(intent, IntentData.REQUEST_CODE_PARTICIPANTS);
        }
    }

    //this is stupid. There must be a better way to persist this state;
    boolean mVideo;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
        case IntentData.REQUEST_CODE_ADD_CONTACTS:
            if (resultCode == Activity.RESULT_OK) {
                long userIds[] = data
                    .getLongArrayExtra(IntentData.ARG_PEER_USER_IDS);
                List<OPUser> newParticipants = OPDataManager.getDatastoreDelegate().getUsers(userIds);
                switch (OPSdkConfig.getInstance().getGroupChatMode()){
                case contact:{
                    newParticipants.addAll(mParticipantInfo.getParticipants());
                    mParticipantInfo = new ParticipantInfo(OPModelUtils.getWindowId(newParticipants)
                        , newParticipants);
                    getNewContactsBasedConversation();
                    onContactsChanged();
                }
                break;
                case thread:{
                    if (mSession.getType() == GroupChatMode.contact) {
                        newParticipants.addAll(mParticipantInfo.getParticipants());
                        mParticipantInfo = new ParticipantInfo(
                            OPModelUtils.getWindowId(newParticipants), newParticipants);
                        getNewThreadBasedConversation();
                        onContactsChanged();
                    } else {
                        mSession.addParticipants(newParticipants);
                    }
                }
                break;
                }
            }
            break;
        case IntentData.REQUEST_CODE_PARTICIPANTS:
            if (resultCode == Activity.RESULT_OK) {
                long userIds[] = data.getLongArrayExtra(IntentData.ARG_PEER_USER_IDS);
                List<OPUser> newParticipants = OPDataManager.getDatastoreDelegate().getUsers
                    (userIds);
                mParticipantInfo = new ParticipantInfo(OPModelUtils.getWindowId(newParticipants)
                    , newParticipants);
                switch (OPSdkConfig.getInstance().getGroupChatMode()){
                case contact:{

                    getNewContactsBasedConversation();
                    onContactsChanged();
                }
                break;
                case thread:{
                    if (mSession != null) {
                        if (mSession.getType() == GroupChatMode.contact) {
                            getNewThreadBasedConversation();

                            onContactsChanged();
                        } else {
                            mSession.onContactsChanged(newParticipants);
                        }
                    } else {

                    }
                }
                break;
                }
            }
            break;
        case IntentData.REQUEST_CODE_GET_CALLEE:
            if (resultCode == Activity.RESULT_OK) {
                long userIds[] = data
                    .getLongArrayExtra(IntentData.ARG_PEER_USER_IDS);
                makeCall(userIds, mVideo);
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void onCallMenuSelected(boolean video) {
        if (mSession.getParticipants().size() == 1) {
            makeCall(mSession.getParticipantIDs(), video);
        } else {
            //TODO: launch profile picker
            mVideo = video;
            Intent intent = new Intent(getActivity(), ProfilePickerActivity.class);
            intent.putExtra(IntentData.ARG_USER_IDS_INCLUDE, mSession.getParticipantIDs());
            startActivityForResult(intent,
                                   IntentData.REQUEST_CODE_GET_CALLEE);
        }
    }

    void onTopicMenuSelected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.layout_set_topic, null);
        final EditText editText = (EditText) view.findViewById(R.id.text);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText() != null) {

                    String topic = editText.getText().toString();
                    if (checkBox.isChecked()) {
                        //TODO: create new session
                        mSession = ConversationManager.getInstance().
                            getConversation(GroupChatMode.thread,
                                            mSession.getParticipantInfo(),
                                            null, true);
                        mConversationId = mSession.getConversationId();

                        mSession.setTopic(topic);
                    } else {
                        mSession.setTopic(topic);
                    }
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
    private void makeCall(long[] userIds,boolean video) {
        Intent intent = new Intent(getActivity(), CallActivity.class);
        intent.putExtra(IntentData.ARG_PEER_USER_IDS, userIds);
        intent.putExtra(IntentData.ARG_VIDEO, video);
        intent.putExtra(IntentData.ARG_CONVERSATION_ID,mSession.getConversationId());

        startActivity(intent);
    }

    // Begin: CursorCallback implementation
    private static final int URL_LOADER = 0;

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
        switch (loaderID){
        case URL_LOADER:
            Uri uri = getMessagesUri();
            if (uri == null) {
                return null;
            }
            // Returns a new CursorLoader
            mLoader= new CursorLoader(getActivity(), // Parent activity context
                                    uri,
                                    null,
                                    null, // No selection clause
                                    null, // No selection arguments
                                    "time asc" // Default sort order
            );
            return mLoader;
        default:
            // An invalid id was passed in
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("test", "ChatFragment onLoadFinished" + cursor);
        mAdapter.changeCursor(cursor);
        mSession.markAllMessagesRead();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.changeCursor(null);
    }

    Uri getMessagesUri() {

        if (TextUtils.isEmpty(mConversationId)) {
            return null;
        }
        return OPContentProvider.getContentUri(
            MessageEntry.URI_PATH_INFO_CONTEXT_URI_BASE + mConversationId);
    }

    void getNewContactsBasedConversation() {
        mType = GroupChatMode.contact.name();
        mSession = ConversationManager.getInstance().
            getConversation(GroupChatMode.contact, mParticipantInfo, null, true);
        mConversationId = mSession.getConversationId();
    }

    void getNewThreadBasedConversation() {
        mSession = ConversationManager.getInstance().
            getConversation(GroupChatMode.thread, mParticipantInfo, null, true);
        mConversationId = mSession.getConversationId();
        mType = GroupChatMode.thread.toString();

    }
    // Beginning of SessionListener implementation
    static final int MENUID_DELETE_MESSAGE = 10000;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v == mMessagesList) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;

            if (acmi.targetView instanceof SelfMessageView
                && ((SelfMessageView) acmi.targetView).canEditMessage()) {
                menu.add(0, MENUID_DELETE_MESSAGE, Menu.NONE, "delete");
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
        case MENUID_DELETE_MESSAGE:
            AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
                .getMenuInfo();
            ((SelfMessageView) acmi.targetView).onDeleteSelected();
            break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onContactComposingStateChanged(ComposingStates composingStates,
                                               OPUser contact) {
        mAdapter.notifyDataSetChanged(composingStates, contact);
    }

    @Override
    public boolean onNewMessage(OPMessage message) {
        return false;
    }

    @Override
    public boolean onPushMessage(OPMessage message) {
        return false;
    }

    @Override
    public boolean onContactsChanged() {
        if (mSession != null) {
            mParticipantInfo = mSession.getParticipantInfo();
        }
        updateUsersView();
        getLoaderManager().restartLoader(URL_LOADER, null, this);

        return true;
    }

    @Override
    public boolean onConversationTopicChanged(String newTopic) {
        setTitle(newTopic);
        return true;
    }

    void setTitle(String title) {
        getActivity().getActionBar().setTitle(title);
    }
    // End of SessionListener implementation
}
