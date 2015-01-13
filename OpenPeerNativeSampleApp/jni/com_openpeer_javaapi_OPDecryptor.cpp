#include "com_openpeer_javaapi_OPDecryptor.h"
#include "openpeer/core/IDecryptor.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPDecryptor
 * Method:    create
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Lcom/openpeer/javaapi/OPDecryptor;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPDecryptor_create
(JNIEnv *, jclass, jstring inSourceFileName, jstring inEncoding)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPDecryptor native create called");

	jni_env = getEnv();

	const char *inSourceFileNameStr;
	inSourceFileNameStr = jni_env->GetStringUTFChars(inSourceFileName, NULL);
	if (inSourceFileNameStr == NULL) {
		return object;
	}

	const char *inEncodingStr;
	inEncodingStr = jni_env->GetStringUTFChars(inEncoding, NULL);
	if (inEncodingStr == NULL) {
		return object;
	}

	IDecryptorPtr decryptorPtr = IDecryptor::create((char const *)inSourceFileNameStr, (char const *)inEncodingStr);

	if(decryptorPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPDecryptor");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IDecryptorPtr* ptrToDecryptor = new std::shared_ptr<IDecryptor>(decryptorPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong decryptor = (jlong) ptrToDecryptor;
			jni_env->SetLongField(object, fid, decryptor);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPDecryptor native create core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPDecryptor
 * Method:    decrypt
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_openpeer_javaapi_OPDecryptor_decrypt
(JNIEnv *, jobject owner)
{
	jbyte* bufferPtr;
	jbyteArray returnArr;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPDecryptor native decrypt called");

	jni_env = getEnv();
	jclass decryptorClass = findClass("com/openpeer/javaapi/OPDecryptor");
	jfieldID decryptorFid = jni_env->GetFieldID(decryptorClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, decryptorFid);

	IDecryptorPtr* coreDecryptorPtr = (IDecryptorPtr*)pointerValue;

	if (coreDecryptorPtr)
	{
		SecureByteBlockPtr sec = coreDecryptorPtr->get()->decrypt();
		returnArr = jni_env->NewByteArray(sec->SizeInBytes());
		jni_env->SetByteArrayRegion(returnArr, (int)0, (int)sec->SizeInBytes(), (const signed char *)sec->data());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPDecryptor native decrypt core pointer is NULL !!!");
	}
	return returnArr;
}

/*
 * Class:     com_openpeer_javaapi_OPDecryptor
 * Method:    coreFinalize
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPDecryptor_coreFinalize
(JNIEnv *, jobject owner)
{
	jboolean ret;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPDecryptor native coreFinalize called");

	jni_env = getEnv();
	jclass decryptorClass = findClass("com/openpeer/javaapi/OPDecryptor");
	jfieldID decryptorFid = jni_env->GetFieldID(decryptorClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, decryptorFid);

	IDecryptorPtr* coreDecryptorPtr = (IDecryptorPtr*)pointerValue;

	if (coreDecryptorPtr)
	{
		ret = (jboolean)coreDecryptorPtr->get()->finalize();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPDecryptor native coreFinalize core pointer is NULL !!!");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPDecryptor
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPDecryptor_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPDecryptor");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IDecryptorPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPDecryptor releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPDecryptor releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
