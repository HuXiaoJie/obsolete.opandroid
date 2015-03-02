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

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openpeer.sample.R;
import com.openpeer.sample.util.DateFormatUtils;
import com.squareup.picasso.Picasso;

public class ChatInfoItemView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mBadgeView;
    private TextView mTitleView;
    private TextView mLastMessageView;
    private TextView mTimeView;

    public ChatInfoItemView(Context context) {
        this(context, null, 0);
    }

    public ChatInfoItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.item_chat_info, this);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mBadgeView = (TextView) findViewById(R.id.badge_view);

        mTitleView = (TextView) findViewById(R.id.title);
        mLastMessageView = (TextView) findViewById(R.id.text_message);
        mTimeView = (TextView) findViewById(R.id.time_view);
    }

    public ChatInfoItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public void updateData(final ChatInfo chatInfo) {
        mTitleView.setText(chatInfo.getNameString());
        String msg = chatInfo.getLastMessage();
        Long time = chatInfo.getLastMessageTime();

        int unreadCount = chatInfo.getUnreadCount();

        this.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConversationActivity.launchForChat(getContext(),
                                                   chatInfo.getType(),
                                                   chatInfo.getConversationId(),
                                                   chatInfo.getUserIDs());
            }
        });
        String avatar = chatInfo.getAvatarUri(48, 48);
        if(!TextUtils.isEmpty(avatar)) {
            Picasso.with(getContext()).load(avatar).into(mImageView);
        }

        if (msg != null) {
            mLastMessageView.setText(msg);
            mTimeView.setText(DateFormatUtils.getSameDayTime(time));
        }
        if (unreadCount > 0) {
            mBadgeView.setVisibility(View.VISIBLE);
            mBadgeView.setText("" + unreadCount);
        } else {
            mBadgeView.setVisibility(View.GONE);
        }
    }

    public void onLongPress() {
        Toast.makeText(getContext(), "TODO: on longpress show something",
                Toast.LENGTH_LONG);
    }

}
