package com.openpeer.sample.events;

import de.greenrobot.event.EventBus;

/**
 * Created by brucexia on 2015-02-05.
 */
public class BaseEvent {
    public void post(){
        EventBus.getDefault().post(this);
    }
}
