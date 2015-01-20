package com.openpeer.javaapi;

public class OPSettings {

	private long nativeClassPointer;
	
	public static native void setup(OPSettingsDelegate delegate);

	public static native void setString(
			String key,
			String value
			);
	public static native void setInt(
			String key,
			long value
			);
	public static native void setUInt(
			String key,
			long value
			);
	public static native void setBool(
			String key,
			boolean value
			);
	public static native void setFloat(
			String key,
			float value
			);
	public static native void setDouble(
			String key,
			double value
			);
	public static native void clear(String key);
	public static native boolean apply(String jsonSettings);
	public static native void applyDefaults();
	
	public static native void clearAll();

	public static native void verifySettingExists(String key);

	public static native void verifyRequiredSettings();
}
