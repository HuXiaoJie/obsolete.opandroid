package com.openpeer.sample.events;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.sdk.model.HOPCall;

/**
 * Created by brucexia on 2015-02-05.
 */
public class CallStateChangeEvent extends BaseEvent{
    HOPCall call;
    CallStates state;

    public CallStateChangeEvent(HOPCall call, CallStates state) {
        this.call = call;
        this.state = state;
    }

    public HOPCall getCall() {
        return call;
    }

    public CallStates getState() {
        return state;
    }
}
