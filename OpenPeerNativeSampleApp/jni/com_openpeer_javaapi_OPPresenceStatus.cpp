#include "com_openpeer_javaapi_OPPresenceStatus.h"
#include "openpeer/core/IPushPresence.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPPresenceStatus
 * Method:    create
 * Signature: ()Lcom/openpeer/javaapi/OPPresenceStatus;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceStatus_create
  (JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	PresenceStatusPtr presenceStatusPtr;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native create called");

	jni_env = getEnv();

	presenceStatusPtr = PresenceStatusPtr(new PresenceStatus());

	if(presenceStatusPtr)
	{
		object = OpenPeerCoreManager::presenceStatusToJava(presenceStatusPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPresenceStatus native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceStatus
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPPresenceStatus;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceStatus_extract
(JNIEnv *, jclass, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native extract called");

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPElement");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(dataEl, fid);

	ElementPtr* coreElementPtr = (ElementPtr*)pointerValue;

	PresenceStatusPtr statusPtr = PresenceStatus::extract(*coreElementPtr);

	if (statusPtr)
	{
		object = OpenPeerCoreManager::presenceStatusToJava(statusPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceStatus
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceStatus_insert
(JNIEnv *, jobject owner, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPPresenceStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceStatusPtr* corePresenceStatusPtr = (PresenceStatusPtr*)pointerValue;
	if (corePresenceStatusPtr)
	{
		corePresenceStatusPtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceStatus
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPPresenceStatus_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native hasData called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceStatusPtr* corePresenceStatusPtr = (PresenceStatusPtr*)pointerValue;
	if (corePresenceStatusPtr)
	{
		ret = (jboolean) corePresenceStatusPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceStatus
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceStatus_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceStatusPtr* corePresenceStatusPtr = (PresenceStatusPtr*)pointerValue;
	if (corePresenceStatusPtr)
	{
		ElementPtr coreEl = corePresenceStatusPtr->get()->toDebug();
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceStatus
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceStatus_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPPresenceStatus");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (PresenceStatusPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceStatus releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPPresenceStatus releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
