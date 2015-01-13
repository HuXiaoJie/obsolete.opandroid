#include "com_openpeer_javaapi_OPPresenceTimeZoneLocation.h"
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
 * Class:     com_openpeer_javaapi_OPPresenceTimeZoneLocation
 * Method:    create
 * Signature: ()Lcom/openpeer/javaapi/OPPresenceTimeZoneLocation;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceTimeZoneLocation_create
  (JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	PresenceTimeZoneLocationPtr presenceLocationPtr;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native create called");

	jni_env = getEnv();

	presenceLocationPtr = PresenceTimeZoneLocationPtr(new PresenceTimeZoneLocation());

	if(presenceLocationPtr)
	{
		object = OpenPeerCoreManager::presenceTimeZoneLocationToJava(presenceLocationPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPresenceTimeZoneLocation native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceTimeZoneLocation
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPPresenceTimeZoneLocation;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceTimeZoneLocation_extract
(JNIEnv *, jclass, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native extract called");

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPElement");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(dataEl, fid);

	ElementPtr* coreElementPtr = (ElementPtr*)pointerValue;

	PresenceTimeZoneLocationPtr locationPtr = PresenceTimeZoneLocation::extract(*coreElementPtr);

	if (locationPtr)
	{
		object = OpenPeerCoreManager::presenceTimeZoneLocationToJava(locationPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceTimeZoneLocation
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceTimeZoneLocation_insert
(JNIEnv *, jobject owner, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPPresenceTimeZoneLocation");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceTimeZoneLocationPtr* corePresenceLocationPtr = (PresenceTimeZoneLocationPtr*)pointerValue;
	if (corePresenceLocationPtr)
	{
		corePresenceLocationPtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceTimeZoneLocation
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPPresenceTimeZoneLocation_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native hasData called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceTimeZoneLocation");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceTimeZoneLocationPtr* corePresenceLocationPtr = (PresenceTimeZoneLocationPtr*)pointerValue;
	if (corePresenceLocationPtr)
	{
		ret = (jboolean) corePresenceLocationPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceTimeZoneLocation
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceTimeZoneLocation_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceTimeZoneLocation");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceTimeZoneLocationPtr* corePresenceLocationPtr = (PresenceTimeZoneLocationPtr*)pointerValue;
	if (corePresenceLocationPtr)
	{
		ElementPtr coreEl = corePresenceLocationPtr->get()->toDebug();
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceTimeZoneLocation
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceTimeZoneLocation_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPPresenceTimeZoneLocation");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (PresenceTimeZoneLocationPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceTimeZoneLocation releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPPresenceTimeZoneLocation releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
