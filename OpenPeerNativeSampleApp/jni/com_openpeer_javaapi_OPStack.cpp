#include "openpeer/core/IStack.h"
#include "openpeer/core/ILogger.h"

#include "OpenPeerCoreManager.h"
#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    singleton
 * Signature: ()Lcom/openpeer/javaapi/OPStack;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPStack_singleton
(JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack native singleton called");

	jni_env = getEnv();
	if(jni_env)
	{
		cls = findClass("com/openpeer/javaapi/OPStack");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		IStackPtr* ptrToStack = new std::shared_ptr<IStack>(IStack::singleton());
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong stack = (jlong) ptrToStack;
		jni_env->SetLongField(object, fid, stack);
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    setup
 * Signature: (Lcom/openpeer/javaapi/OPStackDelegate;Lcom/openpeer/javaapi/OPMediaEngineDelegate;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStack_setup
(JNIEnv *env, jobject owner,
		jobject javaStackDelegate,
		jobject javaMediaEngineDelegate)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack native setup called");

	jni_env = getEnv();
	jclass stackClass = findClass("com/openpeer/javaapi/OPStack");
	jfieldID stackFid = jni_env->GetFieldID(stackClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, stackFid);

	IStackPtr* coreStackPtr = (IStackPtr*)pointerValue;

	StackDelegateWrapperPtr stackDelegatePtr = StackDelegateWrapperPtr(new StackDelegateWrapper(javaStackDelegate));

	mediaEngineDelegatePtr = MediaEngineDelegateWrapperPtr(new MediaEngineDelegateWrapper(javaMediaEngineDelegate));

	if (coreStackPtr)
	{
		coreStackPtr->get()->setup(stackDelegatePtr, mediaEngineDelegatePtr);
		if (stackDelegatePtr)
		{
			StackDelegateWrapperPtr* ptrToStackDelegate = new std::shared_ptr<StackDelegateWrapper>(stackDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(stackClass, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToStackDelegate;
			jni_env->SetLongField(owner, delegateFid, delegate);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPStack native setup core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStack_shutdown
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack native shutdown called");

	jni_env = getEnv();
	jclass stackClass = findClass("com/openpeer/javaapi/OPStack");
	jfieldID stackFid = jni_env->GetFieldID(stackClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, stackFid);

	IStackPtr* coreStackPtr = (IStackPtr*)pointerValue;
	if (coreStackPtr)
	{
		coreStackPtr->get()->shutdown();
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPStack native shutdown core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    createAuthorizedApplicationID
 * Signature: (Ljava/lang/String;Ljava/lang/String;Landroid/text/format/Time;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPStack_createAuthorizedApplicationID
(JNIEnv *env, jclass, jstring applicationID, jstring applicationIDSharedSecret, jobject expires)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jstring authorizedApplicationID;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack native createAuthorizedApplicationID called");

	String applicationIDString;
	applicationIDString = env->GetStringUTFChars(applicationID, NULL);
	if (applicationIDString == NULL) {
		return authorizedApplicationID;
	}

	String applicationIDSharedSecretString;
	applicationIDSharedSecretString = env->GetStringUTFChars(applicationIDSharedSecret, NULL);
	if (applicationIDSharedSecretString == NULL) {
		return authorizedApplicationID;
	}

	Time t = OpenPeerCoreManager::convertTimeFromJava(expires);

	authorizedApplicationID =  env->NewStringUTF(IStack::createAuthorizedApplicationID(applicationIDString, applicationIDSharedSecretString, t).c_str());

	return authorizedApplicationID;
}

/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    getAuthorizedApplicationIDExpiry
 * Signature: (Ljava/lang/String;Landroid/text/format/Time;)Landroid/text/format/Time;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPStack_getAuthorizedApplicationIDExpiry
(JNIEnv *env, jclass, jstring authorizedApplicationID, jobject ignoreThis)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack native getAuthorizedApplicationIDExpiry called");

	String authorizedApplicationIDString;
	authorizedApplicationIDString = env->GetStringUTFChars(authorizedApplicationID, NULL);
	if (authorizedApplicationIDString == NULL) {
		return object;
	}

	Time expiryTime = IStack::getAuthorizedApplicationIDExpiry(authorizedApplicationIDString);

	return OpenPeerCoreManager::convertTimeFromCore(expiryTime);
}

/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    isAuthorizedApplicationIDExpiryWindowStillValid
 * Signature: (Ljava/lang/String;Landroid/text/format/Time;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPStack_isAuthorizedApplicationIDExpiryWindowStillValid
(JNIEnv *env, jclass, jstring authorizedApplicationID, jobject ignoreThis)
{
	jclass cls;
	jmethodID method;
	jobject object;
	jboolean ret;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack native isAuthorizedApplicationIDExpiryWindowStillValid called");

	String authorizedApplicationIDString;
	authorizedApplicationIDString = env->GetStringUTFChars(authorizedApplicationID, NULL);
	if (authorizedApplicationIDString == NULL) {
		return ret;
	}

	ret = IStack::isAuthorizedApplicationIDExpiryWindowStillValid(authorizedApplicationIDString, Seconds(1));
}

/*
 * Class:     com_openpeer_javaapi_OPStack
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStack_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPStack");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IStackPtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (StackDelegateWrapperPtr*)delegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStack Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPStack Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
