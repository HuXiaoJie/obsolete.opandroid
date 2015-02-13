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

import android.content.BroadcastReceiver;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.openpeer.sample.BaseFragment;
import com.openpeer.sample.R;
import com.openpeer.sample.view.ProgressEmptyView;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.datastore.DatabaseContracts.RolodexContactEntry;
import com.openpeer.sdk.model.HOPAccount;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ContactsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>,
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	SwipeRefreshLayout mRootLayout;
    @InjectView(R.id.listview)
	ListView mListView;
	private ContactsAdapter mAdapter;
	private boolean mTest;
    @InjectView(R.id.empty_view)
    ProgressEmptyView emptyView;

	public static android.support.v4.app.Fragment newInstance() {
		return new ContactsFragment();
	}

	public static ContactsFragment newTestInstance() {
		ContactsFragment fragment = new ContactsFragment();
		fragment.mTest = true;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contacts, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        setupView(view);
		getLoaderManager().initLoader(URL_LOADER, null, this);

	}

	private View setupView(View view) {
        ButterKnife.inject(this,view);
		mListView.setEmptyView(emptyView);
		mRootLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
		mRootLayout.setOnRefreshListener(this);
		mAdapter = new ContactsAdapter(getActivity(), null);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				((ContactItemView) view).click();
			}
		});

		setupContent();

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_contacts, menu);
		// SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setOnQueryTextListener(this);
		// Assumes current activity is the searchable activity
		// searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		// SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
		// searchView.setSearchableInfo(info);

		// searchView.setSubmitButtonEnabled(true);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	static class ContactsAdapter extends CursorAdapter {

		public ContactsAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((ContactItemView) view).updateData(cursor);
		}

		@Override
		public View newView(Context view, Cursor cursor, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return new ContactItemView(arg2.getContext());
		}

	}

	@Override
	public void onRefresh() {
		HOPAccount.currentAccount().refreshContacts();
		mRootLayout.setRefreshing(false);
	}

	static String oldQuery;

	void setupContent() {
		// mAdapter.mContacts = OPDataManager.getInstance()
		// .getContacts(0);
		//
		// mAdapter.notifyDataSetChanged();
	}

	class DataChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// Log.d("test", "received broadcast " + arg1.getAction());
			// setupContent();
		}
	}

	// Begin: CursorCallback implementation
	private static final int URL_LOADER = 0;
//	static final String LIST_PROJECTION[] = { BaseColumns._ID, COLUMN_CONTACT_NAME, COLUMN_AVATAR_URI,
//			ContactsEntry.COLUMN_STABLE_ID, ContactsEntry.COLUMN_USER_ID };

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
//        StringBuilder builder = new StringBuilder("not (" + RolodexContactEntry.COLUMN_USER_ID + "=0 and " + ContactsEntry.COLUMN_STABLE_ID + " not null)");
//        String slectionArgs[] = null;
//		if (arg1 != null) {
//			String query = arg1.getString("query");
//			Log.d("test", "ContactsFragment onCreateLoader query " + query);
//			if (!TextUtils.isEmpty(query)) {
//				// Note: instr is available from sqlite 3.7.15
//				builder.append(" and name like ?");
//				slectionArgs = new String[] { "%" + query + "%" };
//			}
//
//		}
        switch (loaderID) {
        case URL_LOADER:
            emptyView.showProgress();
            return new CursorLoader(getActivity(), // Parent activity context
                    HOPDataManager.getInstance()
                            .getContentUri(RolodexContactEntry.URI_PATH_INFO),

                    null, // Projection to return
                    null,// builder.toString(), // No selection clause
                    null,// slectionArgs, // No selection arguments
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
            emptyView.showProgress();
        }

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);

	}

	// End: CursorCallback implementation

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// BEGINNING OF INTERFACE IMPLEMENTATION

	public boolean onQueryTextChange(String newText) {

		if (oldQuery == null && TextUtils.isEmpty(newText)) {
			return true;
		}
		oldQuery = newText;
		Bundle params = new Bundle();
		params.putString("query", newText);
		getLoaderManager().restartLoader(URL_LOADER, params, ContactsFragment.this);

		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	public boolean onClose() {
		return false;
	}

	// END OF INTERFACE IMPLEMENTATION

}
