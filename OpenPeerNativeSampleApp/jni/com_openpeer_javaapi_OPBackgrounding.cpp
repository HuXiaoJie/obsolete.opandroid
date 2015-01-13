#include "openpeer/core/IBackgrounding.h"
#include "openpeer/core/ILogger.h"
#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;
using namespace zsLib;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPBackgrounding
 * Method:    toDebug
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPBackgrounding_toDebug
  (JNIEnv *, jclass)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPBackgrounding native toDebug called");
}

/*
 * Class:     com_openpeer_javaapi_OPBackgrounding
 * Method:    notifyGoingToBackground
 * Signature: (Lcom/openpeer/javaapi/OPBackgroundingCompletionDelegate;)Lcom/openpeer/javaapi/OPBackgroundingQuery;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPBackgrounding_notifyGoingToBackground
  (JNIEnv *, jclass, jobject readyDelegate)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPBackgrounding native notifyGoingToBackground called");
	if(readyDelegate == NULL)
	{
		IBackgrounding::notifyGoingToBackground();
	}
	else
	{
		backgroundingCompletionDelegatePtr = BackgroundingCompletionDelegateWrapperPtr(new BackgroundingCompletionDelegateWrapper(readyDelegate));
		IBackgrounding::notifyGoingToBackground(backgroundingCompletionDelegatePtr);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPBackgrounding
 * Method:    notifyGoingToBackgroundNow
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPBackgrounding_notifyGoingToBackgroundNow
  (JNIEnv *, jclass)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPBackgrounding native notifyGoingToBackgroundNow called");
	IBackgrounding::notifyGoingToBackgroundNow();
}

/*
 * Class:     com_openpeer_javaapi_OPBackgrounding
 * Method:    notifyReturningFromBackground
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPBackgrounding_notifyReturningFromBackground
  (JNIEnv *, jclass)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPBackgrounding native notifyReturningFromBackground called");
	IBackgrounding::notifyReturningFromBackground();
}

/*
 * Class:     com_openpeer_javaapi_OPBackgrounding
 * Method:    subscribe
 * Signature: (Lcom/openpeer/javaapi/OPBackgroundingDelegate;J)Lcom/openpeer/javaapi/OPBackgroundingSubscription;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPBackgrounding_subscribe
  (JNIEnv *, jclass, jobject delegate, jlong phase)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPBackgrounding native subscribe called");

	//ZS_THROW_INVALID_ARGUMENT_IF(phase < 0)
	if (delegate == NULL || phase < 0)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPBackgrounding native subscribe Invalid arguments");
		return object;
	}

	backgroundingDelegatePtr = BackgroundingDelegateWrapperPtr(new BackgroundingDelegateWrapper(delegate));
	IBackgroundingSubscriptionPtr subscriptionPtr = IBackgrounding::subscribe(backgroundingDelegatePtr, phase);

	if(subscriptionPtr)
	{
		jni_env = getEnv();
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPBackgroundingSubscription");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IBackgroundingSubscriptionPtr* ptrToSubscription = new std::shared_ptr<IBackgroundingSubscription>(subscriptionPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong subs = (jlong) ptrToSubscription;
			jni_env->SetLongField(object, fid, subs);
		}
	}
}

#ifdef __cplusplus
}
#endif
