#include "PushMessagingDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushMessagingDelegateWrapper implementation
PushMessagingDelegateWrapper::PushMessagingDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushMessagingDelegate implementation
void PushMessagingDelegateWrapper::onPushMessagingStateChanged(IPushMessagingPtr messaging, PushMessagingStates state)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingStateChanged called");

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
		cls = findClass("com/openpeer/javaapi/OPPushMessaging");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingPtr* ptrToPushMessaging = new std::shared_ptr<IPushMessaging>(messaging);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingObject, fid, (jlong)ptrToPushMessaging);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingStateChanged", "(Lcom/openpeer/javaapi/OPPushMessaging;Lcom/openpeer/javaapi/PushMessagingStates;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushMessagingObject, OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/PushMessagingStates", (jint) state));
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingStateChanged Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void PushMessagingDelegateWrapper::onPushMessagingNewMessages(IPushMessagingPtr messaging)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingNewMessages called");

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
		//create new OPPushMessaging java object
		cls = findClass("com/openpeer/javaapi/OPPushMessaging");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingPtr* ptrToPushMessaging = new std::shared_ptr<IPushMessaging>(messaging);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingObject, fid, (jlong)ptrToPushMessaging);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingNewMessages", "(Lcom/openpeer/javaapi/OPPushMessaging;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushMessagingObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingNewMessages Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushMessagingDelegateWrapper::~PushMessagingDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
