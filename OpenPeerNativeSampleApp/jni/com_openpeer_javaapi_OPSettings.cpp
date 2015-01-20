#include "openpeer/core/ISettings.h"
#include "openpeer/core/ILogger.h"
#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setup
 * Signature: (Lcom/openpeer/javaapi/OPSettingsDelegate;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setup
(JNIEnv *, jclass, jobject javaSettingsDelegate)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setup called");
	settingsDelegatePtr = SettingsDelegateWrapperPtr(new SettingsDelegateWrapper(javaSettingsDelegate));
	ISettings::setup(settingsDelegatePtr);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setString
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setString
(JNIEnv *env , jclass, jstring key, jstring value)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setString called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}

	const char *valueStr;
	valueStr = env->GetStringUTFChars(value, NULL);
	if (valueStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native setString key = %s, value = %s", keyStr, valueStr);
	ISettings::setString(keyStr, valueStr);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setInt
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setInt
(JNIEnv *env, jclass, jstring key, jlong value)
{

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setInt called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native setInt key = %s, value = %d", keyStr, value);
	ISettings::setInt(keyStr, value);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setUInt
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setUInt
(JNIEnv *env, jclass, jstring key, jlong value)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setUInt called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native setUInt key = %s, value = %u", keyStr, value);
	ISettings::setUInt(keyStr, value);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setBool
 * Signature: (Ljava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setBool
(JNIEnv *env, jclass, jstring key, jboolean value)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setBool called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native setBool key = %s, value = %d", keyStr, value);
	ISettings::setBool(keyStr, value);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setFloat
 * Signature: (Ljava/lang/String;F)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setFloat
(JNIEnv *env, jclass, jstring key, jfloat value)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setFloat called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native setFloat key = %s, value = %f", keyStr, value);
	ISettings::setFloat(keyStr, value);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    setDouble
 * Signature: (Ljava/lang/String;D)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_setDouble
(JNIEnv *env, jclass, jstring key, jdouble value)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native setDouble called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native setDouble key = %s, value = %g", keyStr, value);
	ISettings::setDouble(keyStr, value);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    clear
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_clear
(JNIEnv *env, jclass, jstring key)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native clear called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native clear key = %s", keyStr);
	ISettings::clear(keyStr);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    apply
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPSettings_apply
(JNIEnv *env, jclass, jstring jsonSettings)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native apply called");

	const char *jsonSettingsStr;
	jsonSettingsStr = env->GetStringUTFChars(jsonSettings, NULL);
	if (jsonSettingsStr == NULL) {
		return false;
	}

	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native apply jsonSettings = %s", jsonSettingsStr);

	return ISettings::apply(jsonSettingsStr);
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    applyDefaults
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_applyDefaults
(JNIEnv *, jclass)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native applyDefaults called");
	ISettings::applyDefaults();
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    clearAll
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_clearAll
(JNIEnv *, jclass)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native clearAll called");
	ISettings::clearAll();
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    verifySettingExists
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_verifySettingExists
(JNIEnv *env, jclass, jstring key)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native verifySettingExists called");

	const char *keyStr;
	keyStr = env->GetStringUTFChars(key, NULL);
	if (keyStr == NULL) {
		return;
	}
	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPSettings native verifySettingExists key = %s", keyStr);
	try
	{
		ISettings::verifySettingExists(keyStr);
	}
	catch (ISettings::InvalidUsage e)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native verifySettingExists Invalid Usage exception thrown");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPSettings
 * Method:    verifyRequiredSettings
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPSettings_verifyRequiredSettings
(JNIEnv *, jclass)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native verifyRequiredSettings called");
	try
	{
		ISettings::verifyRequiredSettings();
	}
	catch (ISettings::InvalidUsage e)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSettings native verifyRequiredSettings Invalid Usage exception thrown");
	}
}

#ifdef __cplusplus
}
#endif
