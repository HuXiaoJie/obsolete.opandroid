//#include "com_openpeer_javaapi_OPStackMessageQueue.h"
#include "openpeer/core/ICall.h"
#include "openpeer/core/ILogger.h"
#include "OpenPeerCoreManager.h"
#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/CallStates;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCall_toString__Lcom_openpeer_javaapi_CallStates_2
(JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/CallClosedReasons;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCall_toString__Lcom_openpeer_javaapi_CallClosedReasons_2
(JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    toDebugString
 * Signature: (Lcom/openpeer/javaapi/OPCall;Z)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCall_toDebugString
(JNIEnv *, jclass, jobject, jboolean)
{

}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    placeCall
 * Signature: (Lcom/openpeer/javaapi/OPConversationThread;Lcom/openpeer/javaapi/OPContact;ZZ)Lcom/openpeer/javaapi/OPCall;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_placeCall
(JNIEnv *, jclass, jobject conversationThread, jobject toContact, jboolean includeAudio, jboolean includeVideo)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	ICallPtr callPtr;

	if (conversationThread == NULL || toContact == NULL)
	{
		return object;
	}

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPConversationThread");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(conversationThread, fid);
	IConversationThreadPtr* coreConversationThreadPtr = (IConversationThreadPtr*)pointerValue;

	jclass contactClass = findClass("com/openpeer/javaapi/OPContact");
	jfieldID contactfid = jni_env->GetFieldID(contactClass, "nativeClassPointer", "J");
	jlong contactPointerValue = jni_env->GetLongField(toContact, contactfid);

	IContactPtr* contactPtr = (IContactPtr*)contactPointerValue;

	//	IConversationThreadPtr convThread = OpenPeerCoreManager::getConversationThreadFromList(conversationThread);
	//	IContactPtr contact = OpenPeerCoreManager::getContactFromList(toContact);
	if(coreConversationThreadPtr && contactPtr)
	{

		__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni", "Placing call...");
		callPtr = ICall::placeCall(*coreConversationThreadPtr, *contactPtr ,includeAudio, includeVideo);

	}

	if(callPtr)
	{
		if(jni_env)
		{
			ICallPtr* ptrToCall = new boost::shared_ptr<ICall>(callPtr);
			cls = findClass("com/openpeer/javaapi/OPCall");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong call = (jlong) ptrToCall;
			jni_env->SetLongField(object, fid, call);

			__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni",
					"CorePtr raw = %p, ptr as long = %Lu",callPtr.get(), call);

			//OpenPeerCoreManager::coreCallList.push_back(callPtr);

		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getStableID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPCall_getStableID
(JNIEnv *, jobject owner)
{
	jlong ret = 0;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		ret = coreCallPtr->get()->getID();
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getCallID
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCall_getCallID
(JNIEnv *env , jobject owner)
{
	jstring ret;
	JNIEnv *jni_env = 0;
	jni_env = getEnv();

	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	if (coreCallPtr)
	{
		ret = env->NewStringUTF(coreCallPtr->get()->getCallID().c_str());
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getConversationThread
 * Signature: ()Lcom/openpeer/javaapi/OPConversationThread;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getConversationThread
(JNIEnv *, jobject owner)
{
	jobject ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	IConversationThreadPtr convThreadPtr;
	if (coreCallPtr)
	{
		convThreadPtr = coreCallPtr->get()->getConversationThread();
		IConversationThreadPtr* ptrToConversationThread = new boost::shared_ptr<IConversationThread>(convThreadPtr);
		cls = findClass("com/openpeer/javaapi/OPConversationThread");
		jmethodID method = jni_env->GetMethodID(cls, "<init>", "()V");
		ret = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong convThread = (jlong) ptrToConversationThread;
		jni_env->SetLongField(ret, fid, convThread);
	}
	return ret;

}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getCaller
 * Signature: ()Lcom/openpeer/javaapi/OPContact;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getCaller
(JNIEnv *, jobject owner)
{
	jobject ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	IContactPtr contactPtr;
	if (coreCallPtr)
	{
		contactPtr = coreCallPtr->get()->getCaller();
		IContactPtr* ptrToContact = new boost::shared_ptr<IContact>(contactPtr);
		cls = findClass("com/openpeer/javaapi/OPContact");
		jmethodID method = jni_env->GetMethodID(cls, "<init>", "()V");
		ret = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong contact = (jlong) ptrToContact;
		jni_env->SetLongField(ret, fid, contact);

	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getCallee
 * Signature: ()Lcom/openpeer/javaapi/OPContact;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getCallee
(JNIEnv *, jobject owner)
{
	jobject ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	IContactPtr contactPtr;
	if (coreCallPtr)
	{
		contactPtr = coreCallPtr->get()->getCallee();
		IContactPtr* ptrToContact = new boost::shared_ptr<IContact>(contactPtr);
		cls = findClass("com/openpeer/javaapi/OPContact");
		jmethodID method = jni_env->GetMethodID(cls, "<init>", "()V");
		ret = jni_env->NewObject(cls, method);

		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong contact = (jlong) ptrToContact;
		jni_env->SetLongField(ret, fid, contact);
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    hasAudio
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPCall_hasAudio
(JNIEnv *, jobject owner)
{
	jboolean ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		ret = coreCallPtr->get()->hasAudio();
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    hasVideo
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPCall_hasVideo
(JNIEnv *, jobject owner)
{
	jboolean ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		ret = coreCallPtr->get()->hasVideo();
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getState
 * Signature: ()Lcom/openpeer/javaapi/CallStates;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getState
(JNIEnv *, jobject owner)
{
	jint state = 0;
	jobject ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		state = (jint) coreCallPtr->get()->getState();

		jni_env = getEnv();
		if(jni_env)
		{
			ret = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/CallStates", state);

		}
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getClosedReason
 * Signature: ()Lcom/openpeer/javaapi/CallClosedReasons;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getClosedReason
(JNIEnv *, jobject owner)
{
	jint reason = 0;
	jobject ret;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		reason = (jint) coreCallPtr->get()->getClosedReason();

		jni_env = getEnv();
		if(jni_env)
		{
			ret = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/CallClosedReason", reason);

		}
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getCreationTime
 * Signature: ()Landroid/text/format/Time;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getCreationTime
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;

	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	Time creationTime;
	if (coreCallPtr)
	{
		creationTime = coreCallPtr->get()->getcreationTime();

		jni_env = getEnv();
		if(jni_env)
		{
			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");

			//calculate and set Ring time
			zsLib::Duration creationTimeDuration = creationTime - time_t_epoch;
			object = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(object, timeSetMillisMethodID, creationTimeDuration.total_milliseconds());
		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getRingTime
 * Signature: ()Landroid/text/format/Time;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getRingTime
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	Time ringTime;
	if (coreCallPtr)
	{
		ringTime = coreCallPtr->get()->getRingTime();

		jni_env = getEnv();
		if(jni_env)
		{
			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");

			//calculate and set Ring Time
			zsLib::Duration ringTimeDuration = ringTime - time_t_epoch;
			object = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(object, timeSetMillisMethodID, ringTimeDuration.total_milliseconds());
		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getAnswerTime
 * Signature: ()Landroid/text/format/Time;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getAnswerTime
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	Time answerTime;
	if (coreCallPtr)
	{
		answerTime = coreCallPtr->get()->getAnswerTime();

		jni_env = getEnv();
		if(jni_env)
		{
			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");

			//calculate and set Answer Time
			zsLib::Duration answerTimeDuration = answerTime - time_t_epoch;
			object = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(object, timeSetMillisMethodID, answerTimeDuration.total_milliseconds());
		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    getClosedTime
 * Signature: ()Landroid/text/format/Time;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCall_getClosedTime
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;

	Time closedTime;

	if (coreCallPtr)
	{
		closedTime = coreCallPtr->get()->getClosedTime();

		jni_env = getEnv();
		if(jni_env)
		{
			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");

			//calculate and set Answer Time
			zsLib::Duration closedTimeDuration = closedTime - time_t_epoch;
			object = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(object, timeSetMillisMethodID, closedTimeDuration.total_milliseconds());
		}
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    ring
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCall_ring
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{;
	coreCallPtr->get()->ring();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    answer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCall_answer
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		coreCallPtr->get()->answer();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    hold
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCall_hold
(JNIEnv *, jobject owner, jboolean hold)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		coreCallPtr->get()->hold(hold);
	}
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    hangup
 * Signature: (Lcom/openpeer/javaapi/CallClosedReasons;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCall_hangup
(JNIEnv *, jobject owner, jobject callClosedReasonObj)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass cls = findClass("com/openpeer/javaapi/OPCall");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	ICallPtr* coreCallPtr = (ICallPtr*)pointerValue;
	if (coreCallPtr)
	{
		jni_env = getEnv();

		cls = findClass("com/openpeer/javaapi/CallClosedReasons");
		if(jni_env->IsInstanceOf(callClosedReasonObj, cls) == JNI_TRUE)
		{
			jint intValue = OpenPeerCoreManager::getIntValueFromEnumObject(callClosedReasonObj, "com/openpeer/javaapi/CallClosedReasons");

			ICall::CallClosedReasons reason = (ICall::CallClosedReasons)intValue;
			coreCallPtr->get()->hangup(reason);
			//todo Catch call ended state, and reset and remove call from OpenPeerCoreManager call vector
		}
	}
}

/*
 * Class:     com_openpeer_javaapi_OPCall
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCall_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPCall");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (ICallPtr*)pointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "releaseCoreObjects Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "releaseCoreObjects Core object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
