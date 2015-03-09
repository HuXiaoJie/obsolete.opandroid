#include "com_openpeer_javaapi_OPConversationThreadType.h"
#include "openpeer/core/IConversationThread.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/ConversationThreadTypes;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_toString
  (JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    toConversationThreadType
 * Signature: (Ljava/lang/String;)Lcom/openpeer/javaapi/ConversationThreadTypes;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_toConversationThreadType
  (JNIEnv *, jclass, jstring)
{

}

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPConversationThreadType;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_extract
(JNIEnv *, jclass, jobject metadataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	ConversationThreadTypePtr conversationThreadTypePtr;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native extract called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(metadataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

//	conversationThreadTypePtr = ConversationThreadType::extract(*coreElementPtr);



	if (conversationThreadTypePtr)
	{
		cls = findClass("com/openpeer/javaapi/OPConversationThreadType");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		ConversationThreadTypePtr* ptrToConversationThreadType = new boost::shared_ptr<ConversationThreadType>(conversationThreadTypePtr);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong convThreadType = (jlong) ptrToConversationThreadType;
		jni_env->SetLongField(object, fid, convThreadType);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_insert
(JNIEnv *, jobject owner, jobject metadataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(metadataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPConversationThreadType");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ConversationThreadTypePtr* conversationThreadTypePtr = (ConversationThreadTypePtr*)pointerValue;
	if (conversationThreadTypePtr)
	{
//		conversationThreadTypePtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native insert called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPConversationThreadType");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ConversationThreadTypePtr* conversationThreadTypePtr = (ConversationThreadTypePtr*)pointerValue;
	if (conversationThreadTypePtr)
	{
//		ret = (jboolean) conversationThreadTypePtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPConversationThreadType");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ConversationThreadTypePtr* conversationThreadTypePtr = (ConversationThreadTypePtr*)pointerValue;
	if (conversationThreadTypePtr)
	{
		ElementPtr coreEl;// = conversationThreadTypePtr->get()->toDebug();
		ElementPtr* ptrToElement = new boost::shared_ptr<Element>(coreEl);
		cls = findClass("com/openpeer/javaapi/OPElement");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong element = (jlong) ptrToElement;
		jni_env->SetLongField(object, fid, element);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPConversationThreadType
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPConversationThreadType_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPConversationThreadType");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (ConversationThreadTypePtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPConversationThreadType releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPConversationThreadType releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
