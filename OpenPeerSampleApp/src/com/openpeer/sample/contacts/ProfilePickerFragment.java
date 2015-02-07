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
package com.openpeer.sample.contacts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.openpeer.sample.BaseFragment;
import com.openpeer.sample.IntentData;
import com.openpeer.sample.R;
import com.openpeer.sample.view.ProgressEmptyView;
import com.openpeer.sdk.app.HOPDataManager;
import com.openpeer.sdk.datastore.DatabaseContracts.RolodexContactEntry;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfilePickerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private SwipeRefreshLayout mRootLayout;
    @InjectView(R.id.listview)
    ListView mListView;
    private ContactsAdapter mAdapter;
    List<Long> chosenUserIds = new ArrayList<Long>();
    long[] mIdsExclude;
    // Used for picking a user already in a conversation
    long[] mIdsInclude;
    MenuItem mDoneMenu;
    @InjectView(R.id.empty_view)
    ProgressEmptyView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        mIdsInclude = getArguments().getLongArray(IntentData.ARG_USER_IDS_INCLUDE);
        mIdsExclude = getArguments().getLongArray(IntentData.ARG_PEER_USER_IDS);
        return setupView(inflater.inflate(R.layout.fragment_profile_picker, null));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(URL_LOADER, null, this);

    }

    private View setupView(View view) {
        ButterKnife.inject(this,view);
        mListView.setEmptyView(emptyView);
        mRootLayout = (SwipeRefreshLayout) view;
        mRootLayout.setOnRefreshListener(this);
        mAdapter = new ContactsAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_picker, menu);
        mDoneMenu = menu.findItem(R.id.menu_done);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setOnQueryTextListener(this);
        updateDoneText();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    class ContactsAdapter extends CursorAdapter {

        public ContactsAdapter(Context context, Cursor c) {
            super(context, c);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((ViewHolder) view.getTag()).update(cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup arg2) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_contact_picker, null);
            view.setTag(new ViewHolder(view));
            return view;
        }

        class ViewHolder {
            CheckBox checkbox;
            ImageView imageView;
            TextView nameView;

            ViewHolder(View view) {
                checkbox = (CheckBox) view.findViewById(R.id.checkBox);
                imageView = (ImageView) view.findViewById(R.id.image_view);
                nameView = (TextView) view.findViewById(R.id.title);
            }

            public void update(Cursor cursor) {
                long rolodexId = cursor.getLong(0);
                final long userId = cursor.getLong(1);
                String avatar = HOPDataManager.getInstance().getAvatarUri(
                        rolodexId, 48, 48);
                String name = cursor
                        .getString(cursor
                                .getColumnIndex(RolodexContactEntry.COLUMN_CONTACT_NAME));
                Picasso.with(getActivity()).load(avatar).into(imageView);
                nameView.setText(name);
                checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                            boolean arg1) {
                        if (arg1) {
                            chosenUserIds.add(userId);
                        } else {
                            chosenUserIds.remove(userId);
                        }
                        updateDoneText();
                    }

                });
            }
        }

    }

    @Override
    public void onRefresh() {
        HOPDataManager.getInstance().refreshContacts();
        mRootLayout.setRefreshing(false);
    }

    // Begin: CursorCallback implementation
    private static final int URL_LOADER = 0;

    // static final String LIST_PROJECTION[] = { BaseColumns._ID, COLUMN_CONTACT_NAME, COLUMN_AVATAR_URI,
    // ContactsEntry.COLUMN_STABLE_ID, ContactsEntry.COLUMN_USER_ID };

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
        switch (loaderID){
        case URL_LOADER:
            StringBuilder builder = null;
            if (mIdsInclude != null) {
                builder = new StringBuilder(
                    RolodexContactEntry.COLUMN_OPENPEER_CONTACT_ID + "  in (");
                for (int i = 0; i < mIdsInclude.length; i++) {
                    if (i == mIdsInclude.length - 1) {
                        builder.append(mIdsInclude[i] + ")");
                    } else {
                        builder.append(mIdsInclude[i] + ",");
                    }
                }
            } else {
                builder = new StringBuilder(
                    RolodexContactEntry.COLUMN_OPENPEER_CONTACT_ID + " not in (0,");
                for (int i = 0; i < mIdsExclude.length; i++) {
                    if (i == mIdsExclude.length - 1) {
                        builder.append(mIdsExclude[i] + ")");
                    } else {
                        builder.append(mIdsExclude[i] + ",");
                    }
                }
            }

            String slectionArgs[] = null;
            if (arg1 != null) {
                String query = arg1.getString("query");
                Log.d("test", "ContactsFragment onCreateLoader query " + query);
                if (!TextUtils.isEmpty(query)) {
                    // Note: instr is available from sqlite 3.7.15
                    builder.append("and name like ?");
                    slectionArgs = new String[]{"%" + query + "%"};
                }
            }
            emptyView.showProgress();
            return new CursorLoader(getActivity(), // Parent activity context
                                    HOPDataManager.getInstance()
                                        .getContentUri(RolodexContactEntry.URI_PATH_INFO),

                                    null, // Projection to return
                                    builder.toString(),
                                    slectionArgs,
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
        if(cursor.getCount()==0){
            emptyView.showText();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.changeCursor(null);

    }

    // End: CursorCallback implementation
    private void updateDoneText() {
        mDoneMenu.setTitle(getActivity().getString(R.string.menu_done,
                "" + chosenUserIds.size()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_done:

            if(chosenUserIds!=null&& !chosenUserIds.isEmpty()) {
                Intent data = new Intent();
                long[] ids = new long[chosenUserIds.size()];
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = chosenUserIds.get(i);
                }
                data.putExtra(IntentData.ARG_PEER_USER_IDS, ids);
                this.getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }else{
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // BEGINNING OF INTERFACE IMPLEMENTATION
    static String oldQuery;

    public boolean onQueryTextChange(String newText) {

        if (oldQuery == null && TextUtils.isEmpty(newText)) {
            return true;
        }
        oldQuery = newText;
        Bundle params = new Bundle();
        params.putString("query", newText);
        getLoaderManager().restartLoader(URL_LOADER, params,
                ProfilePickerFragment.this);

        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // END OF INTERFACE IMPLEMENTATION
}
