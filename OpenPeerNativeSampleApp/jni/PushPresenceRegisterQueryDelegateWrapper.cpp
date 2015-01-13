#include "PushPresenceRegisterQueryDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushPresenceRegisterQueryDelegateWrapper implementation
PushPresenceRegisterQueryDelegateWrapper::PushPresenceRegisterQueryDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushPresenceRegisterQueryDelegate implementation
void PushPresenceRegisterQueryDelegateWrapper::onPushPresenceRegisterQueryCompleted(IPushPresenceRegisterQueryPtr query)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushPresenceRegisterQueryCompleted called");

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
		cls = findClass("com/openpeer/javaapi/OPPushPresenceRegisterQuery");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceRegisterQueryObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresenceRegisterQueryPtr* ptrToPushPresenceRegisterQuery = new std::shared_ptr<IPushPresenceRegisterQuery>(query);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceRegisterQueryObject, fid, (jlong)ptrToPushPresenceRegisterQuery);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushPresenceRegisterQueryCompleted", "(Lcom/openpeer/javaapi/OPPushPresenceRegisterQuery;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushPresenceRegisterQueryObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushPresenceRegisterQueryCompleted Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushPresenceRegisterQueryDelegateWrapper::~PushPresenceRegisterQueryDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
