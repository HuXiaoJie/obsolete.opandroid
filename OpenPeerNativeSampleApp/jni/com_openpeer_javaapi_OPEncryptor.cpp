#include "com_openpeer_javaapi_OPEncryptor.h"
#include "openpeer/core/IEncryptor.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPEncryptor
 * Method:    create
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Lcom/openpeer/javaapi/OPEncryptor;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPEncryptor_create
(JNIEnv *, jclass, jstring inSourceFileName, jstring inEncodingServiceName)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPEncryptor native create called");

	jni_env = getEnv();

	const char *inSourceFileNameStr;
	inSourceFileNameStr = jni_env->GetStringUTFChars(inSourceFileName, NULL);
	if (inSourceFileNameStr == NULL) {
		return object;
	}

	const char *inEncodingServiceNameStr;
	inEncodingServiceNameStr = jni_env->GetStringUTFChars(inEncodingServiceName, NULL);

	IEncryptorPtr encryptorPtr;
	if (inEncodingServiceNameStr == NULL) {
		encryptorPtr = IEncryptor::create((char const *)inSourceFileNameStr);
	}
	else {
		encryptorPtr = IEncryptor::create((char const *)inSourceFileNameStr, (char const *)inEncodingServiceNameStr);
	}
	if(encryptorPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPEncryptor");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IEncryptorPtr* ptrToEncryptor = new std::shared_ptr<IEncryptor>(encryptorPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong encryptor = (jlong) ptrToEncryptor;
			jni_env->SetLongField(object, fid, encryptor);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPEncryptor native create core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPEncryptor
 * Method:    encrypt
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_openpeer_javaapi_OPEncryptor_encrypt
(JNIEnv *, jobject owner)
{
	jbyte* bufferPtr;
	jbyteArray returnArr;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPEncryptor native encrypt called");

	jni_env = getEnv();
	jclass encryptorClass = findClass("com/openpeer/javaapi/OPEncryptor");
	jfieldID encryptorFid = jni_env->GetFieldID(encryptorClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, encryptorFid);

	IEncryptorPtr* encryptorPtr = (IEncryptorPtr*)pointerValue;

	if (encryptorPtr)
	{
		SecureByteBlockPtr sec = encryptorPtr->get()->encrypt();
		returnArr = jni_env->NewByteArray(sec->SizeInBytes());
		jni_env->SetByteArrayRegion(returnArr, (int)0, (int)sec->SizeInBytes(), (const signed char *)sec->data());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPEncryptor native encrypt core pointer is NULL !!!");
	}
	return returnArr;
}

/*
 * Class:     com_openpeer_javaapi_OPEncryptor
 * Method:    coreFinalize
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPEncryptor_coreFinalize
(JNIEnv *, jobject owner)
{
	jstring ret;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPEncryptor native coreFinalize called");

	jni_env = getEnv();
	jclass encryptorClass = findClass("com/openpeer/javaapi/OPEncryptor");
	jfieldID encryptorFid = jni_env->GetFieldID(encryptorClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, encryptorFid);

	IEncryptorPtr* encryptorPtr = (IEncryptorPtr*)pointerValue;

	if (encryptorPtr)
	{
		ret = jni_env->NewStringUTF(encryptorPtr->get()->finalize().c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPEncryptor native coreFinalize core pointer is NULL !!!");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPEncryptor
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPEncryptor_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPEncryptor");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IEncryptorPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPEncryptor releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPEncryptor releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
