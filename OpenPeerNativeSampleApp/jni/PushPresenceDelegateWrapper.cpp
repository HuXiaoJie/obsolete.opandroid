#include "PushPresenceDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushPresenceDelegateWrapper implementation
PushPresenceDelegateWrapper::PushPresenceDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushPresenceDelegate implementation
void PushPresenceDelegateWrapper::onPushPresenceStateChanged(IPushPresencePtr presence, PushPresenceStates state)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushPresenceStateChanged called");

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
		//create new OPPushPresenceQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushPresence");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresencePtr* ptrToPushPresence = new std::shared_ptr<IPushPresence>(presence);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceObject, fid, (jlong)ptrToPushPresence);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushPresenceStateChanged", "(Lcom/openpeer/javaapi/OPPushPresence;Lcom/openpeer/javaapi/PushPresenceStates;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushPresenceObject, OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/PushPresenceStates", (jint) state));
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushPresenceStateChanged Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void PushPresenceDelegateWrapper::onPushPresenceNewStatus(IPushPresencePtr presence,StatusPtr status)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushPresenceNewStatus called");

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
		//create new OPPushPresence java object
		cls = findClass("com/openpeer/javaapi/OPPushPresence");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresencePtr* ptrToPushPresence = new std::shared_ptr<IPushPresence>(presence);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceObject, fid, (jlong)ptrToPushPresence);

		jclass statusClass = findClass("com/openpeer/javaapi/OPPushPresenceStatus");
		jmethodID statusMethod = jni_env->GetMethodID(statusClass, "<init>", "()V");
		jobject pushPresenceStatusObject = jni_env->NewObject(statusClass, statusMethod);

		//fill new field with pointer to core pointer
		IPushPresence::StatusPtr* ptrToPushPresenceStatus = new std::shared_ptr<IPushPresence::Status>(status);
		jfieldID statusFid = jni_env->GetFieldID(statusClass, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceStatusObject, statusFid, (jlong)ptrToPushPresenceStatus);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushPresenceNewStatus", "(Lcom/openpeer/javaapi/OPPushPresence;Lcom/openpeer/javaapi/OPPushPresenceStatus;)V");
		jni_env->CallVoidMethod(javaDelegate, method, pushPresenceObject, pushPresenceStatusObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushPresenceNewStatus Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushPresenceDelegateWrapper::~PushPresenceDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
