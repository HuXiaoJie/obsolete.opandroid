package com.openpeer.sample.delegates;

import android.content.Intent;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.sample.BackgroundingManager;
import com.openpeer.sample.IntentData;
import com.openpeer.sample.OPApplication;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sample.conversation.CallActivity;
import com.openpeer.sample.events.CallStateChangeEvent;
import com.openpeer.sdk.model.HOPCallDelegate;
import com.openpeer.sdk.model.HOPCallManager;

public class CallDelegateImpl implements HOPCallDelegate {
    private static CallDelegateImpl instance;

    public static CallDelegateImpl getInstance() {
        if (instance == null) {
            instance = new CallDelegateImpl();
        }
        return instance;
    }

    private CallDelegateImpl() {
    }

    @Override
    public void onCallStateChanged(OPCall call, CallStates state) {
        switch (state){
        case CallState_Incoming:{
            String callId = call.getCallID();

            Intent callIntent = new Intent(OPApplication.getInstance(), CallActivity.class);
            callIntent.putExtra(IntentData.ARG_CALL_ID, callId);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            OPApplication.getInstance().startActivity(callIntent);
        }
        break;
        case CallState_Closed:{
            String callId = call.getCallID();
            OPNotificationBuilder.cancelNotificationForCall(callId);

            if (!HOPCallManager.getInstance().hasCalls() &&
                BackgroundingManager.isBackgroundingPending()) {
                BackgroundingManager.onEnteringBackground();
            }
        }
        break;
        }
        new CallStateChangeEvent(call, state).post();
    }
}
