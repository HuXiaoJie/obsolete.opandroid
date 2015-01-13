#include "PushMessagingRegisterQueryDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushMessagingRegisterQueryDelegateWrapper implementation
PushMessagingRegisterQueryDelegateWrapper::PushMessagingRegisterQueryDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushMessagingRegisterQueryDelegate implementation
void PushMessagingRegisterQueryDelegateWrapper::onPushMessagingRegisterQueryCompleted(IPushMessagingRegisterQueryPtr query)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingRegisterQueryCompleted called");

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
		//create new OPCall java object
		cls = findClass("com/openpeer/javaapi/OPPushMessagingRegisterQuery");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingRegisterQueryObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingRegisterQueryPtr* ptrToPushMessagingRegisterQuery = new std::shared_ptr<IPushMessagingRegisterQuery>(query);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingRegisterQueryObject, fid, (jlong)ptrToPushMessagingRegisterQuery);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingRegisterQueryCompleted", "(Lcom/openpeer/javaapi/OPPushMessagingRegisterQuery;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushMessagingRegisterQueryObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingRegisterQueryCompleted Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushMessagingRegisterQueryDelegateWrapper::~PushMessagingRegisterQueryDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
