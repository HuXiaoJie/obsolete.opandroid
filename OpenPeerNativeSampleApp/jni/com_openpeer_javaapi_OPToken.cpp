#include "com_openpeer_javaapi_OPToken.h"
#include "openpeer/core/IIdentity.h"
#include "openpeer/core/IHelper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    hasData
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPToken_hasData
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jboolean ret;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native hasData called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	if (coreTokenPtr)
	{
		ret = (jboolean) coreTokenPtr->get()->hasData();
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native hasData core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    toDebug
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPToken_toDebug
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native toDebug called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	if (coreTokenPtr)
	{
		ElementPtr coreEl = coreTokenPtr->get()->toDebug();
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
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native toDebug core pointer is NULL");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    mergeFrom
 * Signature: (Lcom/openpeer/javaapi/OPToken;Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPToken_mergeFrom
(JNIEnv *, jobject owner, jobject sourceToken, jboolean overwriteExisting)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native mergeFrom called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	//check if source token is valid
	jlong sourceTokenPointerValue = jni_env->GetLongField(sourceToken, fid);
	if (sourceTokenPointerValue == 0)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native mergeFrom source token pointer is NULL");
		return;
	}

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	UseTokenPtr* coreSourceTokenPtr = (UseTokenPtr*)sourceTokenPointerValue;
	if (coreTokenPtr && coreSourceTokenPtr)
	{
		coreTokenPtr->get()->mergeFrom(*(coreSourceTokenPtr->get()), overwriteExisting);
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native mergeFrom core pointer is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    create
 * Signature: (Ljava/lang/String;Ljava/lang/String;Landroid/text/format/Time;)Lcom/openpeer/javaapi/OPToken;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPToken_create__Ljava_lang_String_2Ljava_lang_String_2Landroid_text_format_Time_2
(JNIEnv *, jclass, jstring masterSecret, jstring associatedID, jobject validDuration)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native create called");

	jni_env = getEnv();

	const char *masterSecretStr;
	masterSecretStr = jni_env->GetStringUTFChars(masterSecret, NULL);
	if (masterSecretStr == NULL) {
		return object;
	}

	const char *associatedIDStr;
	associatedIDStr = jni_env->GetStringUTFChars(associatedID, NULL);
	if (associatedIDStr == NULL) {
		return object;
	}

	IIdentity::Token tempToken = IIdentity::Token::create(masterSecretStr, associatedIDStr, OpenPeerCoreManager::convertSecondsFromJava(validDuration));
	IIdentity::Token* coreToken = &tempToken;
	if(coreToken->hasData())
	{
		UseTokenPtr coreTokenPtr = UseTokenPtr(coreToken);
		UseTokenPtr* ptrToToken = new std::shared_ptr<IIdentity::Token>(coreTokenPtr);
		cls = findClass("com/openpeer/javaapi/OPToken");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong token = (jlong) ptrToToken;
		jni_env->SetLongField(object, fid, token);

		OpenPeerCoreManager::fillJavaTokenFromCoreObject(object, *coreToken);
	}

	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPToken native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    create
 * Signature: (Lcom/openpeer/javaapi/OPElement;)Lcom/openpeer/javaapi/OPToken;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPToken_create__Lcom_openpeer_javaapi_OPElement_2
(JNIEnv *, jclass, jobject element)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native create called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPElement");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(element, fid);

	ElementPtr* coreElementPtr = (ElementPtr*)pointerValue;

	IIdentity::Token tempToken = IIdentity::Token::create(*coreElementPtr);
	IIdentity::Token* coreToken = &tempToken;
	if(coreToken->hasData())
	{
		UseTokenPtr coreTokenPtr = UseTokenPtr(coreToken);
		UseTokenPtr* ptrToToken = new std::shared_ptr<IIdentity::Token>(coreTokenPtr);
		cls = findClass("com/openpeer/javaapi/OPToken");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong token = (jlong) ptrToToken;
		jni_env->SetLongField(object, fid, token);

		OpenPeerCoreManager::fillJavaTokenFromCoreObject(object, *coreToken);
	}

	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPToken native create core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    createProof
 * Signature: (Ljava/lang/String;Landroid/text/format/Time;)Lcom/openpeer/javaapi/OPToken;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPToken_createProof
(JNIEnv *, jobject owner, jstring resource, jobject validDuration)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native createProof called");

	jni_env = getEnv();

	const char *resourceStr;
	resourceStr = jni_env->GetStringUTFChars(resource, NULL);
	if (resourceStr == NULL) {
		return object;
	}

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	if(coreTokenPtr) {
		IIdentity::Token tempToken = coreTokenPtr->get()->createProof(resourceStr, OpenPeerCoreManager::convertSecondsFromJava(validDuration));
		IIdentity::Token* coreToken = &tempToken;
		if(coreToken->hasData())
		{
			UseTokenPtr coreTokenPtr = UseTokenPtr(coreToken);
			UseTokenPtr* ptrToToken = new std::shared_ptr<IIdentity::Token>(coreTokenPtr);
			cls = findClass("com/openpeer/javaapi/OPToken");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong token = (jlong) ptrToToken;
			jni_env->SetLongField(object, fid, token);

			OpenPeerCoreManager::fillJavaTokenFromCoreObject(object, *coreToken);
		}
		else
		{
			__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPToken native createProof created pointer is NULL!!! ");
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPToken native createProof core pointer is NULL!!! ");
	}
	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    createElement
 * Signature: ()Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPToken_createElement
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native createProof called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	if(coreTokenPtr) {
		ElementPtr coreEl = coreTokenPtr->get()->createElement();
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
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPToken native createProof core pointer is NULL!!! ");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    validate
 * Signature: (Lcom/openpeer/javaapi/OPToken;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPToken_validate__Lcom_openpeer_javaapi_OPToken_2
(JNIEnv *, jobject owner, jobject proofToken)
{
	jclass cls;
	jmethodID method;
	jobject object;
	jboolean ret = false;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native validate called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	//check if source token is valid
	jlong proofTokenPointerValue = jni_env->GetLongField(proofToken, fid);
	if (proofTokenPointerValue == 0)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native validate proof token pointer is NULL");
		return ret;
	}

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	UseTokenPtr* coreProofTokenPtr = (UseTokenPtr*)proofTokenPointerValue;
	if (coreTokenPtr && coreProofTokenPtr)
	{
		ret = coreTokenPtr->get()->validate(*(coreProofTokenPtr->get()));
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native validate core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    validate
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPToken_validate__Ljava_lang_String_2
(JNIEnv *, jobject owner, jstring inMasterSecret)
{
	jclass cls;
	jmethodID method;
	jobject object;
	jstring ret;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native validate called");

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPToken");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	const char *inMasterSecretStr;
	inMasterSecretStr = jni_env->GetStringUTFChars(inMasterSecret, NULL);
	if (inMasterSecretStr == NULL) {
		return ret;
	}

	UseTokenPtr* coreTokenPtr = (UseTokenPtr*)pointerValue;
	if (coreTokenPtr)
	{
		String outAssociatedID;
		//todo: return associated ID as some structure along with boolean
		coreTokenPtr->get()->validate(inMasterSecretStr, outAssociatedID);
		ret = jni_env->NewStringUTF(outAssociatedID.c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken native validate core pointer is NULL");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPToken
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPToken_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPToken");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (UseTokenPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPToken releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPToken releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
