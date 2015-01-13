#include "com_openpeer_javaapi_OPPushPresenceStatus.h"
#include "openpeer/core/IHelper.h"
#include "openpeer/core/IPushPresence.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPPushPresenceStatus
 * Method:    createEmptyPresence
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresenceStatus_createEmptyPresence
  (JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresenceStatus native create called");

	jni_env = getEnv();

	ElementPtr statusEl = IPushPresence::Status::createEmptyPresence();
	if(statusEl)
	{
		ElementPtr* ptrToElement = new std::shared_ptr<Element>(statusEl);
		cls = findClass("com/openpeer/javaapi/OPElement");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong element = (jlong) ptrToElement;
		jni_env->SetLongField(object, fid, element);
	}

	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresenceStatus native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresenceStatus
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPPushPresenceStatus_hasData
  (JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresenceStatus native hasData called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPushPresenceStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	IPushPresence::StatusPtr* coreStatusPtr = (IPushPresence::StatusPtr*)pointerValue;
	if (coreStatusPtr)
	{
		ret = (jboolean) coreStatusPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresenceStatus native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresenceStatus
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresenceStatus_toDebug
  (JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresenceStatus native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPushPresenceStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	IPushPresence::StatusPtr* coreStatusPtr = (IPushPresence::StatusPtr*)pointerValue;
	if (coreStatusPtr)
	{
		ElementPtr coreEl = coreStatusPtr->get()->toDebug();
		ElementPtr* ptrToElement = new std::shared_ptr<Element>(coreEl);
		cls = findClass("com/openpeer/javaapi/OPElement");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong element = (jlong) ptrToElement;
		jni_env->SetLongField(object, fid, element);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresenceStatus native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresenceStatus
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushPresenceStatus_releaseCoreObjects
  (JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPPushPresenceStatus");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (UseTokenPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresenceStatus releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPComposingStatus releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
