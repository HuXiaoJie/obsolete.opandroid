#include "com_openpeer_javaapi_OPPresenceGeographicLocation.h"
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
 * Class:     com_openpeer_javaapi_OPPresenceGeographicLocation
 * Method:    create
 * Signature: ()Lcom/openpeer/javaapi/OPPresenceGeographicLocation;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceGeographicLocation_create
  (JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	PresenceGeographicLocationPtr presenceLocationPtr;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native create called");

	jni_env = getEnv();

	presenceLocationPtr = PresenceGeographicLocationPtr(new PresenceGeographicLocation());

	if(presenceLocationPtr)
	{
		object = OpenPeerCoreManager::presenceGeographicLocationToJava(presenceLocationPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPresenceGeographicLocation native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceGeographicLocation
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPPresenceGeographicLocation;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceGeographicLocation_extract
(JNIEnv *, jclass, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native extract called");

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPElement");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(dataEl, fid);

	ElementPtr* coreElementPtr = (ElementPtr*)pointerValue;

	PresenceGeographicLocationPtr locationPtr = PresenceGeographicLocation::extract(*coreElementPtr);

	if (locationPtr)
	{
		object = OpenPeerCoreManager::presenceGeographicLocationToJava(locationPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceGeographicLocation
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceGeographicLocation_insert
(JNIEnv *, jobject owner, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPPresenceGeographicLocation");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceGeographicLocationPtr* corePresenceLocationPtr = (PresenceGeographicLocationPtr*)pointerValue;
	if (corePresenceLocationPtr)
	{
		corePresenceLocationPtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceGeographicLocation
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPPresenceGeographicLocation_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native hasData called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceGeographicLocation");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceGeographicLocationPtr* corePresenceLocationPtr = (PresenceGeographicLocationPtr*)pointerValue;
	if (corePresenceLocationPtr)
	{
		ret = (jboolean) corePresenceLocationPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceGeographicLocation
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceGeographicLocation_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceGeographicLocation");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceGeographicLocationPtr* corePresenceLocationPtr = (PresenceGeographicLocationPtr*)pointerValue;
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceGeographicLocation
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceGeographicLocation_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPPresenceGeographicLocation");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (PresenceGeographicLocationPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceGeographicLocation releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPPresenceGeographicLocation releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
