package com.openpeer.delegates;

import android.util.Log;

import com.openpeer.javaapi.OPSettingsDelegate;

public class OPSettingsDelegateImplementation extends OPSettingsDelegate {

	@Override
	public String getString(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getString key = " + key);
		return null;
	}

	@Override
	public long getInt(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getInt key = " + key);
		return 0;
	}

	@Override
	public long getUInt(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getUInt key = " + key);
		return 0;
	}

	@Override
	public boolean getBool(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getBool key = " + key);
		return false;
	}

	@Override
	public float getFloat(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getFloat key = " + key);
		return 0;
	}

	@Override
	public double getDouble(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getDouble key = " + key);
		return 0;
	}

	@Override
	public void setString(String key, String value) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getString key = " + key + ", value = " + value);
	}

	@Override
	public void setInt(String key, long value) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation setInt key = " + key + ", value = " + value);
	}

	@Override
	public void setUInt(String key, long value) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation setUInt key = " + key + ", value = " + value);
	}

	@Override
	public void setBool(String key, boolean value) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation setBool key = " + key + ", value = " + value);
	}

	@Override
	public void setFloat(String key, float value) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation setFloat key = " + key + ", value = " + value);
	}

	@Override
	public void setDouble(String key, double value) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation setDouble key = " + key + ", value = " + value);
	}

	@Override
	public void clear(String key) {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation getString key = " + key);
	}
	
	@Override
	public void clearAll() {
		// TODO Auto-generated method stub
		Log.d("output","OPSettingsDelegateImplementation clearAll called");
	}

}
