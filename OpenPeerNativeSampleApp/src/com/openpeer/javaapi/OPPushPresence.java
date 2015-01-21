package com.openpeer.javaapi;

import java.util.List;
import java.util.Map;

import android.text.format.Time;
import android.util.Log;

public class OPPushPresence {

	private long nativeClassPointer;

	private long nativeDelegatePointer;

	private long nativeDatabaseAbstractionDelegatePointer;
	//-----------------------------------------------------------------------
	// PURPOSE: create a connection to the push presence service
	public static native OPPushPresence create(
			OPPushPresenceDelegate delegate,
			OPPushPresenceDatabaseAbstractionDelegate databaseDelegate,
			OPAccount account
			);

	//-----------------------------------------------------------------------
	// PURPOSE: get the push presence object instance ID
	public native long getID();

	//-----------------------------------------------------------------------
	// PURPOSE: get the current state of the push presence service
	public native PushPresenceStates getState(
			int outErrorCode,
			String outErrorReason
			);

	//-----------------------------------------------------------------------
	// PURPOSE: shutdown the connection to the push presence service
	public native void shutdown();

	//-----------------------------------------------------------------------
	// PURPOSE: register or unregister for push presence status updates
	public native OPPushPresenceRegisterQuery registerDevice(
			OPPushPresenceRegisterQueryDelegate inDelegate,
			OPRegisterDeviceInfo deviceInfo
			);

	//-----------------------------------------------------------------------
	// PURPOSE: send a status message over the network
	public native void send(
			List<OPContact> toContactList,
			OPPushPresenceStatus status
			);

	//-----------------------------------------------------------------------
	// PURPOSE: cause a check to refresh data contained within the server
	public native void recheckNow();

	//-----------------------------------------------------------------------
	// PURPOSE: extract a list of name / value pairs contained within
	//          a push info structure
	// RETURNS: a pointer to the name value map
	public static native Map<String, String> getValues(OPPushPresencePushInfo pushInfo);

	//-----------------------------------------------------------------------
	// PURPOSE: create a JSON blob compatible with the PushInfo.mValues
	//          based on a collection of name / value pairs.
	// RETURNS: a pointer to the values blob or null ElementPtr() if no
	//          values were found.
	public static native OPElement createValues(Map<String, String> values);

	private native void releaseCoreObjects(); 

	protected void finalize() throws Throwable {

		if (nativeClassPointer != 0 || nativeDelegatePointer != 0 || nativeDatabaseAbstractionDelegatePointer != 0)
		{
			Log.d("output", "Cleaning push presence core objects");
			releaseCoreObjects();
		}

		super.finalize();

	}
}
