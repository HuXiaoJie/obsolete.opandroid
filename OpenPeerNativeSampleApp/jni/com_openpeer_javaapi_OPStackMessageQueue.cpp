#include "com_openpeer_javaapi_OPStackMessageQueue.h"
#include "openpeer/core/IStack.h"
#include "openpeer/core/ILogger.h"
#include <android/log.h>


#include "globals.h"
//#define NULL ((void*) 0)


using namespace openpeer::core;
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_singleton
(JNIEnv *env, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue native singleton called");

	jni_env = getEnv();
	if(jni_env)
	{
		cls = findClass("com/openpeer/javaapi/OPStackMessageQueue");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		IStackMessageQueuePtr* ptrToStackMessageQueue = new std::shared_ptr<IStackMessageQueue>(IStackMessageQueue::singleton());
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong stackMessageQueue = (jlong) ptrToStackMessageQueue;
		jni_env->SetLongField(object, fid, stackMessageQueue);

	}
	return object;

}

JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_interceptProcessing
(JNIEnv *, jobject owner, jobject javaStackMessageQueueDelegate)
{

	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue native interceptProcessing called");

	jni_env = getEnv();
	jclass stackMessageQueueClass = findClass("com/openpeer/javaapi/OPStackMessageQueue");
	jfieldID stackMessageQueueFid = jni_env->GetFieldID(stackMessageQueueClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, stackMessageQueueFid);

	IStackMessageQueuePtr* coreStackMesssageQueuePtr = (IStackMessageQueuePtr*)pointerValue;

	StackMessageQueueDelegateWrapperPtr stackMessageQueueDelegatePtr = StackMessageQueueDelegateWrapperPtr(new StackMessageQueueDelegateWrapper(javaStackMessageQueueDelegate));
	if (coreStackMesssageQueuePtr)
	{
		coreStackMesssageQueuePtr->get()->interceptProcessing(stackMessageQueueDelegatePtr);
		if (stackMessageQueueDelegatePtr)
		{
			StackMessageQueueDelegateWrapperPtr* ptrToStackMessageQueueDelegate = new std::shared_ptr<StackMessageQueueDelegateWrapper>(stackMessageQueueDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(stackMessageQueueClass, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToStackMessageQueueDelegate;
			jni_env->SetLongField(owner, delegateFid, delegate);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPStackMessageQueue native interceptProcessing core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPStackMessageQueue
 * Method:    notifyProcessMessageFromCustomThread
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_notifyProcessMessageFromCustomThread
(JNIEnv *, jobject)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue native notifyProcessMessageFromCustomThread called");
	IStackMessageQueue::singleton()->notifyProcessMessageFromCustomThread();
}

/*
 * Class:     com_openpeer_javaapi_OPStackMessageQueue
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPStackMessageQueue");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IStackMessageQueuePtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (StackMessageQueueDelegateWrapperPtr*)delegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPStackMessageQueue Core object not deleted - already NULL!");
	}
}
#ifdef __cplusplus
}
#endif
