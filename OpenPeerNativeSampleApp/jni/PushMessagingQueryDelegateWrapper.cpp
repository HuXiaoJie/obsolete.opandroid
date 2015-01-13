#include "PushMessagingQueryDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushMessagingQueryDelegateWrapper implementation
PushMessagingQueryDelegateWrapper::PushMessagingQueryDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushMessagingQueryDelegate implementation
void PushMessagingQueryDelegateWrapper::onPushMessagingQueryUploaded(IPushMessagingQueryPtr query)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingQueryUploaded called");

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

	if (javaDelegate != NULL)
	{
		//create new OPPushMessagingQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushMessagingQuery");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingQueryObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingQueryPtr* ptrToPushMessagingQuery = new std::shared_ptr<IPushMessagingQuery>(query);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingQueryObject, fid, (jlong)ptrToPushMessagingQuery);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingQueryUploaded", "(Lcom/openpeer/javaapi/OPPushMessagingQuery;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushMessagingQueryObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingQueryUploaded Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void PushMessagingQueryDelegateWrapper::onPushMessagingQueryPushStatesChanged(IPushMessagingQueryPtr query)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingQueryPushStatesChanged called");

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

	if (javaDelegate != NULL)
	{
		//create new OPPushMessagingQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushMessagingQuery");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingQueryObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingQueryPtr* ptrToPushMessagingQuery = new std::shared_ptr<IPushMessagingQuery>(query);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingQueryObject, fid, (jlong)ptrToPushMessagingQuery);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingQueryPushStatesChanged", "(Lcom/openpeer/javaapi/OPPushMessagingQuery;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushMessagingQueryObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingQueryPushStatesChanged Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushMessagingQueryDelegateWrapper::~PushMessagingQueryDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
