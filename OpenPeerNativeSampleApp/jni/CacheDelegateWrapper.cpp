#include "globals.h"
#include "CacheDelegateWrapper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"
#include "openpeer/services/IHelper.h"

//ICacheDelegate implementation
CacheDelegateWrapper::CacheDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}
zsLib::String CacheDelegateWrapper::fetch(const char *cookieNamePath)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jstring cookieJavaString;
	const char *fetchedStr;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "Cache fetch called - cookieNamePath = %s", cookieNamePath);

	bool attached = false;
	switch (android_jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_6))
	{
	case JNI_OK:
		break;
	case JNI_EDETACHED:
		if (android_jvm->AttachCurrentThread(&jni_env, NULL)!=0)
		{
			throw std::runtime_error("Could not attach current thread");
		}
		attached = true;
		break;
	case JNI_EVERSION:
		throw std::runtime_error("Invalid java version");
	}

	cookieJavaString =  jni_env->NewStringUTF(cookieNamePath);

	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "fetch", "(Ljava/lang/String;)Ljava/lang/String;");
		object = jni_env->CallObjectMethod(javaDelegate, method, cookieJavaString);

		cls = findClass("java/lang/String");
		if(jni_env->IsInstanceOf(object, cls) == JNI_TRUE)
		{
			fetchedStr = jni_env->GetStringUTFChars((jstring)object, NULL);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "Cache fetch Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if (attached)
	{
		android_jvm->DetachCurrentThread();
	}
	return fetchedStr;
}
void CacheDelegateWrapper::store(const char *cookieNamePath,
		Time expires,
		const char *str)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jstring cookieJavaString;
	jstring storeStr;

	String expStr = openpeer::services::IHelper::timeToString(expires);

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "Cache store called - cookieNamePath = %s",cookieNamePath);

	bool attached = false;
	switch (android_jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_6))
	{
	case JNI_OK:
		break;
	case JNI_EDETACHED:
		if (android_jvm->AttachCurrentThread(&jni_env, NULL)!=0)
		{
			throw std::runtime_error("Could not attach current thread");
		}
		attached = true;
		break;
	case JNI_EVERSION:
		throw std::runtime_error("Invalid java version");
	}

	cookieJavaString =  jni_env->NewStringUTF(cookieNamePath);
	storeStr =  jni_env->NewStringUTF(str);

	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "store", "(Ljava/lang/String;Landroid/text/format/Time;Ljava/lang/String;)V");
		jni_env->CallVoidMethod(javaDelegate, method, cookieJavaString, OpenPeerCoreManager::convertTimeFromCore(expires), storeStr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "Cache store Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}
void CacheDelegateWrapper::clear(const char *cookieNamePath)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jstring cookieJavaString;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "Cache clear called - cookieNamePath = %s", cookieNamePath);

	bool attached = false;
	switch (android_jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_6))
	{
	case JNI_OK:
		break;
	case JNI_EDETACHED:
		if (android_jvm->AttachCurrentThread(&jni_env, NULL)!=0)
		{
			throw std::runtime_error("Could not attach current thread");
		}
		attached = true;
		break;
	case JNI_EVERSION:
		throw std::runtime_error("Invalid java version");
	}

	cookieJavaString = jni_env->NewStringUTF(cookieNamePath);
	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "clear", "(Ljava/lang/String;)V");
		jni_env->CallVoidMethod(javaDelegate, method, cookieJavaString);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "Cache clear Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

CacheDelegateWrapper::~CacheDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);
}
