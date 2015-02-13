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

package com.openpeer.sample.conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.openpeer.sample.BaseFragment;
import com.openpeer.sample.IntentData;
import com.openpeer.sample.R;
import com.openpeer.sample.contacts.ProfilePickerActivity;
import com.openpeer.sample.view.UserItemView;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.utils.HOPModelUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class ParticipantsManagementFragment extends BaseFragment {

    long participantIds[];
    ParticipantsAdapter mAdapter;

    @InjectView(R.id.participantsView)
    GridView participantsView;

    @OnItemClick(R.id.participantsView)
    void onItemClick(int position) {
        mAdapter.onItemClick(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        participantIds = getArguments().getLongArray(IntentData.ARG_PEER_USER_IDS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
        savedInstanceState) {
        return inflater.inflate(R.layout.fragment_participants, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this,view);
        mAdapter = new ParticipantsAdapter();
        mAdapter.mUserList = HOPDataManager.getInstance().getUsers(participantIds);
        mAdapter.participantsView = new WeakReference<>(this);
        participantsView.setAdapter(mAdapter);
    }

    public List<HOPContact> getParticipants() {
        return mAdapter.mUserList;
    }

    static class ParticipantsAdapter extends BaseAdapter {
        boolean mDeleteMode;
        List<HOPContact> mUserList;
        WeakReference<ParticipantsManagementFragment> participantsView;

        public void onItemClick(int position) {
            Object object = getItem(position);
            if (object instanceof HOPContact) {
                if (mDeleteMode) {
                    List<HOPContact> users = new ArrayList<>();
                    users.add((HOPContact) object);
                    mUserList.remove(object);
                    if (mUserList.size() == 1) {
                        mDeleteMode = false;
                    }
                    notifyDataSetChanged();
                }
            } else if (object instanceof DummyAddAction) {
                ((DummyAddAction) object).onClick();
            } else if (object instanceof DummyRemoveAction) {
                ((DummyRemoveAction) object).onClick();
            }
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getCount() {
            return mUserList.size() == 1 ? 2 : mUserList.size() + 2;
        }

        public Object getItem(int position) {
            if (position < mUserList.size()) {
                return mUserList.get(position);
            } else if (mUserList.size() > 1 && position == mUserList.size()) {
                return new DummyRemoveAction();
            } else {
                return new DummyAddAction();
            }
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < mUserList.size()) {
                return 0;
            } else if (mUserList.size() > 1 && position == mUserList.size()) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object object = getItem(position);
            if (convertView == null) {
                if (object instanceof HOPContact) {
                    convertView = new UserItemView(parent.getContext());
                } else if (object instanceof DummyAddAction) {
                    return View.inflate(parent.getContext(), R.layout.view_add, null);
                } else if (object instanceof DummyRemoveAction) {
                    return View.inflate(parent.getContext(), R.layout.view_remove, null);
                }
                ((UserItemView) convertView).setDeleteMode(mDeleteMode);
                ((UserItemView) convertView).update(object);
            } else if (object instanceof HOPContact) {

                ((UserItemView) convertView).setDeleteMode(mDeleteMode);
                ((UserItemView) convertView).update(object);
            }
            return convertView;
        }

        class DummyAddAction {
            public void onClick() {
                participantsView.get().launchProfilePicker();
            }
        }

        class DummyRemoveAction {
            public void onClick() {
                mDeleteMode = !mDeleteMode;
                notifyDataSetChanged();
            }
        }
    }

    void launchProfilePicker() {
        Intent intent = new Intent(getActivity(), ProfilePickerActivity.class);
        intent.putExtra(IntentData.ARG_PEER_USER_IDS,
                        HOPModelUtils.getUserIdsArray(mAdapter.mUserList));
        startActivityForResult(intent, IntentData.REQUEST_CODE_ADD_CONTACTS);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            long userIds[] = data.getLongArrayExtra(IntentData.ARG_PEER_USER_IDS);
            List<HOPContact> users = HOPDataManager.getInstance().getUsers(userIds);
            mAdapter.mUserList.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }

    void setActivityResult() {
        Intent intent = new Intent();

        List<HOPContact> users = getParticipants();
        if (users != null && !users.isEmpty()) {
            intent.putExtra(IntentData.ARG_PEER_USER_IDS, HOPModelUtils.getUserIdsArray(users));
            getActivity().setResult(Activity.RESULT_OK, intent);
        }
    }
}
