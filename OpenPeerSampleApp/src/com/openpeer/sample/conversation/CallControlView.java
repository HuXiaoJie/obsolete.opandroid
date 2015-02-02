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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openpeer.javaapi.CallClosedReasons;
import com.openpeer.javaapi.OPCall;
import com.openpeer.sample.R;

public class CallControlView extends LinearLayout {
	private OPCall mCall;
	private ImageView mAnswerButton;
	private ImageView mEndButton;
	private View mPaddingView;

	CallActionListener mListener;

	public CallControlView(Context context) {
		this(context, null, 0);
	}

	public CallControlView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public CallControlView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.layout_call_control, this);
		mAnswerButton = (ImageView) findViewById(R.id.answer);
		mEndButton = (ImageView) findViewById(R.id.end);
		mPaddingView = findViewById(R.id.padview);
	}

	public void bindCall(OPCall call) {
		mCall = call;
		mEndButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                mCall.hangup(CallClosedReasons.CallClosedReason_User);
                //test
                Log.d("test","call closed reason "+mCall.getClosedReason());
				if (mListener != null) {
					mListener.onEndClick();
				}
			}
		});
		if (mCall.isOutgoing()) {
			removeView(mAnswerButton);
			removeView(mPaddingView);
		}
		updateView();
	}

	public void updateView() {
		switch (mCall.getState()) {
		case CallState_Incoming:
			mAnswerButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCall.answer();
					removeView(mAnswerButton);
					removeView(mPaddingView);
					if (mListener != null) {
						mListener.onAnswerClick();
					}
				}
			});

			break;
		case CallState_Hold:
			break;
		case CallState_Closing:
		case CallState_Closed:
			break;
		case CallState_Open:
			break;
		default:
		}
	}

	public void registerActionListener(CallActionListener listener) {
		mListener = listener;
	}

	public interface CallActionListener {
		public void onAnswerClick();

		public void onEndClick();

	}

}
