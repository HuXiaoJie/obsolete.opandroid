package com.openpeer.sdk.model;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;

/**
 * Created by brucexia on 2015-02-05.
 */
public interface CallDelegate {
    public void onCallStateChanged(OPCall call, CallStates state);
}
