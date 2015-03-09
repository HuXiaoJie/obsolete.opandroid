package com.openpeer.javaapi;

import android.util.Log;

public class OPConversationThreadType {

	private long nativeClassPointer;
	
	public static native String toString(ConversationThreadTypes type);
	public static native ConversationThreadTypes toConversationThreadType(String str);
	
    public static native OPConversationThreadType extract(OPElement conversationThreadMetaDataEl);
    public native void insert(OPElement conversationThreadMetaDataEl);
    
    public native boolean hasData();

    public native OPElement toDebug();
    
	private native void releaseCoreObjects(); 

	protected void finalize() throws Throwable {

		if (nativeClassPointer != 0)
		{
			Log.d("output", "Cleaning OPConversationThreadType core objects");
			releaseCoreObjects();
		}

		super.finalize();
	}
}
