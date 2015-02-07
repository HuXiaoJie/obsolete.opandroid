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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.R;
import com.openpeer.sample.util.DateFormatUtils;
import com.openpeer.sdk.app.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.HOPConversation;

public class PeerMessageView extends RelativeLayout {
    OPMessage mMessage;
    HOPConversation mSession;
    ImageView avatarView;
    TextView title;
    View editedIndicator;

    TextView time;
    TextView text;

    public void setup() {
        title = (TextView) findViewById(R.id.user);
        avatarView = (ImageView) findViewById(R.id.avatar);
        text = (TextView) findViewById(R.id.message);
        time = (TextView) findViewById(R.id.time);
        editedIndicator = findViewById(R.id.edit);
    }

    public PeerMessageView(Context context) {
        this(context, null, 0);
    }

    public PeerMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.item_message_peer, this);
        setup();
    }

    public PeerMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void update(OPMessage data) {
        mMessage = data;

        HOPContact user = HOPDataManager.getInstance().getUserById(data.getSenderId());
        if (user != null) {
            if (user.getName() != null) {
                title.setText(user.getName());
            }
        }

        time.setText(DateFormatUtils.getSameDayTime(data.getTime().toMillis(false)));
        switch (data.getEditState()) {
        case Deleted:
            text.setText(R.string.msg_deleted);
            text.setEnabled(false);
            editedIndicator.setVisibility(View.GONE);

            break;
        case Edited:
            text.setText(data.getMessage());
            text.setEnabled(true);
            editedIndicator.setVisibility(View.VISIBLE);

            break;
        default:
            text.setText(data.getMessage());
            text.setEnabled(true);
            editedIndicator.setVisibility(View.GONE);

        }
    }

    /**
     * @param session
     */
    public void setSession(HOPConversation session) {
        mSession = session;
    }
}
