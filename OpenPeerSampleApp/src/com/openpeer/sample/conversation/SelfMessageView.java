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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openpeer.javaapi.OPMessage;
import com.openpeer.sample.R;
import com.openpeer.sample.util.DateFormatUtils;
import com.openpeer.sdk.app.HOPDataManager;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.MessageEditState;

public class SelfMessageView extends RelativeLayout {
    private static final long FREEZING_PERIOD = 60 * 60 * 1000l;
    OPMessage mMessage;
    HOPConversation mSession;
    TextView title;

    TextView time;
    TextView text;
    View editedIndicator;
    TextView deliveryStatusView;
    int viewType;

    public void setup() {
        title = (TextView) findViewById(R.id.user);
        text = (TextView) findViewById(R.id.message);
        time = (TextView) findViewById(R.id.time);
        editedIndicator = findViewById(R.id.edit);
        deliveryStatusView = (TextView) findViewById(R.id.status);

    }

    public SelfMessageView(Context context) {
        this(context, null, 0);
    }

    public SelfMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.layout_message_self, this);
        setup();
    }

    public SelfMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void update(OPMessage data,boolean showDeliveryState) {
        mMessage = data;

        time.setText(DateFormatUtils.getSameDayTime(data.getTime()
                .toMillis(false)));

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
        if(showDeliveryState){
            showStatusOfMyLastMessage();
        } else {
            deliveryStatusView.setVisibility(View.GONE);
        }
    }

    private void showStatusOfMyLastMessage() {
        switch (mMessage.getDeliveryStatus()) {
        case MessageDeliveryState_Delivered:
            deliveryStatusView.setText(R.string.label_delivered);
            deliveryStatusView.setVisibility(View.VISIBLE);
            break;
        case MessageDeliveryState_Read:
            deliveryStatusView.setText(R.string.label_read);
            deliveryStatusView.setVisibility(View.VISIBLE);
            break;
        case MessageDeliveryState_Sent:
            deliveryStatusView.setText(R.string.label_sent);
            deliveryStatusView.setVisibility(View.VISIBLE);
            break;
        default:
            break;

        }
    }

    /**
     * @param session
     */
    public void setSession(HOPConversation session) {
        mSession = session;
    }

    public OPMessage getMessage() {
        return mMessage;
    }

    public void onDeleteSelected() {
        OPMessage message = new OPMessage(HOPDataManager.getInstance().getCurrentUserId(),
                OPMessage.TYPE_TEXT,
                "",
                System.currentTimeMillis(),
                OPMessage.generateUniqueId());
        message.setReplacesMessageId(mMessage.getMessageId());
        mSession.sendMessage(message, false);
    }

    public boolean canEditMessage() {
        return mMessage.getTime().toMillis(false) > System.currentTimeMillis()
                - FREEZING_PERIOD
                && mMessage.getEditState() != MessageEditState.Deleted;
    }

}
