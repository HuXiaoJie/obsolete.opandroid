#include "com_openpeer_javaapi_OPCallSystemMessage.h"
#include "openpeer/core/ISystemMessage.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/CallSystemMessageTypes;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_toString
(JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    toCallSystemMessageType
 * Signature: (Ljava/lang/String;)Lcom/openpeer/javaapi/CallSystemMessageTypes;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_toCallSystemMessageType
(JNIEnv *, jclass, jstring)
{

}

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    create
 * Signature: (Lcom/openpeer/javaapi/CallSystemMessageTypes;Lcom/openpeer/javaapi/OPContact;I)Lcom/openpeer/javaapi/OPCallSystemMessage;
 */
// JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_create
//   (JNIEnv *, jclass, jobject type, jobject callee, jint errorCode)
// {
// 	jclass cls;
// 	jmethodID method;
// 	jobject object;
// 	JNIEnv *jni_env = 0;
// 	CallSystemMessagePtr systemMessagePtr;
// 	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native create called");

// 	if (type == NULL || callee == NULL)
// 	{
// 		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPCallSystemMessage native create - invalid parameters");
// 		return object;
// 	}

// 	jni_env = getEnv();

// 	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
// 	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
// 	jlong contactPointerValue = jni_env->GetLongField(callee, contactfid);

// 	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;

// 	if(contactPtr)
// 	{
// 		if(errorCode == 0)
// 		{
// 		systemMessagePtr = CallSystemMessagePtr(
// 				new CallSystemMessage(
// 						(CallSystemMessage::CallSystemMessageTypes)OpenPeerCoreManager::getIntValueFromEnumObject(type, "com/openpeer/javaapi/CallSystemMessageTypes"),
// 						*contactPtr));
// 		}
// 		else
// 		{
// 			systemMessagePtr = CallSystemMessagePtr(
// 							new CallSystemMessage(
// 									(CallSystemMessage::CallSystemMessageTypes)OpenPeerCoreManager::getIntValueFromEnumObject(type, "com/openpeer/javaapi/CallSystemMessageTypes"),
// 									*contactPtr,
// 									(int) errorCode));
// 		}
// 	}

// 	if (systemMessagePtr)
// 	{
// 		CallSystemMessagePtr* ptrToSystemMessage = new boost::shared_ptr<CallSystemMessage>(systemMessagePtr);
// 		cls = findClass("com/openpeer/javaapi/OPCallSystemMessage");
// 		method = jni_env->GetMethodID(cls, "<init>", "()V");
// 		object = jni_env->NewObject(cls, method);

// 		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
// 		jlong systemMessage = (jlong) ptrToSystemMessage;
// 		jni_env->SetLongField(object, fid, systemMessage);
// 	}
// 	else
// 	{
// 		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPCallSystemMessage native create core pointer is NULL!!! ");
// 	}
// 	return object;
// }

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    extract
 * Signature: (Lcom/openpeer/javaapi/OPElement;Lcom/openpeer/javaapi/OPAccount;)Lcom/openpeer/javaapi/OPCallSystemMessage;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_extract
(JNIEnv *, jclass, jobject dataEl, jobject javaAccount)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	CallSystemMessagePtr systemMessagePtr;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native extract called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	if (javaAccount != NULL)
	{
		jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
		jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

		IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

		if(coreAccountPtr)
		{
			systemMessagePtr = CallSystemMessage::extract(*coreElementPtr, *coreAccountPtr);
		}
	}



	if (systemMessagePtr)
	{
		cls = findClass("com/openpeer/javaapi/OPCallSystemMessage");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		CallSystemMessagePtr* ptrToCallSystemMessage = new boost::shared_ptr<CallSystemMessage>(systemMessagePtr);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong systemMessage = (jlong) ptrToCallSystemMessage;
		jni_env->SetLongField(object, fid, systemMessage);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native extract core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    insert
 * Signature: (Lcom/openpeer/javaapi/OPElement;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_insert
(JNIEnv *, jobject owner, jobject dataEl)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native insert called");

	jni_env = getEnv();

	jclass elementClass = findClass("com/openpeer/javaapi/OPElement");
	jfieldID elementFID = jni_env->GetFieldID(elementClass, "nativeClassPointer", "J");
	jlong elementPointerValue = jni_env->GetLongField(dataEl, elementFID);

	ElementPtr* coreElementPtr = (ElementPtr*)elementPointerValue;

	cls = findClass("com/openpeer/javaapi/OPCallSystemMessage");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	CallSystemMessagePtr* coreCallSystemMessagePtr = (CallSystemMessagePtr*)pointerValue;
	if (coreCallSystemMessagePtr)
	{
		coreCallSystemMessagePtr->get()->insert(*coreElementPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native insert core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native insert called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPCallSystemMessage");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	CallSystemMessagePtr* coreCallSystemMessagePtr = (CallSystemMessagePtr*)pointerValue;
	if (coreCallSystemMessagePtr)
	{
		ret = (jboolean) coreCallSystemMessagePtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPCallSystemMessage");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	CallSystemMessagePtr* coreCallSystemMessagePtr = (CallSystemMessagePtr*)pointerValue;
	if (coreCallSystemMessagePtr)
	{
		ElementPtr coreEl = coreCallSystemMessagePtr->get()->toDebug();
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCallSystemMessage
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCallSystemMessage_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPCallSystemMessage");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (CallSystemMessagePtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPCallSystemMessage releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPCallSystemMessage releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
