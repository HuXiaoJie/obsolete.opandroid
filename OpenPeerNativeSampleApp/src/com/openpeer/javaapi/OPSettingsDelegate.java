package com.openpeer.javaapi;

public abstract class OPSettingsDelegate {

	public abstract String getString(String key);
	public abstract long getInt(String key);
	//TODO: Implement unsigned logic
	//http://jessicarbrown.com/resources/unsignedtojava.html
	public abstract long getUInt(String key);

	public abstract boolean getBool(String key);
	public abstract float getFloat(String key);
	public abstract double getDouble(String key);

	public abstract void setString(
			String key,
			String value
			);
	public abstract void setInt(
			String key,
			long value
			);
	//TODO: Implement unsigned logic
	//http://jessicarbrown.com/resources/unsignedtojava.html
	public abstract void setUInt(
			String key,
			long value
			);
	public abstract void setBool(
			String key,
			boolean value
			);
	public abstract void setFloat(
			String key,
			float value
			);
	public abstract void setDouble(
			String key,
			double value
			);

	public abstract void clear(String key);
	
	public abstract void clearAll();
}
