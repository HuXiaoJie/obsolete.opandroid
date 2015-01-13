#include "CallDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//ICallDelegate implementation
CallDelegateWrapper::CallDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

void CallDelegateWrapper::onCallStateChanged(ICallPtr call, ICall::CallStates state)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onCallStateChanged called state = %d", (jint)state);

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

	if (javaDelegate != NULL){

		//create new OPCall java object
		cls = findClass("com/openpeer/javaapi/OPCall");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject callObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		ICallPtr* ptrToCall = new std::shared_ptr<ICall>(call);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(callObject, fid, (jlong)ptrToCall);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onCallStateChanged", "(Lcom/openpeer/javaapi/OPCall;Lcom/openpeer/javaapi/CallStates;)V");
		jni_env->CallVoidMethod(javaDelegate, method,callObject, OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/CallStates", (jint) state));
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onCallStateChanged Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

CallDelegateWrapper::~CallDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
