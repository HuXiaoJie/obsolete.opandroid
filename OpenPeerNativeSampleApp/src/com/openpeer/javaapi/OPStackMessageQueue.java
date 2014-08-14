package com.openpeer.javaapi;

import android.util.Log;

public class OPStackMessageQueue {

	private long nativeClassPointer;
	
	private long nativeDelegatePointer;
	
	public static native OPStackMessageQueue singleton();

    //-----------------------------------------------------------------------
    // PURPOSE: Intercept the processing of event messages from within the
    //          default message queue so they can be processed/executed from
    //          within the context of a custom thread.
    // NOTE:    Can only be called once. Once override, everytime
    //          "IStackMessageQueueDelegate::onStackMessageQueueWakeUpCustomThreadAndProcess"
    //          is called on the delegate the delegate must wake up the main
    //          thread then call "IStackMessageQueue::notifyProcessMessageFromCustomThread"
    //          from the main thread.
    //
    //          MUST be called BEFORE called "IStack::setup"
    public native void interceptProcessing(OPStackMessageQueueDelegate delegate);

    //-----------------------------------------------------------------------
    // PURPOSE: Notify that a message can be processed from the custom thread.
    // NOTE:    Only call this routine from within the context of running from
    //          the custom thread.
    public native void notifyProcessMessageFromCustomThread();
    
    private native void releaseCoreObjects(); 
    
    protected void finalize() throws Throwable {
    	
    	if (nativeClassPointer != 0 || nativeDelegatePointer != 0)
    	{
    		Log.d("output", "Cleaning stack message queue core objects");
    		releaseCoreObjects();
    	}
    		
    	super.finalize();
    }
}
