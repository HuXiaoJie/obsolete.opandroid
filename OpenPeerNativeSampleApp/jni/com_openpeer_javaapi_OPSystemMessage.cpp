#include "com_openpeer_javaapi_OPSystemMessage.h"
#include "openpeer/core/ISystemMessage.h"
#include "openpeer/core/IHelper.h"
#include "OpenPeerCoreManager.h"
#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPSystemMessage
 * Method:    createEmptySystemMessage
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPSystemMessage_createEmptySystemMessage
  (JNIEnv *, jclass)
{
	JNIEnv *jni_env = 0;
	jclass cls;
	jmethodID method;
	jobject object;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSystemMessage native createEmptySystemMessage called");

	jni_env = getEnv();

	ElementPtr coreEl = ISystemMessage::createEmptySystemMessage();
	ElementPtr* ptrToElement = new std::shared_ptr<Element>(coreEl);
	cls = findClass("com/openpeer/javaapi/OPElement");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong element = (jlong) ptrToElement;
	jni_env->SetLongField(object, fid, element);

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPSystemMessage
 * Method:    getMessageType
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPSystemMessage_getMessageType
  (JNIEnv *, jclass)
{
	JNIEnv *jni_env = 0;
	jstring ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPSystemMessage native getMessageType called");

	jni_env = getEnv();

	String coreRetStr = String(ISystemMessage::getMessageType());
	ret = jni_env->NewStringUTF(coreRetStr.c_str());

	return ret;
}

#ifdef __cplusplus
}
#endif
