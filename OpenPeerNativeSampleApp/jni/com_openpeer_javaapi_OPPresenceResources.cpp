#include "com_openpeer_javaapi_OPPresenceResources.h"
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
 * Class:     com_openpeer_javaapi_OPPresenceResources
 * Method:    create
 * Signature: ()Lcom/openpeer/javaapi/OPPresenceResources;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceResources_create
  (JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	PresenceResourcesPtr presenceResourcesPtr;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native create called");

	jni_env = getEnv();

	presenceResourcesPtr = PresenceResourcesPtr(new PresenceResources());

	if(presenceResourcesPtr)
	{
		object = OpenPeerCoreManager::presenceResourcesToJava(presenceResourcesPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPresenceResources native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceResources
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPPresenceResources;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceResources_extract
(JNIEnv *, jclass, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native extract called");

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPElement");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(dataEl, fid);

	ElementPtr* coreElementPtr = (ElementPtr*)pointerValue;

	PresenceResourcesPtr resourcesPtr = PresenceResources::extract(*coreElementPtr);

	if (resourcesPtr)
	{
		object = OpenPeerCoreManager::presenceResourcesToJava(resourcesPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceResources
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceResources_insert
(JNIEnv *, jobject owner, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPPresenceResources");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceResourcesPtr* corePresenceResourcesPtr = (PresenceResourcesPtr*)pointerValue;
	if (corePresenceResourcesPtr)
	{
		corePresenceResourcesPtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceResources
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPPresenceResources_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native hasData called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceResources");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceResourcesPtr* corePresenceResourcesPtr = (PresenceResourcesPtr*)pointerValue;
	if (corePresenceResourcesPtr)
	{
		ret = (jboolean) corePresenceResourcesPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceResources
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPresenceResources_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceResources");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	PresenceResourcesPtr* corePresenceResourcesPtr = (PresenceResourcesPtr*)pointerValue;
	if (corePresenceResourcesPtr)
	{
		ElementPtr coreEl = corePresenceResourcesPtr->get()->toDebug();
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPresenceResources
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPresenceResources_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPPresenceResources");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (PresenceResourcesPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPresenceResources releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPPresenceResources releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
