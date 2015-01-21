package com.openpeer.javaapi;

import java.util.List;

import android.text.format.Time;
import android.util.Log;

public class OPRegisterDeviceInfo {
	
//	private long nativeClassPointer;
//	private boolean isPushMessaging; 	//flag to indicate if the core pointer is to IPushMessaging or IPushPresence core interface
	
    private String mDeviceToken;        // a token used for pushing to this particular service
    private Time mExpires;              // how long should the subscription for push messaging last; pass in Time() to remove a previous subscription
    private String mMappedType;         // for APNS maps to "loc-key"
    private boolean mUnreadBadge;       // true causes total unread messages to be displayed in badge
    private String mSound;              // what sound to play upon receiving a message. For APNS, maps to "sound" field
    private String mAction;             // for APNS, maps to "action-loc-key"
    private String mLaunchImage;        // for APNS, maps to "launch-image"
    private int mPriority;          	// for APNS, maps to push priority
    private List<String> mValueNames;   // list of values requested from each push from the push server (in order they should be delivered); empty = all values

//    public native boolean hasData();
//    public native OPElement toDebug();
    
	public String getDeviceToken() {
		return mDeviceToken;
	}
	public void setDeviceToken(String mDeviceToken) {
		this.mDeviceToken = mDeviceToken;
	}
	public Time getExpires() {
		return mExpires;
	}
	public void setExpires(Time mExpires) {
		this.mExpires = mExpires;
	}
	public String getMappedType() {
		return mMappedType;
	}
	public void setMappedType(String mMappedType) {
		this.mMappedType = mMappedType;
	}
	public boolean isUnreadBadge() {
		return mUnreadBadge;
	}
	public void setUnreadBadge(boolean mUnreadBadge) {
		this.mUnreadBadge = mUnreadBadge;
	}
	public String getSound() {
		return mSound;
	}
	public void setSound(String mSound) {
		this.mSound = mSound;
	}
	public String getAction() {
		return mAction;
	}
	public void setAction(String mAction) {
		this.mAction = mAction;
	}
	public String getLaunchImage() {
		return mLaunchImage;
	}
	public void setLaunchImage(String mLaunchImage) {
		this.mLaunchImage = mLaunchImage;
	}
	public int getPriority() {
		return mPriority;
	}
	public void setPriority(int mPriority) {
		this.mPriority = mPriority;
	}
	public List<String> getValueNames() {
		return mValueNames;
	}
	public void setValueNames(List<String> mValueNames) {
		this.mValueNames = mValueNames;
	}
	
//    private native void releaseCoreObjects(); 
//    
//    protected void finalize() throws Throwable {
//    	
//    	if (nativeClassPointer != 0)
//    	{
//    		Log.d("output", "Cleaning Register device info core objects");
//    		releaseCoreObjects();
//    	}
//    		
//    	super.finalize();
//    }
}
