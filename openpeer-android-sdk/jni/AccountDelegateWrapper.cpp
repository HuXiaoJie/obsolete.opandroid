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
#include "AccountDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//IAccountDelegate implementation
AccountDelegateWrapper::AccountDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

void AccountDelegateWrapper::onAccountStateChanged(IAccountPtr account, IAccount::AccountStates state)
{

	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onAccountStateChanged called state = %d", (jint)state);

	bool attached = false;
	switch (android_jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_6))
	{
	case JNI_OK:
		break;
	case JNI_EDETACHED:
		if (android_jvm->AttachCurrentThread(&jni_env, NULL)!=0)
		{
			throw std::runtime_error("Could not attach current thread");
		}
		attached = true;
		break;
	case JNI_EVERSION:
		throw std::runtime_error("Invalid java version");
	}

	if (javaDelegate != NULL){

		//create new OPAccount java object
		cls = findClass("com/openpeer/javaapi/OPAccount");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject accountObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IAccountPtr* ptrToAccount = new boost::shared_ptr<IAccount>(account);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(accountObject, fid, (jlong)ptrToAccount);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onAccountStateChanged", "(Lcom/openpeer/javaapi/OPAccount;Lcom/openpeer/javaapi/AccountStates;)V");
		jni_env->CallVoidMethod(javaDelegate, method, accountObject, OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/AccountStates", (jint) state));

	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onAccountStateChanged Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}

}
void AccountDelegateWrapper::onAccountAssociatedIdentitiesChanged(IAccountPtr account)
{

	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onAccountAssociatedIdentitiesChanged called");

	bool attached = false;
	switch (android_jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_6))
	{
	case JNI_OK:
		break;
	case JNI_EDETACHED:
		if (android_jvm->AttachCurrentThread(&jni_env, NULL)!=0)
		{
			throw std::runtime_error("Could not attach current thread");
		}
		attached = true;
		break;
	case JNI_EVERSION:
		throw std::runtime_error("Invalid java version");
	}

	if (javaDelegate != NULL){

		//create new OPAccount java object
		cls = findClass("com/openpeer/javaapi/OPAccount");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject accountObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IAccountPtr* ptrToAccount = new boost::shared_ptr<IAccount>(account);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(accountObject, fid, (jlong)ptrToAccount);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onAccountAssociatedIdentitiesChanged", "(Lcom/openpeer/javaapi/OPAccount;)V");
		jni_env->CallVoidMethod(javaDelegate, method, accountObject);

	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onAccountAssociatedIdentitiesChanged Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}
void AccountDelegateWrapper::onAccountPendingMessageForInnerBrowserWindowFrame(IAccountPtr account)
{

	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onAccountPendingMessageForInnerBrowserWindowFrame called");

	bool attached = false;
	switch (android_jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_6))
	{
	case JNI_OK:
		break;
	case JNI_EDETACHED:
		if (android_jvm->AttachCurrentThread(&jni_env, NULL)!=0)
		{
			throw std::runtime_error("Could not attach current thread");
		}
		attached = true;
		break;
	case JNI_EVERSION:
		throw std::runtime_error("Invalid java version");
	}

	if (javaDelegate != NULL){

		//create new OPAccount java object
		cls = findClass("com/openpeer/javaapi/OPAccount");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject accountObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IAccountPtr* ptrToAccount = new boost::shared_ptr<IAccount>(account);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(accountObject, fid, (jlong)ptrToAccount);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onAccountPendingMessageForInnerBrowserWindowFrame", "(Lcom/openpeer/javaapi/OPAccount;)V");
		jni_env->CallVoidMethod(javaDelegate, method, accountObject);

	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onAccountPendingMessageForInnerBrowserWindowFrame Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}




AccountDelegateWrapper::~AccountDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
