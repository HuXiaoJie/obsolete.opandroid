/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
#include "openpeer/core/IContact.h"
#include "openpeer/core/IHelper.h"
#include "openpeer/core/ILogger.h"
#include "OpenPeerCoreManager.h"

#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    toDebugString
 * Signature: (Lcom/openpeer/javaapi/OPContact;Z)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPContact_toDebugString
(JNIEnv *, jclass, jobject, jboolean)
{

}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    createFromPeerFilePublic
 * Signature: (Lcom/openpeer/javaapi/OPAccount;Ljava/lang/String;)Lcom/openpeer/javaapi/OPContact;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPContact_createFromPeerFilePublic
(JNIEnv *, jclass,
		jobject javaAccount,
		jstring peerFilePublic)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	IContactPtr contactPtr;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native createFromPeerFilePublic called");

	jni_env = getEnv();

	const char *peerFilePublicStr;
	peerFilePublicStr = jni_env->GetStringUTFChars(peerFilePublic, NULL);
	if (peerFilePublicStr == NULL) {
		return object;
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if(coreAccountPtr)
	{
		ElementPtr peerFileElement = IHelper::createElement(peerFilePublicStr);
		contactPtr = IContact::createFromPeerFilePublic(*coreAccountPtr, IHelper::createPeerFilePublic(peerFileElement));
	}

	if(contactPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPContact");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IContactPtr* ptrToContact = new std::shared_ptr<IContact>(contactPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong contact = (jlong) ptrToContact;
			jni_env->SetLongField(object, fid, contact);

			__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni",
					"CorePtr raw = %p, ptr as long = %Lu",contactPtr.get(), contact);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native createFromPeerFilePublic core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    getForSelf
 * Signature: (Lcom/openpeer/javaapi/OPAccount;)Lcom/openpeer/javaapi/OPContact;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPContact_getForSelf
(JNIEnv *, jclass, jobject javaAccount)
{
	jclass cls;
	jmethodID method;
	jobject object = NULL;
	JNIEnv *jni_env = 0;
	IContactPtr contactPtr;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native getForSelf called");

	jni_env = getEnv();

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if(coreAccountPtr)
	{
		contactPtr = IContact::getForSelf(*coreAccountPtr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native getForSelf core account pointer is NULL!!!");
	}

	if(contactPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPContact");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IContactPtr* ptrToContact = new std::shared_ptr<IContact>(contactPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong contact = (jlong) ptrToContact;
			jni_env->SetLongField(object, fid, contact);

			__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni",
					"CorePtr raw = %p, ptr as long = %Lu",contactPtr.get(), contact);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native getForSelf core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    getStableID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPContact_getStableID
(JNIEnv *, jobject owner)
{
	jlong ret = 0;
	JNIEnv * jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native getStableID called");

	jni_env = getEnv();
	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(owner, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;
	if (contactPtr)
	{
		ret = contactPtr->get()->getID();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native getStableID core pointer is NULL!!!");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    isSelf
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPContact_isSelf
(JNIEnv *, jobject owner)
{
	jboolean ret;
	JNIEnv * jni_env = 0;
	jni_env = getEnv();

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native isSelf called");

	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(owner, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;
	if (contactPtr)
	{
		ret = contactPtr->get()->isSelf();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native isSelf core pointer is NULL!!!");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    getPeerURI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPContact_getPeerURI
(JNIEnv *, jobject owner)
{
	jstring ret;
	JNIEnv * jni_env = 0;
	jni_env = getEnv();

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native getPeerURI called");

	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(owner, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;
	if (contactPtr)
	{
		ret = jni_env->NewStringUTF(contactPtr->get()->getPeerURI().c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native getPeerURI core pointer is NULL!!!");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    getPeerFilePublic
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPContact_getPeerFilePublic
(JNIEnv *env, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	jstring ret;
	JNIEnv * jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native getPeerFilePublic called");

	jni_env = getEnv();
	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(owner, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;
	if (contactPtr)
	{
		if(jni_env)
		{
			ElementPtr peerFilePublic = IHelper::convertToElement(contactPtr->get()->getPeerFilePublic());
			ret = env->NewStringUTF(IHelper::convertToString(peerFilePublic).c_str());

			//TODO export peer file public to ElementPtr and then convert to String
			jclass peerFileCls = findClass("com/openpeer/javaapi/OPPeerFilePublic");
			jmethodID peerFileMethodID = jni_env->GetMethodID(peerFileCls, "<init>", "()V");
			jobject peerFileObject = jni_env->NewObject(peerFileCls, peerFileMethodID);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native getPeerFilePublic core pointer is NULL!!!");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    getAssociatedAccount
 * Signature: ()Lcom/openpeer/javaapi/OPAccount;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPContact_getAssociatedAccount
(JNIEnv *, jobject owner)
{
	jobject accountObject;
	JNIEnv * jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native getAssociatedAccount called");

	jni_env = getEnv();
	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(owner, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;
	if (contactPtr)
	{
		IAccountPtr accountPtr = contactPtr->get()->getAssociatedAccount();
		if (accountPtr)
		{
			jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
			jmethodID accountMethod = jni_env->GetMethodID(accountClass, "<init>", "()V");
			accountObject = jni_env->NewObject(accountClass, accountMethod);

			IAccountPtr* ptrToAccount = new std::shared_ptr<IAccount>(accountPtr);
			jfieldID fid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
			jlong acc = (jlong) ptrToAccount;
			jni_env->SetLongField(accountObject, fid, acc);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native getAssociatedAccount core pointer is NULL!!!");
	}
	return accountObject;
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    hintAboutLocation
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPContact_hintAboutLocation
(JNIEnv *, jobject owner, jstring locationId)
{
	JNIEnv * jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact native hintAboutLocation called");

	jni_env = getEnv();
	const char *locationIdStr;
	locationIdStr = jni_env->GetStringUTFChars(locationId, NULL);
	if (locationIdStr == NULL) {
		return;
	}

	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(owner, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;
	if (contactPtr)
	{
		contactPtr->get()->hintAboutLocation(locationIdStr);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPContact native hinstAboutLocation core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPContact
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPContact_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPContact");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IContactPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPContact core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPContact core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
