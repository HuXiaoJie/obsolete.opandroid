#include "openpeer/core/ICache.h"
#include "openpeer/core/ILogger.h"
#include <android/log.h>

#include "globals.h"
#include "OpenPeerCoreManager.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    setup
 * Signature: (Lcom/openpeer/javaapi/OPCacheDelegate;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCache_setup
(JNIEnv *, jclass, jobject javaCacheDelegate)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCache native setup called");

	cacheDelegatePtr = CacheDelegateWrapperPtr(new CacheDelegateWrapper(javaCacheDelegate));
	ICache::setup(cacheDelegatePtr);
}

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    fetch
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCache_fetch
(JNIEnv *env, jobject, jstring cookieNamePath)
{
	jstring ret;
	String cookieNamePathString;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCache native fetch called");

	cookieNamePathString = env->GetStringUTFChars(cookieNamePath, NULL);
	if (cookieNamePathString == NULL) {
		return ret;
	}

	ret =  env->NewStringUTF(ICache::fetch(cookieNamePathString).c_str());
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    store
 * Signature: (Ljava/lang/String;Landroid/text/format/Time;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCache_store
(JNIEnv *env, jobject, jstring cookieNamePath, jobject expires, jstring str)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCache native store called");

	String cookieNamePathString;
	cookieNamePathString = env->GetStringUTFChars(cookieNamePath, NULL);
	if (cookieNamePathString == NULL) {
		return;
	}

	String strString;
	strString = env->GetStringUTFChars(str, NULL);
	if (strString == NULL) {
		return;
	}

	ICache::store(cookieNamePathString, OpenPeerCoreManager::convertTimeFromJava(expires), strString);
}

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    clear
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCache_clear
(JNIEnv *env, jobject, jstring cookieNamePath)
{
	String cookieNamePathString;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCache native clear called");

	cookieNamePathString = env->GetStringUTFChars(cookieNamePath, NULL);
	if (cookieNamePathString == NULL) {
		return;
	}

	ICache::clear(cookieNamePathString);
}
#ifdef __cplusplus
}
#endif
