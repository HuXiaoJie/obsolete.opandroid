package com.openpeer.sample.events;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;

/**
 * Created by brucexia on 2015-02-05.
 */
public class CallStateChangeEvent extends BaseEvent{
    OPCall call;
    CallStates state;

    public CallStateChangeEvent(OPCall call, CallStates state) {
        this.call = call;
        this.state = state;
    }

    public OPCall getCall() {
        return call;
    }

    public CallStates getState() {
        return state;
    }
}
