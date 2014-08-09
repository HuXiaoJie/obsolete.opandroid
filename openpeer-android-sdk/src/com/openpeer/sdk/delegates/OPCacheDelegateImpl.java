package com.openpeer.sdk.delegates;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;

import com.openpeer.javaapi.OPCacheDelegate;

public class OPCacheDelegateImpl extends OPCacheDelegate {

	//TODO: Considering the amount of cache to be stored I would prefer using diskbased lru cache
	private static final String PREF_CACHE_NAME = "core_cache";
	private static OPCacheDelegateImpl instance;

	private OPCacheDelegateImpl() {
	}

	private Context mContext;

	public static OPCacheDelegateImpl getInstance(Context context) {
		if (instance == null) {
			instance = new OPCacheDelegateImpl();
			instance.mContext = context;
			instance.applyConfig();
		}
		return instance;
	}
	// TODO: Read configurations from propery file.. This function may need to be made public if we
	// want to do a global configuration file later.
	private void applyConfig(){
		
	}

	private SharedPreferences getPreference() {
		return mContext.getSharedPreferences(PREF_CACHE_NAME,
				Context.MODE_PRIVATE);
	}

	@Override
	public synchronized String fetch(String cookieNamePath) {
		// TODO connect with shared preferences

		return getPreference().getString(cookieNamePath, "");
	}

	@Override
	public synchronized void store(String cookieNamePath, Time expires, String str) {
//		Log.d("OPCacheDelegateImplementation", "cookieNamePath "
//				+ cookieNamePath + " expires " + expires + " value " + str);
		SharedPreferences.Editor editor = getPreference().edit();
		editor.putString(cookieNamePath, str);
		editor.apply();

	}

	@Override
	public synchronized void clear(String cookieNamePath) {
		// TODO connect with shared preferences
		SharedPreferences.Editor editor = getPreference().edit();
		editor.remove(cookieNamePath);
		editor.apply();
	}
	

}
