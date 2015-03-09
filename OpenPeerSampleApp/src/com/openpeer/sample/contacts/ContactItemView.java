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

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openpeer.sample.R;
import com.openpeer.sample.conversation.ConversationActivity;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.GroupChatMode;
import com.squareup.picasso.Picasso;

public class ContactItemView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTitleView;

    private View mChatView;
    private View mInviteView;
    long mUserId;
    private long rolodexId;

    public ContactItemView(Context context) {
        this(context, null, 0);
    }

    public ContactItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.item_contact, this);
        mImageView = (ImageView) findViewById(R.id.image_view);

        mTitleView = (TextView) findViewById(R.id.title);
        mChatView = findViewById(R.id.chat);
        mInviteView = findViewById(R.id.invite);
    }

    public ContactItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public void updateData(Cursor cursor) {
        rolodexId = cursor.getLong(0);
        mUserId = cursor.getLong(1);
        mTitleView.setText(cursor.getString(2));
        String avatar = HOPDataManager.getInstance().getAvatarUri(
            rolodexId, 48, 48);
        if(!TextUtils.isEmpty(avatar)) {
            Picasso.with(getContext()).load(avatar).into(mImageView);
        }

        if (mUserId != 0) {
            mInviteView.setVisibility(View.GONE);
//            mChatView.setVisibility(View.VISIBLE);
            final long mUserIds[] = {mUserId};

            mChatView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("test", "Chat button tapped");
                    click();
                }
            });

        } else {
//            mInviteView.setVisibility(View.VISIBLE);
            mInviteView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(
                        getContext(),
                        "To be implemented: Invite your friends to use Open peer!",
                        Toast.LENGTH_LONG).show();
                }
            });

            mChatView.setVisibility(View.GONE);
        }
    }

    public void click() {
        final long userIds[] = {mUserId};
        ConversationActivity.launchForChat(getContext(),
                                           GroupChatMode.contact.toString(),
                                           null,
                                           userIds);
    }

}
