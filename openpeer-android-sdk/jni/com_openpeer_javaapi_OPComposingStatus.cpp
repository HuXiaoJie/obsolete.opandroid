#include "com_openpeer_javaapi_OPComposingStatus.h"
#include "openpeer/core/ComposingStatus.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

const char * CLASS_NAME = "com/openpeer/javaapi/OPComposingStatus";
/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/ComposingStates;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPComposingStatus_toString
(JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    toComposingState
 * Signature: (Ljava/lang/String;)Lcom/openpeer/javaapi/ComposingStates;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPComposingStatus_toComposingState
(JNIEnv *, jclass, jstring)
{

}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    create
 * Signature: (Lcom/openpeer/javaapi/ComposingStates;)Lcom/openpeer/javaapi/OPComposingStatus;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPComposingStatus_create
  (JNIEnv *, jclass, jobject state)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	ComposingStatusPtr composingStatusPtr;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native create called");

	if (state == NULL)
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPComposingStatus native create - invalid parameters");
		return object;
	}

	jni_env = getEnv();

	composingStatusPtr = ComposingStatusPtr(
					new ComposingStatus(
							(ComposingStatus::ComposingStates)OpenPeerCoreManager::getIntValueFromEnumObject(state, "com/openpeer/javaapi/ComposingStates")));

	if(composingStatusPtr)
	{
		ComposingStatusPtr* ptrToComposingStatus = new std::shared_ptr<ComposingStatus>(composingStatusPtr);
		cls = findClass("com/openpeer/javaapi/OPComposingStatus");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong composingStatus = (jlong) ptrToComposingStatus;
		jni_env->SetLongField(object, fid, composingStatus);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPComposingStatus native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPComposingStatus;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPComposingStatus_extract
(JNIEnv *, jclass, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native extract called");

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPElement");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(dataEl, fid);

	ElementPtr* coreElementPtr = (ElementPtr*)pointerValue;

	ComposingStatusPtr statusPtr = ComposingStatus::extract(*coreElementPtr);

	if (statusPtr)
	{
		cls = findClass("com/openpeer/javaapi/OPComposingStatus");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		ComposingStatusPtr* ptrToComposingStatus = new std::shared_ptr<ComposingStatus>(statusPtr);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong status = (jlong) ptrToComposingStatus;
		jni_env->SetLongField(object, fid, status);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPComposingStatus_insert
(JNIEnv *, jobject owner, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPComposingStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ComposingStatusPtr* coreComposingStatusPtr = (ComposingStatusPtr*)pointerValue;
	if (coreComposingStatusPtr)
	{
		coreComposingStatusPtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPComposingStatus_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native insert called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPComposingStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ComposingStatusPtr* coreComposingStatusPtr = (ComposingStatusPtr*)pointerValue;
	if (coreComposingStatusPtr)
	{
		ret = (jboolean) coreComposingStatusPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPComposingStatus_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPComposingStatus");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ComposingStatusPtr* coreComposingStatusPtr = (ComposingStatusPtr*)pointerValue;
	if (coreComposingStatusPtr)
	{
		ElementPtr coreEl = coreComposingStatusPtr->get()->toDebug();
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPComposingStatus_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPComposingStatus");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (ComposingStatusPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPComposingStatus releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPComposingStatus releaseCoreObjects Core object not deleted - already NULL!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPComposingStatus
 * Method:    getComposingState
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPComposingStatus;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPComposingStatus_getComposingState
(JNIEnv *, jobject owner) {
  JNIEnv *jni_env = getEnv();

  jclass cls = findClass(CLASS_NAME);
  jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
  jlong pointerValue = jni_env->GetLongField(owner, fid);

  ComposingStatusPtr* coreComposingStatusPtr =
      (ComposingStatusPtr*) pointerValue;
  jint state = (jint) coreComposingStatusPtr->get()->mComposingStatus;
  jobject object = OpenPeerCoreManager::getJavaEnumObject(
      "com/openpeer/javaapi/ComposingStates", state);
  return object;
}
#ifdef __cplusplus
}
#endif
