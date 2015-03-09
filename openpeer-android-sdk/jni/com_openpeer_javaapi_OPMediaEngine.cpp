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
#include "com_openpeer_javaapi_OPMediaEngine.h"
#include "OpenPeerCoreManager.h"
#include "openpeer/core/IStack.h"
#include "openpeer/core/ILogger.h"
#include "openpeer/core/internal/core_MediaEngine.h"
#include "openpeer/core/test/TestMediaEngine.h"
#include <android/log.h>
#include "webrtc/modules/video_capture/video_capture_internal.h"
#include "webrtc/modules/video_render/video_render_internal.h"
#include "webrtc/voice_engine/include/voe_base.h"

#include "globals.h"

//#define TEST_MEDIA_ENGINE

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif

jobject g_glChannelSurface;
jobject g_glCaptureSurface;

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    init
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_init
(JNIEnv *, jclass, jobject context)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	if(jni_env)
	{
		webrtc::VoiceEngine::SetAndroidObjects(android_jvm, jni_env, context);
		webrtc::SetCaptureAndroidVM(android_jvm, context);
		webrtc::SetRenderAndroidVM(android_jvm);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    singleton
 * Signature: ()Lcom/openpeer/javaapi/OPMediaEngine;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_singleton
(JNIEnv *, jclass)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	if(jni_env)
	{
#ifdef TEST_MEDIA_ENGINE
		cls = findClass("com/openpeer/javaapi/test/OPTestMediaEngine");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);

		openpeer::core::test::TestMediaEngineFactoryPtr overrideFactory(new openpeer::core::test::TestMediaEngineFactory);
		openpeer::core::internal::Factory::override(overrideFactory);
#else
		cls = findClass("com/openpeer/javaapi/OPMediaEngine");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		object = jni_env->NewObject(cls, method);


#endif
		IMediaEnginePtr* ptrToMediaEngine = new std::shared_ptr<IMediaEngine>(IMediaEngine::singleton());
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong engine = (jlong) ptrToMediaEngine;
		jni_env->SetLongField(object, fid, engine);
	}
	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setDefaultVideoOrientation
 * Signature: (Lcom/openpeer/javaapi/VideoOrientations;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setDefaultVideoOrientation
(JNIEnv *, jobject owner, jobject videoOrientationObj)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		cls = findClass("com/openpeer/javaapi/VideoOrientations");
		if(jni_env->IsInstanceOf(videoOrientationObj, cls) == JNI_TRUE)
		{
			jint intValue = OpenPeerCoreManager::getIntValueFromEnumObject(videoOrientationObj, "com/openpeer/javaapi/VideoOrientations");

			IMediaEngine::VideoOrientations orientation = (IMediaEngine::VideoOrientations)intValue;
			coreMediaEnginePtr->get()->setDefaultVideoOrientation(orientation);
		}
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getDefaultVideoOrientation
 * Signature: ()Lcom/openpeer/javaapi/VideoOrientations;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getDefaultVideoOrientation
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		if(jni_env)
		{
			IMediaEngine::VideoOrientations value = coreMediaEnginePtr->get()->getDefaultVideoOrientation();
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/VideoOrientations", (jint)value);
		}
	}
	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setRecordVideoOrientation
 * Signature: (Lcom/openpeer/javaapi/VideoOrientations;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setRecordVideoOrientation
(JNIEnv *, jobject owner, jobject videoOrientationObj)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		cls = findClass("com/openpeer/javaapi/VideoOrientations");
		if(jni_env->IsInstanceOf(videoOrientationObj, cls) == JNI_TRUE)
		{
			jint intValue = OpenPeerCoreManager::getIntValueFromEnumObject(videoOrientationObj, "com/openpeer/javaapi/VideoOrientations");

			IMediaEngine::VideoOrientations orientation = (IMediaEngine::VideoOrientations)intValue;
			coreMediaEnginePtr->get()->setRecordVideoOrientation(orientation);
		}
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getRecordVideoOrientation
 * Signature: ()Lcom/openpeer/javaapi/VideoOrientations;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getRecordVideoOrientation
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		if(jni_env)
		{
			IMediaEngine::VideoOrientations value = coreMediaEnginePtr->get()->getRecordVideoOrientation();
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/VideoOrientations", (jint)value);
		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setVideoOrientation
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setVideoOrientation
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;
	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setVideoOrientation();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setCaptureRenderView
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setCaptureRenderView
(JNIEnv *, jobject owner, jobject glSurface)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;
	if (coreMediaEnginePtr)
	{

		g_glCaptureSurface = jni_env->NewGlobalRef(glSurface);
		coreMediaEnginePtr->get()->setCaptureRenderView(g_glCaptureSurface);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setChannelRenderView
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setChannelRenderView
(JNIEnv *, jobject owner, jobject glSurface)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;
	if (coreMediaEnginePtr)
	{

		g_glChannelSurface = jni_env->NewGlobalRef(glSurface);
		coreMediaEnginePtr->get()->setChannelRenderView(g_glChannelSurface);
	}

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setCaptureCapability
 * Signature: (Lcom/openpeer/javaapi/OPCaptureCapability;Lcom/openpeer/javaapi/CameraTypes;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setCaptureCapability
(JNIEnv *, jobject owner, jobject capability, jobject cameraType)
{
	JNIEnv *jni_env = 0;
	IMediaEngine::CaptureCapability coreCapability;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;
	if (coreMediaEnginePtr)
	{

		//Fetch OPCaptureCapability class
		jclass capabilityClass = findClass("com/openpeer/javaapi/OPCaptureCapability");
		//Fetch getter methods from OPCaptureCapability class
		jmethodID getWidthID = jni_env->GetMethodID( capabilityClass, "getWidth", "()I" );
		jmethodID getHeightID = jni_env->GetMethodID( capabilityClass, "getHeight", "()I" );
		jmethodID getMaxFPSID = jni_env->GetMethodID( capabilityClass, "getMaxFPS", "()I" );

		coreCapability.width = (int)jni_env->CallIntMethod(capability, getWidthID);
		coreCapability.height = (int)jni_env->CallIntMethod(capability, getHeightID);
		coreCapability.maxFPS = (int)jni_env->CallIntMethod(capability, getMaxFPSID);

		coreMediaEnginePtr->get()->setCaptureCapability(coreCapability,
				(IMediaEngine::CameraTypes)OpenPeerCoreManager::getIntValueFromEnumObject(cameraType, "com/openpeer/javaapi/CameraTypes"));
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getCaptureCapabilities
 * Signature: (Lcom/openpeer/javaapi/CameraTypes;)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getCaptureCapabilities
(JNIEnv *, jobject owner, jobject cameraType)
{
	jclass cls;
	jmethodID method;
	jobject returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPMediaEngine native getCaptureCapabilities called");

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	//core capabilities
	IMediaEngine::CaptureCapabilityList coreCapabilities;
	if (coreMediaEnginePtr)
	{
		coreCapabilities = coreMediaEnginePtr->get()->getCaptureCapabilities((IMediaEngine::CameraTypes)OpenPeerCoreManager::getIntValueFromEnumObject(cameraType, "com/openpeer/javaapi/CameraTypes"));
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPMediaEngine native getCaptureCapabilities core pointer is NULL !!!");
	}

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);


		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		//Fetch OPCaptureCapability class
		jclass capabilityClass = findClass("com/openpeer/javaapi/OPCaptureCapability");
		jmethodID capabilityConstructorMethodID = jni_env->GetMethodID(capabilityClass, "<init>", "()V");
		//Fetch getter methods from OPCaptureCapability class
		jmethodID setWidthID = jni_env->GetMethodID( capabilityClass, "setWidth", "(I)V" );
		jmethodID setHeightID = jni_env->GetMethodID( capabilityClass, "setHeight", "(I)V" );
		jmethodID setMaxFPSID = jni_env->GetMethodID( capabilityClass, "setMaxFPS", "(I)V" );

		//fill/update map
		for(IMediaEngine::CaptureCapabilityList::iterator coreListIter = coreCapabilities.begin();
				coreListIter != coreCapabilities.end(); coreListIter++)
		{
			jobject capabilityObject = jni_env->NewObject(capabilityClass, capabilityConstructorMethodID);

			jni_env->CallVoidMethod(capabilityObject, setWidthID, (jint)coreListIter->width);
			jni_env->CallVoidMethod(capabilityObject, setHeightID, (jint)coreListIter->height);
			jni_env->CallVoidMethod(capabilityObject, setMaxFPSID, (jint)coreListIter->maxFPS);
			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , capabilityObject);

			jni_env->DeleteLocalRef(capabilityObject);
		}

	}
	return returnListObject;
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setCaptureRenderViewCropping
 * Signature: (FFFF)V
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setCaptureRenderViewCropping
  (JNIEnv *, jobject owner, jfloat left, jfloat top, jfloat right, jfloat bottom)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setCaptureRenderViewCropping(left, top, right, bottom);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setChannelRenderViewCropping
 * Signature: (FFFF)V
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setChannelRenderViewCropping
  (JNIEnv *, jobject owner, jfloat left, jfloat top, jfloat right, jfloat bottom)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setChannelRenderViewCropping(left, top, right, bottom);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setEcEnabled
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setEcEnabled
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		coreMediaEnginePtr->get()->setEcEnabled(value);
	}

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setAgcEnabled
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setAgcEnabled
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setAgcEnabled(value);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setNsEnabled
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setNsEnabled
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setNsEnabled(value);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setVoiceRecordFile
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setVoiceRecordFile
(JNIEnv *, jobject owner, jstring filename)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	String filenameString;
	filenameString = jni_env->GetStringUTFChars(filename, NULL);
	if (filenameString == NULL) {
		return;
	}


	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setVoiceRecordFile(filenameString);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getVoiceRecordFile
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getVoiceRecordFile
(JNIEnv *, jobject owner)
{
	jstring ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		String record = coreMediaEnginePtr->get()->getVoiceRecordFile();
		ret = jni_env->NewStringUTF(record.c_str());
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setMuteEnabled
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setMuteEnabled
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setMuteEnabled(value);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getMuteEnabled
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getMuteEnabled
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		return coreMediaEnginePtr->get()->getMuteEnabled();
	}
	else
	{
		return IMediaEngine::singleton()->getMuteEnabled();
	}

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setLoudspeakerEnabled
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setLoudspeakerEnabled
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setLoudspeakerEnabled(value);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getLoudspeakerEnabled
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getLoudspeakerEnabled
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		return coreMediaEnginePtr->get()->getLoudspeakerEnabled();
	}
	else
	{
		return IMediaEngine::singleton()->getLoudspeakerEnabled();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getOutputAudioRoute
 * Signature: ()Lcom/openpeer/javaapi/OutputAudioRoutes;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getOutputAudioRoute
(JNIEnv *, jobject owner)
{

	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		if(jni_env)
		{
			IMediaEngine::OutputAudioRoutes value = coreMediaEnginePtr->get()->getOutputAudioRoute();
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/OutputAudioRoutes", (jint)value);
		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setContinuousVideoCapture
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setContinuousVideoCapture
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setContinuousVideoCapture(value);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getContinuousVideoCapture
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getContinuousVideoCapture
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		return coreMediaEnginePtr->get()->getContinuousVideoCapture();
	}
	else
	{
		return IMediaEngine::singleton()->getContinuousVideoCapture();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setFaceDetection
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setFaceDetection
(JNIEnv *, jobject owner, jboolean value)
{
	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setFaceDetection(value);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getFaceDetection
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getFaceDetection
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		return coreMediaEnginePtr->get()->getFaceDetection();
	}
	else
	{
		return IMediaEngine::singleton()->getFaceDetection();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getCameraType
 * Signature: ()Lcom/openpeer/javaapi/CameraTypes;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getCameraType
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		IMediaEngine::CameraTypes value = coreMediaEnginePtr->get()->getCameraType();
		object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/CameraTypes", (jint) value);

	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    setCameraType
 * Signature: (Lcom/openpeer/javaapi/CameraTypes;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_setCameraType
(JNIEnv *, jobject owner, jobject cameraTypeObj)
{

	jclass cls;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->setCameraType((IMediaEngine::CameraTypes)OpenPeerCoreManager::getIntValueFromEnumObject(cameraTypeObj, "com/openpeer/javaapi/CameraTypes"));
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    startVideoCapture
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_startVideoCapture
(JNIEnv *, jobject owner)
{

	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->startVideoCapture();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    stopVideoCapture
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_stopVideoCapture
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->stopVideoCapture();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    startRecordVideoCapture
 * Signature: (Ljava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_startRecordVideoCapture
(JNIEnv *, jobject owner, jstring filename, jboolean saveToLibrary)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	String filenameString;
	filenameString = jni_env->GetStringUTFChars(filename, NULL);
	if (filenameString == NULL) {
		return;
	}

	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{

		coreMediaEnginePtr->get()->startRecordVideoCapture(filenameString, saveToLibrary);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    stopRecordVideoCapture
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_stopRecordVideoCapture
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		coreMediaEnginePtr->get()->stopRecordVideoCapture();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getVideoTransportStatistics
 * Signature: (Lcom/openpeer/javaapi/OPRtpRtcpStatistics;)I
 */
JNIEXPORT jint JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getVideoTransportStatistics
(JNIEnv *, jobject, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    getVoiceTransportStatistics
 * Signature: (Lcom/openpeer/javaapi/OPRtpRtcpStatistics;)I
 */
JNIEXPORT jint JNICALL Java_com_openpeer_javaapi_OPMediaEngine_getVoiceTransportStatistics
(JNIEnv *, jobject owner, jobject stat)
{

}

/*
 * Class:     com_openpeer_javaapi_OPMediaEngine
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPMediaEngine_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPMediaEngine");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IMediaEnginePtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (MediaEngineDelegateWrapperPtr*)delegatePointerValue;

		if(g_glChannelSurface != NULL)
		{
			jni_env->DeleteGlobalRef(g_glChannelSurface);
		}

		if(g_glCaptureSurface != NULL)
		{
			jni_env->DeleteGlobalRef(g_glCaptureSurface);
		}
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "Media engine releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "releaseCoreObjects Core object not deleted - already NULL!");
	}
}

/*
 * Class:     com_openpeer_javaapi_test_OPTestMediaEngine
 * Method:    startVoice
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_test_OPTestMediaEngine_startVoice
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		((internal::IMediaEngineForCallTransport*)(test::TestMediaEngine*)coreMediaEnginePtr->get())->startVoice();
	}
}

/*
 * Class:     com_openpeer_javaapi_test_OPTestMediaEngine
 * Method:    stopVoice
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_test_OPTestMediaEngine_stopVoice
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		((internal::IMediaEngineForCallTransport*)(test::TestMediaEngine*)coreMediaEnginePtr->get())->stopVoice();
	}
}

/*
 * Class:     com_openpeer_javaapi_test_OPTestMediaEngine
 * Method:    startVideoChannel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_test_OPTestMediaEngine_startVideoChannel
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		((internal::IMediaEngineForCallTransport*)(test::TestMediaEngine*)coreMediaEnginePtr->get())->startVideoChannel();
	}
}

/*
 * Class:     com_openpeer_javaapi_test_OPTestMediaEngine
 * Method:    stopVideoChannel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_test_OPTestMediaEngine_stopVideoChannel
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		((internal::IMediaEngineForCallTransport*)(test::TestMediaEngine*)coreMediaEnginePtr->get())->stopVideoChannel();
	}
}

/*
 * Class:     com_openpeer_javaapi_test_OPTestMediaEngine
 * Method:    setReceiverAddress
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_test_OPTestMediaEngine_setReceiverAddress
(JNIEnv *, jobject owner, jstring receiver_address)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	String addressString;
	addressString = jni_env->GetStringUTFChars(receiver_address, NULL);
	if (addressString == NULL) {
		return;
	}

	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		((test::TestMediaEngine*)coreMediaEnginePtr->get())->setReceiverAddress(addressString);
	}
}

/*
 * Class:     com_openpeer_javaapi_test_OPTestMediaEngine
 * Method:    getReceiverAddress
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_test_OPTestMediaEngine_getReceiverAddress
(JNIEnv *, jobject owner)
{
	jstring ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass mediaEngineClass = findClass("com/openpeer/javaapi/OPMediaEngine");
	jfieldID mediaEngineFid = jni_env->GetFieldID(mediaEngineClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, mediaEngineFid);

	IMediaEnginePtr* coreMediaEnginePtr = (IMediaEnginePtr*)pointerValue;

	if (coreMediaEnginePtr)
	{
		String address = ((test::TestMediaEngine*)coreMediaEnginePtr->get())->getReceiverAddress();
		ret = jni_env->NewStringUTF(address.c_str());
	}
	return ret;
}

#ifdef __cplusplus
}
#endif
