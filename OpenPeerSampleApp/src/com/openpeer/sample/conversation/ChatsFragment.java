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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.openpeer.sample.BaseFragment;
import com.openpeer.sample.R;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.datastore.DatabaseContracts.WindowViewEntry;
import com.openpeer.sdk.model.HOPConversation;

public class ChatsFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ChatInfoAdaptor mAdapter;
    private List<HOPConversation> mSessions;
    private ListView mMessagesList;

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        // args.putString(IntentData.ARG_PEER_CONTACT_ID, peerContactId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChatsFragment newTestInstance() {
        ChatsFragment fragment = new ChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_chats, null);
        return setupView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(URL_LOADER,null,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    View setupView(View view) {
        View emptyView = view.findViewById(R.id.empty_view);
        emptyView.findViewById(R.id.start_chat)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof ChatsViewListener) {
                            ((ChatsViewListener) getActivity())
                                    .onChatsEmptyViewClick();
                        }
                    }
                });
        mMessagesList = (ListView) view.findViewById(R.id.listview);
        mMessagesList.setEmptyView(emptyView);
        mAdapter = new ChatInfoAdaptor(getActivity(), null);
        mMessagesList.setAdapter(mAdapter);
        getLoaderManager().initLoader(URL_LOADER, null, this);

        return view;
    }

    private List<HOPConversation> getSessions() {
        if (mSessions == null) {
            mSessions = new ArrayList<HOPConversation>();
            // TODO: do some setup
        }
        return mSessions;
    }

    class ChatInfoAdaptor extends CursorAdapter {

        public ChatInfoAdaptor(Context context, Cursor c) {
            super(context, c);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ChatInfo info = ChatInfo.fromCursor(cursor);
            ((ChatInfoItemView) view).updateData(info);
        }

        @Override
        public View newView(Context view, Cursor cursor, ViewGroup arg2) {
            // TODO Auto-generated method stub
            return new ChatInfoItemView(arg2.getContext());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_chat, menu);
    }

    // Begin: CursorCallback implementation
    private static final int URL_LOADER = 0;

    // static final String LIST_PROJECTION[] = { BaseColumns._ID,
    // WindowViewEntry.COLUMN_PARTICIPANT_NAMES,
    // WindowViewEntry.COLUMN_LAST_MESSAGE,
    // WindowViewEntry.COLUMN_LAST_MESSAGE_TIME,
    //
    // WindowViewEntry.COLUMN_USER_ID,
    // WindowViewEntry.COLUMN_WINDOW_ID };

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
        switch (loaderID) {
        case URL_LOADER:
            // Returns a new CursorLoader
            return new CursorLoader(
                    getActivity(), // Parent activity context
                    getChatsUri(),

                    null,// LIST_PROJECTION, // Projection to return
                    null, // No selection clause
                    null, // No selection arguments
                    null // Default sort order
            );
        default:
            // An invalid id was passed in
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.changeCursor(null);

    }

    // End: CursorCallback implementation
    public static interface ChatsViewListener {
        public void onChatsEmptyViewClick();
    }
    public Uri getChatsUri() {
        return HOPDataManager.getInstance()
            .getContentUri(WindowViewEntry.URI_PATH_INFO_CONTEXT);
    }
}
