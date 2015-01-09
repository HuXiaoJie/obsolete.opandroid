/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
#include "openpeer/core/ICache.h"
#include "openpeer/core/ILogger.h"
#include <android/log.h>

#include "globals.h"

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

	Time t;
	jni_env = getEnv();

	cls = findClass("android/text/format/Time");
	if(jni_env->IsInstanceOf(expires, cls) == JNI_TRUE)
	{
		jmethodID timeMethodID   = jni_env->GetMethodID(cls, "toMillis", "(Z)J");
		jlong longValue = jni_env->CallLongMethod(expires, timeMethodID, false);
		t = std::chrono::time_point<std::chrono::system_clock>(std::chrono::milliseconds(longValue));
	}

	String strString;
	strString = env->GetStringUTFChars(str, NULL);
	if (strString == NULL) {
		return;
	}

	ICache::store(cookieNamePathString, t, strString);
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
