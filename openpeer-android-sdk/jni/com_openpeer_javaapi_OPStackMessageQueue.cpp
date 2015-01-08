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
#include "com_openpeer_javaapi_OPStackMessageQueue.h"
#include "openpeer/core/IStack.h"
#include "openpeer/core/ILogger.h"
#include <android/log.h>


#include "globals.h"
//#define NULL ((void*) 0)


using namespace openpeer::core;
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_singleton
(JNIEnv *env, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue native singleton called");

	jni_env = getEnv();
	if(jni_env)
	{
		cls = findClass("com/openpeer/javaapi/OPStackMessageQueue");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		IStackMessageQueuePtr* ptrToStackMessageQueue = new std::shared_ptr<IStackMessageQueue>(IStackMessageQueue::singleton());
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong stackMessageQueue = (jlong) ptrToStackMessageQueue;
		jni_env->SetLongField(object, fid, stackMessageQueue);

	}
	return object;

}

JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_interceptProcessing
(JNIEnv *, jobject owner, jobject javaStackMessageQueueDelegate)
{

	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue native interceptProcessing called");

	jni_env = getEnv();
	jclass stackMessageQueueClass = findClass("com/openpeer/javaapi/OPStackMessageQueue");
	jfieldID stackMessageQueueFid = jni_env->GetFieldID(stackMessageQueueClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, stackMessageQueueFid);

	IStackMessageQueuePtr* coreStackMesssageQueuePtr = (IStackMessageQueuePtr*)pointerValue;

	StackMessageQueueDelegateWrapperPtr stackMessageQueueDelegatePtr = StackMessageQueueDelegateWrapperPtr(new StackMessageQueueDelegateWrapper(javaStackMessageQueueDelegate));
	if (coreStackMesssageQueuePtr)
	{
		coreStackMesssageQueuePtr->get()->interceptProcessing(stackMessageQueueDelegatePtr);
		if (stackMessageQueueDelegatePtr)
		{
			StackMessageQueueDelegateWrapperPtr* ptrToStackMessageQueueDelegate = new std::shared_ptr<StackMessageQueueDelegateWrapper>(stackMessageQueueDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(stackMessageQueueClass, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToStackMessageQueueDelegate;
			jni_env->SetLongField(owner, delegateFid, delegate);
		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPStackMessageQueue native interceptProcessing core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPStackMessageQueue
 * Method:    notifyProcessMessageFromCustomThread
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_notifyProcessMessageFromCustomThread
(JNIEnv *, jobject)
{
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue native notifyProcessMessageFromCustomThread called");
	IStackMessageQueue::singleton()->notifyProcessMessageFromCustomThread();
}

/*
 * Class:     com_openpeer_javaapi_OPStackMessageQueue
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPStackMessageQueue_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPStackMessageQueue");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IStackMessageQueuePtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (StackMessageQueueDelegateWrapperPtr*)delegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPStackMessageQueue Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPStackMessageQueue Core object not deleted - already NULL!");
	}
}
#ifdef __cplusplus
}
#endif
