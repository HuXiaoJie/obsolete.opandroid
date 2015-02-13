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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.R;
import com.openpeer.sample.util.DateFormatUtils;
import com.openpeer.sample.util.ModelUtil;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.HOPConversationEvent;

import java.util.List;

public class ConversationEventView extends LinearLayout {

    private TextView mTitleView;
    private TextView mTimeView;

    public ConversationEventView(Context context) {
        this(context, null, 0);
    }

    public ConversationEventView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.layout_conversation_event,
                                             this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTimeView = (TextView) findViewById(R.id.time);
    }

    public ConversationEventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void update(OPMessage message) {
        mTimeView.setText(DateFormatUtils.getSameDayTime(message.getTime().toMillis(false)));
        HOPConversationEvent.EventTypes eventType = HOPConversationEvent.EventTypes
            .valueOf(message.getMessageType());
        switch (eventType){
        case ContactsChange:{
            HOPConversationEvent.ContactsChange event = HOPConversationEvent.contactsChangeFromJson
                (message.getMessage());
            if ( event.getAdded().length>0) {
                List<HOPContact> users = HOPDataManager.getInstance().getUsers(event.getAdded
                    ());
                String names = ModelUtil.getNamesStringFromUsers(users);
                if (!TextUtils.isEmpty(names)) {
                    mTitleView.setText(names + " joined");
                }
            } else {
                List<HOPContact> users = HOPDataManager.getInstance().getUsers(
                    event.getRemoved());
                String names = ModelUtil.getNamesStringFromUsers(users);
                if (!TextUtils.isEmpty(names)) {
                    mTitleView.setText(names + " removed");
                }
            }
        }
        break;
        default:
        }

    }
}
