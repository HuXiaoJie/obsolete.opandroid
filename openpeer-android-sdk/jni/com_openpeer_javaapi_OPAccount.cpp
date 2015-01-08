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
/*
 * com_openpeer_javaapi_OPAccount.cpp
 *
 *  Created on: Sep 18, 2013
 *      Author: appl
 */

#include "com_openpeer_javaapi_OPAccount.h"
#include "openpeer/core/IAccount.h"
#include "openpeer/core/IIdentity.h"
#include "openpeer/core/IHelper.h"
#include "OpenPeerCoreManager.h"

#include <android/log.h>;

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/AccountStates;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_toString
(JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    toDebugString
 * Signature: (Lcom/openpeer/javaapi/OPAccount;Z)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_toDebugString
(JNIEnv *, jclass, jobject, jboolean)
{

}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    login
 * Signature: (Lcom/openpeer/javaapi/OPAccountDelegate;Lcom/openpeer/javaapi/OPConversationThreadDelegate;Lcom/openpeer/javaapi/OPCallDelegate;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Lcom/openpeer/javaapi/OPAccount
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPAccount_login
(JNIEnv *env, jclass,
		jobject javaAccountDelegate,
		jobject javaConversationThreadDelegate,
		jobject javaCallDelegate,
		jstring namespaceGrantOuterFrameURLUponReload,
		jstring grantID,
		jstring lockboxServiceDomain,
		jboolean forceCreateNewLockboxAccount)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native login called");

	const char *namespaceGrantOuterFrameURLUponReloadStr;
	namespaceGrantOuterFrameURLUponReloadStr = env->GetStringUTFChars(namespaceGrantOuterFrameURLUponReload, NULL);
	if (namespaceGrantOuterFrameURLUponReloadStr == NULL) {
		return object;
	}

	const char *grantIDStr;
	grantIDStr = env->GetStringUTFChars(grantID, NULL);
	if (grantIDStr == NULL) {
		return object;
	}

	const char *lockboxServiceDomainStr;
	lockboxServiceDomainStr = env->GetStringUTFChars(lockboxServiceDomain, NULL);
	if (lockboxServiceDomainStr == NULL) {
		return object;
	}

	if (javaAccountDelegate == NULL)
	{
		return object;
	}
	if (javaConversationThreadDelegate == NULL)
	{
		return object;
	}
	if (javaCallDelegate == NULL)
	{
		return object;
	}

	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPAccount native login parameters are valid");

	//set java delegate to account delegate wrapper and init shared pointer for wrappers
	AccountDelegateWrapperPtr accountDelegatePtr = AccountDelegateWrapperPtr(new AccountDelegateWrapper(javaAccountDelegate));

	//set java delegate to conversation thread delegate wrapper and init shared pointer for wrappers
	conversationThreadDelegatePtr = ConversationThreadDelegateWrapperPtr(new ConversationThreadDelegateWrapper(javaConversationThreadDelegate));

	//set java delegate to call delegate wrapper and init shared pointer for wrappers
	callDelegatePtr = CallDelegateWrapperPtr(new CallDelegateWrapper(javaCallDelegate));

	IAccountPtr accountPtr = IAccount::login(accountDelegatePtr, conversationThreadDelegatePtr, callDelegatePtr,
			namespaceGrantOuterFrameURLUponReloadStr, grantIDStr, lockboxServiceDomainStr, forceCreateNewLockboxAccount);

	if (accountPtr)
	{
		jni_env = getEnv();
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPAccount");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IAccountPtr* ptrToAccount = new std::shared_ptr<IAccount>(accountPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong acc = (jlong) ptrToAccount;
			jni_env->SetLongField(object, fid, acc);

			if (accountDelegatePtr != NULL)
			{
				AccountDelegateWrapperPtr* ptrToAccountDelegateWrapperPtr= new std::shared_ptr<AccountDelegateWrapper>(accountDelegatePtr);
				jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
				jlong delegate = (jlong) ptrToAccountDelegateWrapperPtr;
				jni_env->SetLongField(object, delegateFid, delegate);
			}

		}

	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native login core pointer is NULL !!!");
	}
	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    relogin
 * Signature: (Lcom/openpeer/javaapi/OPAccountDelegate;Lcom/openpeer/javaapi/OPConversationThreadDelegate;Lcom/openpeer/javaapi/OPCallDelegate;Ljava/lang/String;Ljava/lang/String;)Lcom/openpeer/javaapi/OPAccount;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPAccount_relogin
(JNIEnv *env, jclass,
		jobject javaAccountDelegate,
		jobject javaConversationThreadDelegate,
		jobject javaCallDelegate,
		jstring namespaceGrantOuterFrameURLUponReload,
		jstring reloginInformation)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native relogin called");

	const char *namespaceGrantOuterFrameURLUponReloadStr;
	namespaceGrantOuterFrameURLUponReloadStr = env->GetStringUTFChars(namespaceGrantOuterFrameURLUponReload, NULL);
	if (namespaceGrantOuterFrameURLUponReloadStr == NULL) {
		return object;
	}

	const char *reloginInformationStr;
	reloginInformationStr = env->GetStringUTFChars(reloginInformation, NULL);
	if (reloginInformationStr == NULL) {
		return object;
	}
	if (javaAccountDelegate == NULL)
	{
		return object;
	}
	if (javaConversationThreadDelegate == NULL)
	{
		return object;
	}

	if (javaCallDelegate == NULL)
	{
		return object;
	}

	__android_log_print(ANDROID_LOG_VERBOSE, "com.openpeer.jni", "OPAccount native login parameters are valid");
	//set java delegate to account delegate wrapper and init shared pointer for wrappers
	AccountDelegateWrapperPtr accountDelegatePtr = AccountDelegateWrapperPtr(new AccountDelegateWrapper(javaAccountDelegate));
	//set java delegate to conversation thread delegate wrapper and init shared pointer for wrappers
	conversationThreadDelegatePtr = ConversationThreadDelegateWrapperPtr(new ConversationThreadDelegateWrapper(javaConversationThreadDelegate));
	//set java delegate to call delegate wrapper and init shared pointer for wrappers
	callDelegatePtr = CallDelegateWrapperPtr(new CallDelegateWrapper(javaCallDelegate));


	ElementPtr reloginElement = IHelper::createElement(reloginInformationStr);
	IAccountPtr accountPtr = IAccount::relogin(accountDelegatePtr, conversationThreadDelegatePtr, callDelegatePtr,
			namespaceGrantOuterFrameURLUponReloadStr, reloginElement);

	if (accountPtr)
	{
		jni_env = getEnv();
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPAccount");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);
			globalAccount = object;

			IAccountPtr* ptrToAccount = new std::shared_ptr<IAccount>(accountPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong acc = (jlong) ptrToAccount;
			jni_env->SetLongField(object, fid, acc);

			if (accountDelegatePtr != NULL)
			{
				AccountDelegateWrapperPtr* ptrToAccountDelegateWrapperPtr= new std::shared_ptr<AccountDelegateWrapper>(accountDelegatePtr);
				jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
				jlong delegate = (jlong) ptrToAccountDelegateWrapperPtr;
				jni_env->SetLongField(object, delegateFid, delegate);
			}

		}

	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native relogin core pointer is NULL !!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getID
 * Signature: ()J;
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPAccount_getID
(JNIEnv *env, jobject owner)
{
	jlong pid = 0;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getID called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		pid = coreAccountPtr->get()->getID();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getID core pointer is NULL !!!");
	}

	return pid;

}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getStableID
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_getStableID
(JNIEnv *, jobject owner)
{
	jstring stableId;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getStableID called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		stableId = jni_env->NewStringUTF(coreAccountPtr->get()->getStableID().c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getStableID core pointer is NULL !!!");
	}
	return stableId;

}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getState
 * Signature: (ILjava/lang/String;)Lcom/openpeer/javaapi/AccountStates;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPAccount_getState
(JNIEnv *, jobject owner, jint, jstring)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jint state = 0;
	unsigned short int outErrorCode;
	String outErrorReason;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getState called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		state = (jint) coreAccountPtr->get()->getState(&outErrorCode, &outErrorReason);
		__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni", "Account State = %d", state);
		if(jni_env)
		{
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/AccountStates", state);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getState core pointer is NULL !!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getReloginInformation
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_getReloginInformation
(JNIEnv *env , jobject owner)
{
	ElementPtr reloginInfoElement;
	jstring reloginInfo;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getReloginInformation called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		reloginInfoElement = coreAccountPtr->get()->getReloginInformation();

		reloginInfo =  jni_env->NewStringUTF(IHelper::convertToString(reloginInfoElement).c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getReloginInformation core pointer is NULL !!!");
	}
	return reloginInfo;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getLocationID
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_getLocationID
(JNIEnv *env , jobject owner)
{
	jstring locationID;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getLocationID called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{

		locationID =  jni_env->NewStringUTF(coreAccountPtr->get()->getLocationID().c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getLocationID core pointer is NULL !!!");
	}
	return locationID;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPAccount_shutdown
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native shutdown called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;
	if (coreAccountPtr)
	{
		coreAccountPtr->get()->shutdown();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native shutdown core pointer is NULL !!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getPeerFilePrivate
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_getPeerFilePrivate
(JNIEnv *env, jobject owner)
{
	ElementPtr peerFilePrivateElement;
	jstring peerFilePrivate;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getPeerFilePrivate called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		peerFilePrivateElement = coreAccountPtr->get()->savePeerFilePrivate();

		peerFilePrivate =  env->NewStringUTF(IHelper::convertToString(peerFilePrivateElement).c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getPeerFilePrivate core pointer is NULL !!!");
	}
	return peerFilePrivate;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getPeerFilePrivateSecret
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_openpeer_javaapi_OPAccount_getPeerFilePrivateSecret
(JNIEnv *env, jobject owner)
{
	jbyte* bufferPtr;
	jbyteArray returnArr;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getPeerFilePrivateSecret called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		SecureByteBlockPtr sec = coreAccountPtr->get()->getPeerFilePrivateSecret();
		returnArr = env->NewByteArray(sec->SizeInBytes());
		env->SetByteArrayRegion(returnArr, (int)0, (int)sec->SizeInBytes(), (const signed char *)sec->data());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getPeerFilePrivateSecret core pointer is NULL !!!");
	}
	return returnArr;

}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getAssociatedIdentities
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPAccount_getAssociatedIdentities
(JNIEnv *env, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getAssociatedIdentities called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//Core identity list
	IdentityListPtr coreIdentities;


	//take associated identities from core
	if (coreAccountPtr)
	{
		coreIdentities = coreAccountPtr->get()->getAssociatedIdentities();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getAssociatedIdentities core pointer is NULL !!!");
	}

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);


		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		//fill/update map
		for(IdentityList::iterator coreListIter = coreIdentities->begin();
				coreListIter != coreIdentities->end(); coreListIter++)
		{
			//fetch List item object / OPIdentity
			jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
			jmethodID identityConstructorMethodID = jni_env->GetMethodID(identityClass, "<init>", "()V");
			jobject identityObject = jni_env->NewObject(identityClass, identityConstructorMethodID);

			IIdentityPtr* ptrToIdentity =  new std::shared_ptr<IIdentity>(*coreListIter);
			jfieldID fid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
			jlong identity = (jlong) ptrToIdentity;
			jni_env->SetLongField(identityObject, fid, identity);

			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , identityObject);


		}

	}
	return returnListObject;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    removeIdentities
 * Signature: (Ljava/util/List;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPAccount_removeIdentities
(JNIEnv *, jobject owner, jobject identitiesToRemove)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native removeIdentities called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//Core identity list
	IdentityList coreIdentitiesToRemove;

	//fetch JNI env
	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		if(jni_env->IsInstanceOf(identitiesToRemove, arrayListClass) != JNI_TRUE)
		{
			return;
		}
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( identitiesToRemove, sizeMethodID );

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get IdentParams object by index.
			jobject identityObject = jni_env->CallObjectMethod( identitiesToRemove, listGetMethodID, i );
			if( identityObject != NULL )
			{
				jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
				jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
				jlong pointerValue = jni_env->GetLongField(identityObject, identityFid);

				IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
				//add core identities to list for removal
				coreIdentitiesToRemove.push_front(*coreIdentityPtr);

			}
		}
	}
	//remove associated identities from core
	if (coreAccountPtr)
	{
		coreAccountPtr->get()->removeIdentities(coreIdentitiesToRemove);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native removeIdentities core pointer is NULL !!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getInnerBrowserWindowFrameURL
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_getInnerBrowserWindowFrameURL
(JNIEnv *, jobject owner)
{
	String innerBrowserWindowFrameURLString;
	jstring innerBrowserWindowFrameURL;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getInnerBrowserWindowFrameURL called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		innerBrowserWindowFrameURLString = coreAccountPtr->get()->getInnerBrowserWindowFrameURL();

		innerBrowserWindowFrameURL =  jni_env->NewStringUTF(innerBrowserWindowFrameURLString.c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getInnerBrowserWindowFrameURL core pointer is NULL !!!");
	}
	return innerBrowserWindowFrameURL;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    notifyBrowserWindowVisible
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPAccount_notifyBrowserWindowVisible
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native notifyBrowserWindowVisible called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;
	if (coreAccountPtr)
	{
		coreAccountPtr->get()->notifyBrowserWindowVisible();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native notifyBrowserWindowVisible core pointer is NULL !!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    notifyBrowserWindowClosed
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPAccount_notifyBrowserWindowClosed
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native notifyBrowserWindowClosed called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;
	if (coreAccountPtr)
	{
		coreAccountPtr->get()->notifyBrowserWindowClosed();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native notifyBrowserWindowClosed core pointer is NULL !!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    getNextMessageForInnerBrowerWindowFrame
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPAccount_getNextMessageForInnerBrowerWindowFrame
(JNIEnv *, jobject owner)
{
	ElementPtr nextMessageForInnerBrowerWindowFrameElement;
	jstring nextMessageForInnerBrowerWindowFrame;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native getNextMessageForInnerBrowserWindowFrame called");

	jni_env = getEnv();
	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		nextMessageForInnerBrowerWindowFrameElement = coreAccountPtr->get()->getNextMessageForInnerBrowerWindowFrame();

		nextMessageForInnerBrowerWindowFrame =  jni_env->NewStringUTF(IHelper::convertToString(nextMessageForInnerBrowerWindowFrameElement).c_str());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native getNextMessageForInnerBrowserWindowFrame core pointer is NULL !!!");
	}
	return nextMessageForInnerBrowerWindowFrame;
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    handleMessageFromInnerBrowserWindowFrame
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPAccount_handleMessageFromInnerBrowserWindowFrame
(JNIEnv *env, jobject owner, jstring unparsedMessage)
{
	String unparsedMessageString;

	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native handleMessageFromInnerBrowserWindowFrame called");

	jni_env = getEnv();

	unparsedMessageString = jni_env->GetStringUTFChars(unparsedMessage, NULL);
	if (unparsedMessageString == NULL) {
		return;
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	if (coreAccountPtr)
	{
		coreAccountPtr->get()->handleMessageFromInnerBrowserWindowFrame(IHelper::createElement(unparsedMessageString));
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPAccount native handleMessageFromInnerBrowserWindowFrame core pointer is NULL !!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPAccount
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPAccount_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPAccount");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IAccountPtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (AccountDelegateWrapperPtr*)delegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPAccount native object deleted");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPAccount native object not deleted - already NULL");
	}
}

#ifdef __cplusplus
}
#endif


