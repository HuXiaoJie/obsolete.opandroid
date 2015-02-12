#include "OpenPeerCoreManager.h"
#include "globals.h"
#include "openpeer/core/IHelper.h"
#include "openpeer/core/IPushMessaging.h"
#include <android/log.h>;

IStackMessageQueuePtr OpenPeerCoreManager::queuePtr = IStackMessageQueuePtr();
ISettingsPtr OpenPeerCoreManager::settingsPtr = ISettingsPtr();
ICachePtr OpenPeerCoreManager::cachePtr = ICachePtr();


jobject OpenPeerCoreManager::getJavaEnumObject(String enumClassName, jint index)
{
	JNIEnv *jni_env = getEnv();

	jclass cls = findClass(enumClassName.c_str());
	jmethodID valuesMethodID = jni_env->GetStaticMethodID(cls, "values", ("()[L" + enumClassName  + ";").c_str());
	jobjectArray valuesArray = (jobjectArray)jni_env->CallStaticObjectMethod(cls, valuesMethodID);
	jobject returnObj = jni_env->GetObjectArrayElement(valuesArray, index);
	jni_env->DeleteLocalRef(valuesArray);
	jni_env->DeleteLocalRef(cls);

	return returnObj;
}

jint OpenPeerCoreManager::getIntValueFromEnumObject(jobject enumObject, String enumClassName)
{
	JNIEnv *jni_env = getEnv();

	jclass cls = findClass(enumClassName.c_str());
	jmethodID ordinalMethodID = jni_env->GetMethodID(cls, "ordinal", "()I");
	jint intValue = (jint) jni_env->CallIntMethod(enumObject, ordinalMethodID);
	jni_env->DeleteLocalRef(cls);

	return intValue;
}

String OpenPeerCoreManager::getObjectClassName (jobject delegate)
{
	String ret;
	JNIEnv *env = getEnv();

	jclass cls = env->GetObjectClass(delegate);

	// First get the class object
	jmethodID mid = env->GetMethodID(cls, "getClass", "()Ljava/lang/Class;");
	jobject clsObj = env->CallObjectMethod(delegate, mid);

	// Now get the class object's class descriptor
	cls = env->GetObjectClass(clsObj);

	// Find the getName() method on the class object
	mid = env->GetMethodID(cls, "getName", "()Ljava/lang/String;");

	// Call the getName() to get a jstring object back
	jstring strObj = (jstring)env->CallObjectMethod(clsObj, mid);

	// Now get the c string from the java jstring object
	ret = String(env->GetStringUTFChars(strObj, NULL));

	env->DeleteLocalRef(clsObj);
	env->DeleteLocalRef(cls);
	// Release the memory pinned char array
	env->ReleaseStringUTFChars(strObj, ret);
	env->DeleteLocalRef(strObj);
	return ret;
}

zsLib::Time OpenPeerCoreManager::convertTimeFromJava(jobject timeObject)
{
	JNIEnv* jni_env = getEnv();
	jclass timeCls = findClass("android/text/format/Time");
	jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "toMillis","(Z)J");
	jlong longValue = jni_env->CallLongMethod(timeObject, timeMethodID, false);

	Time t = std::chrono::time_point<std::chrono::system_clock>(std::chrono::milliseconds(longValue));
	return t;
}

jobject OpenPeerCoreManager::convertTimeFromCore(zsLib::Time coreTime)
{
	JNIEnv* jni_env = getEnv();
	//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
	jclass timeCls = findClass("android/text/format/Time");
	jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
	jobject object = jni_env->NewObject(timeCls, timeMethodID);

	if (Time() != coreTime){
		//calculate and set java time
		long milliseconds_since_epoch = coreTime.time_since_epoch() / std::chrono::milliseconds(1);
		jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");
		jni_env->CallVoidMethod(object, timeSetMillisMethodID, milliseconds_since_epoch);
	}

	return object;
}

jobject OpenPeerCoreManager::convertSecondsFromCore(zsLib::Seconds coreSeconds)
{
	JNIEnv* jni_env = getEnv();
	//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
	jclass timeCls = findClass("android/text/format/Time");
	jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
	jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");

	//calculate and set java time
	std::chrono::milliseconds ms = std::chrono::duration_cast<std::chrono::milliseconds> (coreSeconds);
	jobject object = jni_env->NewObject(timeCls, timeMethodID);
	jni_env->CallVoidMethod(object, timeSetMillisMethodID, ms.count());

	return object;
}

zsLib::Seconds OpenPeerCoreManager::convertSecondsFromJava(jobject timeObject)
{
	JNIEnv* jni_env = getEnv();
	jclass timeCls = findClass("android/text/format/Time");
	jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "toMillis","(Z)J");
	jlong longValue = jni_env->CallLongMethod(timeObject, timeMethodID, false);

	Seconds s = std::chrono::duration_cast<std::chrono::seconds> (std::chrono::milliseconds(longValue));
	return s;
}

void OpenPeerCoreManager::fillJavaTokenFromCoreObject(jobject javaToken, IIdentity::Token coreToken)
{
	JNIEnv *jni_env = 0;
	jni_env = getEnv();

	jclass cls = findClass("com/openpeer/javaapi/OPToken");

	jfieldID fid = jni_env->GetFieldID(cls, "mID", "Ljava/lang/String;");
	jstring id = jni_env->NewStringUTF(coreToken.mID.c_str());
	jni_env->SetObjectField(javaToken, fid, id);

	jfieldID fSecret = jni_env->GetFieldID(cls, "mSecret", "Ljava/lang/String;");
	jstring secret = jni_env->NewStringUTF(coreToken.mSecret.c_str());
	jni_env->SetObjectField(javaToken, fSecret, secret);

	jfieldID fSecretEncrypted = jni_env->GetFieldID(cls, "mSecretEncrypted", "Ljava/lang/String;");
	jstring secretEncrypted = jni_env->NewStringUTF(coreToken.mSecretEncrypted.c_str());
	jni_env->SetObjectField(javaToken, fSecretEncrypted, secretEncrypted);

	//TIME
	jfieldID fExpires = jni_env->GetFieldID(cls, "mExpires", "Landroid/text/format/Time;");
	//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
	jni_env->SetObjectField(javaToken, fExpires, OpenPeerCoreManager::convertTimeFromCore(coreToken.mExpires));

	jfieldID fProof = jni_env->GetFieldID(cls, "mProof", "Ljava/lang/String;");
	jstring proof = jni_env->NewStringUTF(coreToken.mProof.c_str());
	jni_env->SetObjectField(javaToken, fProof, proof);

	jfieldID fNonce = jni_env->GetFieldID(cls, "mNonce", "Ljava/lang/String;");
	jstring nonce = jni_env->NewStringUTF(coreToken.mNonce.c_str());
	jni_env->SetObjectField(javaToken, fNonce, nonce);

	jfieldID fResource = jni_env->GetFieldID(cls, "mResource", "Ljava/lang/String;");
	jstring resource = jni_env->NewStringUTF(coreToken.mResource.c_str());
	jni_env->SetObjectField(javaToken, fResource, resource);

}

IPushMessaging::PushInfo OpenPeerCoreManager::pushInfoToCore(jobject javaPushInfo)
{
	IPushMessaging::PushInfo returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushInfoToCore called");

	jni_env = getEnv();
	if (jni_env)
	{
		//mServiceType
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPPushInfo");

		jmethodID getServiceTypeMethodID = jni_env->GetMethodID( javaItemClass, "getServiceType", "()Ljava/lang/String;" );
		jstring serviceType = (jstring)jni_env->CallObjectMethod(javaPushInfo, getServiceTypeMethodID);
		returnObject.mServiceType = String(jni_env->GetStringUTFChars(serviceType, NULL));
		jni_env->ReleaseStringUTFChars(serviceType, returnObject.mServiceType);
		jni_env->DeleteLocalRef(serviceType);

		//mValues
		jmethodID getValuesMethodID = jni_env->GetMethodID( javaItemClass, "getValues", "()Lcom/openpeer/javaapi/OPElement;" );
		jclass elementCls = findClass("com/openpeer/javaapi/OPElement");
		jfieldID elementFid = jni_env->GetFieldID(elementCls, "nativeClassPointer", "J");
		jobject elementValuesObject = jni_env->CallObjectMethod(javaPushInfo, getValuesMethodID);
		jlong valuesPointerValue = jni_env->GetLongField(elementValuesObject, elementFid);

		ElementPtr* coreValuesElementPtr = (ElementPtr*)valuesPointerValue;
		returnObject.mValues = *coreValuesElementPtr;

		//mCustom
		jmethodID getCustomMethodID = jni_env->GetMethodID( javaItemClass, "getCustom", "()Lcom/openpeer/javaapi/OPElement;" );
		jobject elementCustomObject = jni_env->CallObjectMethod(javaPushInfo, getCustomMethodID);
		jlong customPointerValue = jni_env->GetLongField(elementCustomObject, elementFid);

		ElementPtr* coreCustomElementPtr = (ElementPtr*)customPointerValue;
		returnObject.mCustom = *coreCustomElementPtr;
	}

	return returnObject;
}

IPushPresence::PushInfo OpenPeerCoreManager::presencePushInfoToCore(jobject javaPushInfo)
{
	IPushPresence::PushInfo returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager presencePushInfoToCore called");

	jni_env = getEnv();
	if (jni_env)
	{
		//mServiceType
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPPushPresencePushInfo");

		jmethodID getServiceTypeMethodID = jni_env->GetMethodID( javaItemClass, "getServiceType", "()Ljava/lang/String;" );
		jstring serviceType = (jstring)jni_env->CallObjectMethod(javaPushInfo, getServiceTypeMethodID);
		returnObject.mServiceType = String(jni_env->GetStringUTFChars(serviceType, NULL));
		jni_env->ReleaseStringUTFChars(serviceType, returnObject.mServiceType);
		jni_env->DeleteLocalRef(serviceType);

		//mValues
		jmethodID getValuesMethodID = jni_env->GetMethodID( javaItemClass, "getValues", "()Lcom/openpeer/javaapi/OPElement;" );
		jclass elementCls = findClass("com/openpeer/javaapi/OPElement");
		jfieldID elementFid = jni_env->GetFieldID(elementCls, "nativeClassPointer", "J");
		jobject elementValuesObject = jni_env->CallObjectMethod(javaPushInfo, getValuesMethodID);
		jlong valuesPointerValue = jni_env->GetLongField(elementValuesObject, elementFid);

		ElementPtr* coreValuesElementPtr = (ElementPtr*)valuesPointerValue;
		returnObject.mValues = *coreValuesElementPtr;

		//mCustom
		jmethodID getCustomMethodID = jni_env->GetMethodID( javaItemClass, "getCustom", "()Lcom/openpeer/javaapi/OPElement;" );
		jobject elementCustomObject = jni_env->CallObjectMethod(javaPushInfo, getCustomMethodID);
		jlong customPointerValue = jni_env->GetLongField(elementCustomObject, elementFid);

		ElementPtr* coreCustomElementPtr = (ElementPtr*)customPointerValue;
		returnObject.mCustom = *coreCustomElementPtr;
	}

	return returnObject;
}

jobject OpenPeerCoreManager::pushInfoToJava(IPushMessaging::PushInfo corePushInfo)
{
	jobject returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushInfoToJava called");

	jni_env = getEnv();

	if(jni_env)
	{
		//mServiceType
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPPushInfo");
		jmethodID javaItemConstructorMethodID = jni_env->GetMethodID(javaItemClass, "<init>", "()V");
		returnObject = jni_env->NewObject(javaItemClass, javaItemConstructorMethodID);

		jmethodID setServiceTypeMethodID = jni_env->GetMethodID( javaItemClass, "setServiceType", "(Ljava/lang/String;)V" );
		jstring serviceType = jni_env->NewStringUTF(corePushInfo.mServiceType.c_str());
		jni_env->CallVoidMethod(returnObject, setServiceTypeMethodID, serviceType);

		//mValues
		ElementPtr coreEl = corePushInfo.mValues;
		ElementPtr* ptrToElement = new std::shared_ptr<Element>(coreEl);
		jclass elementCls = findClass("com/openpeer/javaapi/OPElement");
		jmethodID elementMethod = jni_env->GetMethodID(elementCls, "<init>", "()V");
		jobject elementValuesObject = jni_env->NewObject(elementCls, elementMethod);

		jfieldID fid = jni_env->GetFieldID(elementCls, "nativeClassPointer", "J");
		jlong element = (jlong) ptrToElement;
		jni_env->SetLongField(elementValuesObject, fid, element);

		jmethodID setValuesMethodID = jni_env->GetMethodID( javaItemClass, "setValues", "(Lcom/openpeer/javaapi/OPElement;)V" );
		jni_env->CallVoidMethod(returnObject, setValuesMethodID, elementValuesObject);

		//mCustom
		ElementPtr coreCustomEl = corePushInfo.mCustom;
		ElementPtr* ptrToCustomElement = new std::shared_ptr<Element>(coreCustomEl);
		jobject elementCustomObject = jni_env->NewObject(elementCls, elementMethod);
		jlong elementCustom = (jlong) ptrToCustomElement;
		jni_env->SetLongField(elementCustomObject, fid, elementCustom);

		jmethodID setCustomMethodID = jni_env->GetMethodID( javaItemClass, "setCustom", "(Lcom/openpeer/javaapi/OPElement;)V" );
		jni_env->CallVoidMethod(returnObject, setCustomMethodID, elementCustomObject);

	}

	return returnObject;
}


//push messaging helper methods
IPushMessaging::PushInfoList OpenPeerCoreManager::pushInfoListToCore(jobject javaPushInfoList)
{
	IPushMessaging::PushInfoList returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushInfoListToCore called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( javaPushInfoList, sizeMethodID );

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get Contact object by index.
			jobject pushInfoObject = jni_env->CallObjectMethod( javaPushInfoList, listGetMethodID, i );
			IPushMessaging::PushInfo corePushInfo = pushInfoToCore(pushInfoObject);
			//add core contacts to list for removal
			returnListObject.push_front(corePushInfo);
		}
	}
	return returnListObject;
}
jobject OpenPeerCoreManager::pushInfoListToJava(IPushMessaging::PushInfoList corePushInfoList)
{
	jobject returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushInfoListToJava called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);

		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		//fill java list
		for(IPushMessaging::PushInfoList::iterator coreListIter = corePushInfoList.begin();
				coreListIter != corePushInfoList.end(); coreListIter++)
		{
			jobject javaItemObject = pushInfoToJava(*coreListIter);
			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , javaItemObject);
			jni_env->DeleteLocalRef(javaItemObject);
		}
	}
	return returnListObject;
}

IPushMessaging::PushStateContactDetail OpenPeerCoreManager::pushStateContactDetailToCore(jobject javaPushStateContactDetail)
{
	IPushMessaging::PushStateContactDetail returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushStateContactDetailToCore called");

	jni_env = getEnv();
	if (jni_env)
	{
		//mRemotePeer
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPPushStateContactDetail");

		jmethodID getRemotePeerMethodID = jni_env->GetMethodID( javaItemClass, "getRemotePeer", "()Lcom/openpeer/javaapi/OPContact;" );
		jobject remotePeer = jni_env->CallObjectMethod(javaPushStateContactDetail, getRemotePeerMethodID);
		jclass contactCls = findClass("com/openpeer/javaapi/OPContact");
		jfieldID contactFid = jni_env->GetFieldID(contactCls, "nativeClassPointer", "J");
		jlong remotePeerPointerValue = jni_env->GetLongField(remotePeer, contactFid);
		IContactPtr* coreRemotePeerPtr = (IContactPtr*)remotePeerPointerValue;
		returnObject.mRemotePeer = *coreRemotePeerPtr;

		//mErrorCode
		jmethodID getErrorCodeMethodID = jni_env->GetMethodID( javaItemClass, "getErrorCode", "()I" );
		jint errorCode = jni_env->CallIntMethod(javaPushStateContactDetail, getErrorCodeMethodID);
		returnObject.mErrorCode = (int) errorCode;

		//mErrorReason
		jmethodID getErrorReasonMethodID = jni_env->GetMethodID( javaItemClass, "getErrorReason", "()Ljava/lang/String;" );
		jstring errorReason = (jstring) jni_env->CallObjectMethod(javaPushStateContactDetail, getErrorReasonMethodID);
		returnObject.mErrorReason = String(jni_env->GetStringUTFChars(errorReason, NULL));
		jni_env->ReleaseStringUTFChars(errorReason, returnObject.mErrorReason);
		jni_env->DeleteLocalRef(errorReason);
	}

	return returnObject;
}
jobject OpenPeerCoreManager::pushStateContactDetailToJava(IPushMessaging::PushStateContactDetail corePushStateContactDetail)
{
	jobject returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushStateContactDetailToCore called");

	jni_env = getEnv();

	if(jni_env)
	{
		jclass javaStateContactDetailClass = findClass("com/openpeer/javaapi/OPPushStateContactDetail");
		jmethodID javaStateContactDetailConstructorMethodID = jni_env->GetMethodID(javaStateContactDetailClass, "<init>", "()V");
		returnObject = jni_env->NewObject(javaStateContactDetailClass, javaStateContactDetailConstructorMethodID);

		//mRemotePeer
		IContactPtr coreRemotePeer = corePushStateContactDetail.mRemotePeer;
		IContactPtr* ptrToRemotePeer = new std::shared_ptr<IContact>(coreRemotePeer);
		jclass contactCls = findClass("com/openpeer/javaapi/OPContact");
		jmethodID contactMethod = jni_env->GetMethodID(contactCls, "<init>", "()V");
		jobject remotePeerObject = jni_env->NewObject(contactCls, contactMethod);

		jfieldID fid = jni_env->GetFieldID(contactCls, "nativeClassPointer", "J");
		jlong remotePeer = (jlong) ptrToRemotePeer;
		jni_env->SetLongField(remotePeerObject, fid, remotePeer);

		jmethodID setRemotePeerMethodID = jni_env->GetMethodID( javaStateContactDetailClass, "setRemotePeer", "(Lcom/openpeer/javaapi/OPContact;)V" );
		jni_env->CallVoidMethod(returnObject, setRemotePeerMethodID, remotePeerObject);

		//mErrorCode
		jint errorCode = (jint) corePushStateContactDetail.mErrorCode;
		jmethodID setErrorCodeMethodID = jni_env->GetMethodID( javaStateContactDetailClass, "setErrorCode", "(I)V" );
		jni_env->CallVoidMethod(returnObject, setErrorCodeMethodID, errorCode);

		//mErrorReason
		jmethodID setErrorReasonMethodID = jni_env->GetMethodID( javaStateContactDetailClass, "setErrorReason", "(Ljava/lang/String;)V" );
		jstring errorReason = jni_env->NewStringUTF(corePushStateContactDetail.mErrorReason.c_str());
		jni_env->CallVoidMethod(returnObject, setErrorReasonMethodID, errorReason);

	}

	return returnObject;
}

IPushMessaging::PushStateContactDetailList OpenPeerCoreManager::pushStateContactDetailListToCore(jobject javaPushStateContactDetailList)
{
	IPushMessaging::PushStateContactDetailList returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushStateContactDetailListToCore called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( javaPushStateContactDetailList, sizeMethodID );

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get Contact object by index.
			jobject pushStateContactDetailObject = jni_env->CallObjectMethod( javaPushStateContactDetailList, listGetMethodID, i );
			IPushMessaging::PushStateContactDetail corePushStateContactDetail = pushStateContactDetailToCore(pushStateContactDetailObject);
			//add core contacts to list for removal
			returnListObject.push_front(corePushStateContactDetail);
		}
	}
	return returnListObject;
}
jobject OpenPeerCoreManager::pushStateContactDetailListToJava(IPushMessaging::PushStateContactDetailList corePushStateContactDetailList)
{
	jobject returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushStateContactDetailListToJava called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);

		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		//fill java list
		for(IPushMessaging::PushStateContactDetailList::iterator coreListIter = corePushStateContactDetailList.begin();
				coreListIter != corePushStateContactDetailList.end(); coreListIter++)
		{
			jobject javaItemObject = pushStateContactDetailToJava(*coreListIter);
			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , javaItemObject);
			jni_env->DeleteLocalRef(javaItemObject);
		}
	}
	return returnListObject;
}

IPushMessaging::PushStateDetailMap OpenPeerCoreManager::pushStateDetailMapToCore(jobject javaPushStateDetailMap)
{
	IPushMessaging::PushStateDetailMap returnMap;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushStateDetailMapToCore called");

	jni_env = getEnv();
	if(jni_env)
	{
		jclass hashMapClass = findClass("java/util/HashMap");
		// size method fetch
		jmethodID sizeMethodID = jni_env->GetMethodID(hashMapClass, "size", "()I");


		// Get the Set Class
		jclass setClass = findClass("java/util/Set");
		// Get the "Iterator" class
		jclass iteratorClass = findClass("java/util/Iterator");
		// Get the Map.Entry class
		jclass mapEntryClass = findClass("java/util/Map/Entry");

		// Get link to Method "entrySet"
		jmethodID entrySetMethod = jni_env->GetMethodID(hashMapClass, "entrySet", "()Ljava/util/Set;");
		// Get link to Method "iterator"
		jmethodID iteratorMethod = jni_env->GetMethodID(setClass, "iterator", "()Ljava/util/Iterator;");
		// Get link to Method "hasNext"
		jmethodID hasNextMethod = jni_env->GetMethodID(iteratorClass, "hasNext", "()Z");
		// Get link to Method "next"
		jmethodID nextMethod = jni_env->GetMethodID(iteratorClass, "next", "()Ljava/util/Map/Entry;");
		// Get link to GetKey/GetValue methods
		jmethodID getKeyMethod = jni_env->GetMethodID(mapEntryClass, "getKey", "()Ljava/lang/Object");
		jmethodID getValueMethod = jni_env->GetMethodID(mapEntryClass, "getValue", "()Ljava/lang/Object");

		int mapItemsCount = (int)jni_env->CallIntMethod( javaPushStateDetailMap, sizeMethodID );

		jboolean bHasNext = false;
		if (mapItemsCount > 0)
		{
			bHasNext = true;
		}
		// Invoke the "entrySet" method on the HashMap object
		jobject entrySetObject = jni_env->CallObjectMethod(javaPushStateDetailMap, entrySetMethod);
		// Invoke the "iterator" method on the jobject_of_entryset variable of type Set
		jobject iteratorObject = jni_env->CallObjectMethod(entrySetObject, iteratorMethod);
		while(bHasNext)
		{
			//Gewt key and value from map entry iterator
			jobject keyObject = jni_env->CallObjectMethod(iteratorObject, getKeyMethod);
			jobject valueObject = jni_env->CallObjectMethod(iteratorObject, getValueMethod);

			//Pack key and value to C++ class
			std::pair<IPushMessaging::PushStates, IPushMessaging::PushStateContactDetailList> coreEntry;
			IPushMessaging::PushStates state = (IPushMessaging::PushStates) getIntValueFromEnumObject(keyObject, "com/openpeer/javaapi/PushStates");
			IPushMessaging::PushStateContactDetailList coreList = pushStateContactDetailListToCore(valueObject);
			coreEntry = std::make_pair(state, coreList);

			returnMap.insert(coreEntry);

			// Invoke - Get the value hasNextMethod
			bHasNext = jni_env->CallBooleanMethod(iteratorObject, hasNextMethod);
			//Invoke next() method to obtain entry
			iteratorObject = jni_env->CallObjectMethod(iteratorObject, nextMethod);

		}


	}
	return returnMap;

}
jobject OpenPeerCoreManager::pushStateDetailMapToJava(IPushMessaging::PushStateDetailMap coreMap)
{

	jobject returnMapObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushStateDetailMapToJava called");

	jni_env = getEnv();
	if(jni_env)
	{
		jclass hashMapClass = findClass("java/util/HashMap");
		jmethodID hashMapConstructorMethodID = jni_env->GetMethodID(hashMapClass, "<init>", "()V");
		jobject returnMapObject = jni_env->NewObject(hashMapClass, hashMapConstructorMethodID);

		jmethodID putMethod = jni_env->GetMethodID(
				hashMapClass,
				"put",
				"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
		);

		for( IPushMessaging::PushStateDetailMap::iterator it = coreMap.begin(); it != coreMap.end(); ++it )
		{
			//key == PushStates
			jobject key = getJavaEnumObject("com/openpeer/javaapi/PushStates", (int)(*it).first );

			//value == List<OPPushStateContactDetail>
			jobject value = pushStateContactDetailListToJava( (*it).second );

			jni_env->CallVoidMethod(
					returnMapObject,
					putMethod,
					key,
					value
			);
		}
	}

	return returnMapObject;
}

IPushMessaging::PushMessage OpenPeerCoreManager::pushMessageToCore(jobject javaPushMessage)
{
	IPushMessaging::PushMessage returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushMessageToCore called");

	jni_env = getEnv();
	if (jni_env)
	{
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPPushMessage");

		//mMessageID
		jmethodID getMessageIDMethodID = jni_env->GetMethodID( javaItemClass, "getMessageID", "()Ljava/lang/String;" );
		jstring messageID = (jstring)jni_env->CallObjectMethod(javaPushMessage, getMessageIDMethodID);
		returnObject.mMessageID = String(jni_env->GetStringUTFChars(messageID, NULL));
		jni_env->ReleaseStringUTFChars(messageID, returnObject.mMessageID);
		jni_env->DeleteLocalRef(messageID);

		//mMimeType
		jmethodID getMimeTypeMethodID = jni_env->GetMethodID( javaItemClass, "getMimeType", "()Ljava/lang/String;" );
		jstring mimeType = (jstring)jni_env->CallObjectMethod(javaPushMessage, getMimeTypeMethodID);
		returnObject.mMimeType = String(jni_env->GetStringUTFChars(mimeType, NULL));
		jni_env->ReleaseStringUTFChars(mimeType, returnObject.mMimeType);
		jni_env->DeleteLocalRef(mimeType);

		//mFullMessage
		jmethodID getFullMessageMethodID = jni_env->GetMethodID( javaItemClass, "getFullMessage", "()Ljava/lang/String;" );
		jstring fullMessage = (jstring)jni_env->CallObjectMethod(javaPushMessage, getFullMessageMethodID);
		returnObject.mFullMessage = String(jni_env->GetStringUTFChars(fullMessage, NULL));
		jni_env->ReleaseStringUTFChars(fullMessage, returnObject.mFullMessage);
		jni_env->DeleteLocalRef(fullMessage);

		//mRawFullMessage
		jmethodID getRawFullMessageMethodID = jni_env->GetMethodID( javaItemClass, "getRawFullMessage", "()[B" );
		jbyteArray rawFullMessage = (jbyteArray) jni_env->CallObjectMethod(javaPushMessage, getRawFullMessageMethodID);
		jbyte* rawFullMessageBytes = jni_env->GetByteArrayElements(rawFullMessage,NULL);
		jsize rawFullMessageSize = jni_env->GetArrayLength(rawFullMessage);
		returnObject.mRawFullMessage = IHelper::convert((BYTE*)rawFullMessageBytes, rawFullMessageSize);
		jni_env->ReleaseByteArrayElements(rawFullMessage,rawFullMessageBytes,0);

		//mPushType
		jmethodID getPushTypeMethodID = jni_env->GetMethodID( javaItemClass, "getPushType", "()Ljava/lang/String;" );
		jstring pushType = (jstring)jni_env->CallObjectMethod(javaPushMessage, getPushTypeMethodID);
		returnObject.mPushType = String(jni_env->GetStringUTFChars(pushType, NULL));
		jni_env->ReleaseStringUTFChars(pushType, returnObject.mPushType);
		jni_env->DeleteLocalRef(pushType);

		//mPushInfos
		jmethodID getPushInfosMethodID = jni_env->GetMethodID( javaItemClass, "getPushInfos", "()Ljava/util/List;" );
		jobject pushInfos = jni_env->CallObjectMethod(javaPushMessage, getPushInfosMethodID);
		returnObject.mPushInfos = pushInfoListToCore(pushInfos);

		//mSent
		jmethodID getSentMethodID = jni_env->GetMethodID( javaItemClass, "getSent", "()Landroid/text/format/Time;" );
		jobject sent = jni_env->CallObjectMethod(javaPushMessage, getSentMethodID);
		returnObject.mSent = OpenPeerCoreManager::convertTimeFromJava(sent);

		//mExpires
		jmethodID getExpiresMethodID = jni_env->GetMethodID( javaItemClass, "getExpires", "()Landroid/text/format/Time;" );
		jobject expires = jni_env->CallObjectMethod(javaPushMessage, getExpiresMethodID);
		returnObject.mExpires = OpenPeerCoreManager::convertTimeFromJava(expires);

		//mFrom
		jmethodID getFromMethodID = jni_env->GetMethodID( javaItemClass, "getFrom", "()Lcom/openpeer/javaapi/OPContact;" );
		jobject from = jni_env->CallObjectMethod(javaPushMessage, getFromMethodID);
		jclass contactCls = findClass("com/openpeer/javaapi/OPContact");
		jfieldID contactFid = jni_env->GetFieldID(contactCls, "nativeClassPointer", "J");
		jlong fromPointerValue = jni_env->GetLongField(from, contactFid);
		IContactPtr* coreFromPtr = (IContactPtr*)fromPointerValue;
		returnObject.mFrom = *coreFromPtr;

		//mPushStateDetails
		jmethodID getPushStateDetailsMethodID = jni_env->GetMethodID( javaItemClass, "getPushStateDetails", "()Ljava/util/Map;" );
		jobject pushStateDetails = jni_env->CallObjectMethod(javaPushMessage, getPushStateDetailsMethodID);
		returnObject.mPushStateDetails = pushStateDetailMapToCore(pushStateDetails);
	}

	return returnObject;
}
jobject OpenPeerCoreManager::pushMessageToJava(IPushMessaging::PushMessage corePushMessage)
{
	jobject returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushMessageToJava called");

	jni_env = getEnv();

	if(jni_env)
	{

		jclass javaItemClass = findClass("com/openpeer/javaapi/OPPushInfo");
		jmethodID javaItemConstructorMethodID = jni_env->GetMethodID(javaItemClass, "<init>", "()V");
		returnObject = jni_env->NewObject(javaItemClass, javaItemConstructorMethodID);

		//mMessageID
		jmethodID setMessageIDMethodID = jni_env->GetMethodID( javaItemClass, "setMessageID", "(Ljava/lang/String;)V" );
		jstring messageID = jni_env->NewStringUTF(corePushMessage.mMessageID.c_str());
		jni_env->CallVoidMethod(returnObject, setMessageIDMethodID, messageID);

		//mMimeType
		jmethodID setMimeTypeMethodID = jni_env->GetMethodID( javaItemClass, "setMimeType", "(Ljava/lang/String;)V" );
		jstring mimeType = jni_env->NewStringUTF(corePushMessage.mMimeType.c_str());
		jni_env->CallVoidMethod(returnObject, setMimeTypeMethodID, mimeType);

		//mFullMessage
		jmethodID setFullMessageMethodID = jni_env->GetMethodID( javaItemClass, "setFullMessage", "(Ljava/lang/String;)V" );
		jstring fullMessage = jni_env->NewStringUTF(corePushMessage.mFullMessage.c_str());
		jni_env->CallVoidMethod(returnObject, setFullMessageMethodID, fullMessage);

		//mRawFullMessage
		jmethodID setRawFullMessageMethodID = jni_env->GetMethodID( javaItemClass, "setRawFullMessage", "([B)V" );
		jbyteArray rawFullMessage = jni_env->NewByteArray(corePushMessage.mRawFullMessage->SizeInBytes());
		jni_env->SetByteArrayRegion(rawFullMessage, (int)0, (int)corePushMessage.mRawFullMessage->SizeInBytes(), (const signed char *)corePushMessage.mRawFullMessage->data());
		jni_env->CallVoidMethod(returnObject, setRawFullMessageMethodID, rawFullMessage);

		//mPushType
		jmethodID setPushTypeMethodID = jni_env->GetMethodID( javaItemClass, "setPushType", "(Ljava/lang/String;)V" );
		jstring pushType = jni_env->NewStringUTF(corePushMessage.mPushType.c_str());
		jni_env->CallVoidMethod(returnObject, setFullMessageMethodID, pushType);

		//mPushInfos
		jmethodID setPushInfosMethodID = jni_env->GetMethodID( javaItemClass, "setPushInfos", "(Ljava/util/List;)V" );
		jobject pushInfos = pushInfoListToJava(corePushMessage.mPushInfos);
		jni_env->CallVoidMethod(returnObject, setPushInfosMethodID, pushInfos);

		//mSent
		jmethodID setSentMethodID = jni_env->GetMethodID( javaItemClass, "setSent", "(Landroid/text/format/Time;)V" );
		jni_env->CallVoidMethod(returnObject, setSentMethodID, OpenPeerCoreManager::convertTimeFromCore(corePushMessage.mSent));

		//mExpires
		jmethodID setExpiresMethodID = jni_env->GetMethodID( javaItemClass, "setExpires", "(Landroid/text/format/Time;)V" );
		jni_env->CallVoidMethod(returnObject, setExpiresMethodID, OpenPeerCoreManager::convertTimeFromCore(corePushMessage.mExpires));

		//mFrom
		IContactPtr coreFrom = corePushMessage.mFrom;
		IContactPtr* ptrToFrom = new std::shared_ptr<IContact>(coreFrom);
		jclass contactCls = findClass("com/openpeer/javaapi/OPContact");
		jmethodID contactMethod = jni_env->GetMethodID(contactCls, "<init>", "()V");
		jobject fromObject = jni_env->NewObject(contactCls, contactMethod);

		jfieldID fid = jni_env->GetFieldID(contactCls, "nativeClassPointer", "J");
		jlong from = (jlong) ptrToFrom;
		jni_env->SetLongField(fromObject, fid, from);

		jmethodID setFromMethodID = jni_env->GetMethodID( javaItemClass, "setFrom", "(Lcom/openpeer/javaapi/OPContact;)V" );
		jni_env->CallVoidMethod(returnObject, setFromMethodID, fromObject);

		//mPushStateDetails
		jmethodID setPushStateDetailsMethodID = jni_env->GetMethodID( javaItemClass, "setPushStateDetails", "(Ljava/util/Map;)V" );
		jobject pushStateDetails = pushStateDetailMapToJava(corePushMessage.mPushStateDetails);
		jni_env->CallVoidMethod(returnObject, setPushStateDetailsMethodID, pushStateDetails);

		//nativeClassPointer
		IPushMessaging::PushMessagePtr corePushMessagePtr = IPushMessaging::PushMessagePtr(&corePushMessage);
		IPushMessaging::PushMessagePtr* ptrToPushMessage = new std::shared_ptr<IPushMessaging::PushMessage>(corePushMessagePtr);

		jfieldID nativePtrFid = jni_env->GetFieldID(javaItemClass, "nativeClassPointer", "J");
		jlong pushMessage = (jlong) ptrToPushMessage;
		jni_env->SetLongField(returnObject, nativePtrFid, pushMessage);

	}

	return returnObject;
}
IPushMessaging::PushMessageList OpenPeerCoreManager::pushMessageListToCore(jobject javaPushMessageList)
{
	IPushMessaging::PushMessageList returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushMessageListToCore called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( javaPushMessageList, sizeMethodID );

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get Contact object by index.
			jobject pushMessageObject = jni_env->CallObjectMethod( javaPushMessageList, listGetMethodID, i );
			//todo check if needed
			IPushMessaging::PushMessage corePushMessage = pushMessageToCore(pushMessageObject);

			//add core contacts to list for removal
			jclass cls = findClass("com/openpeer/javaapi/OPPushMessage");
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong pointerValue = jni_env->GetLongField(pushMessageObject, fid);

			IPushMessaging::PushMessagePtr* corePushMessagePtr = (IPushMessaging::PushMessagePtr*)pointerValue;
			returnListObject.push_front(*corePushMessagePtr);
		}
	}
	return returnListObject;
}
jobject OpenPeerCoreManager::pushMessageListToJava(IPushMessaging::PushMessageList corePushMessageList)
{
	jobject returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager pushMessageListToJava called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);

		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		//fill java list
		for(IPushMessaging::PushMessageList::iterator coreListIter = corePushMessageList.begin();
				coreListIter != corePushMessageList.end(); coreListIter++)
		{
			jobject javaItemObject = pushMessageToJava(*coreListIter->get());
			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , javaItemObject);
			jni_env->DeleteLocalRef(javaItemObject);
		}
	}
	return returnListObject;
}

IPushMessaging::ValueNameList OpenPeerCoreManager::valueNameListToCore(jobject javaValueNameList)
{
	IPushMessaging::ValueNameList returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager valueNameListToCore called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( javaValueNameList, sizeMethodID );

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get ValueName object by index.
			jstring valueName = (jstring) jni_env->CallObjectMethod( javaValueNameList, listGetMethodID, i );
			IPushMessaging::ValueName coreValueName = String(jni_env->GetStringUTFChars(valueName, NULL));
			returnListObject.push_front(coreValueName);
			jni_env->ReleaseStringUTFChars(valueName, coreValueName);
			jni_env->DeleteLocalRef(valueName);

		}
	}
	return returnListObject;
}

IPushPresence::ValueNameList OpenPeerCoreManager::presenceValueNameListToCore(jobject javaValueNameList)
{
	IPushPresence::ValueNameList returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager valueNameListToCore called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( javaValueNameList, sizeMethodID );

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get ValueName object by index.
			jstring valueName = (jstring) jni_env->CallObjectMethod( javaValueNameList, listGetMethodID, i );
			IPushPresence::ValueName coreValueName = String(jni_env->GetStringUTFChars(valueName, NULL));
			returnListObject.push_front(coreValueName);
			jni_env->ReleaseStringUTFChars(valueName, coreValueName);
			jni_env->DeleteLocalRef(valueName);

		}
	}
	return returnListObject;
}

IPushMessaging::NameValueMap OpenPeerCoreManager::nameValueMapToCore(jobject javaNameValueMap)
{
	IPushMessaging::NameValueMap returnMap;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager nameValueMapToCore called");

	jni_env = getEnv();
	if(jni_env)
	{
		jclass hashMapClass = findClass("java/util/HashMap");
		// size method fetch
		jmethodID sizeMethodID = jni_env->GetMethodID(hashMapClass, "size", "()I");


		// Get the Set Class
		jclass setClass = findClass("java/util/Set");
		// Get the "Iterator" class
		jclass iteratorClass = findClass("java/util/Iterator");
		// Get the Map.Entry class
		jclass mapEntryClass = findClass("java/util/Map/Entry");

		// Get link to Method "entrySet"
		jmethodID entrySetMethod = jni_env->GetMethodID(hashMapClass, "entrySet", "()Ljava/util/Set;");
		// Get link to Method "iterator"
		jmethodID iteratorMethod = jni_env->GetMethodID(setClass, "iterator", "()Ljava/util/Iterator;");
		// Get link to Method "hasNext"
		jmethodID hasNextMethod = jni_env->GetMethodID(iteratorClass, "hasNext", "()Z");
		// Get link to Method "next"
		jmethodID nextMethod = jni_env->GetMethodID(iteratorClass, "next", "()Ljava/util/Map/Entry;");
		// Get link to GetKey/GetValue methods
		jmethodID getKeyMethod = jni_env->GetMethodID(mapEntryClass, "getKey", "()Ljava/lang/Object");
		jmethodID getValueMethod = jni_env->GetMethodID(mapEntryClass, "getValue", "()Ljava/lang/Object");

		int mapItemsCount = (int)jni_env->CallIntMethod( javaNameValueMap, sizeMethodID );

		jboolean bHasNext = false;
		if (mapItemsCount > 0)
		{
			bHasNext = true;
		}
		// Invoke the "entrySet" method on the HashMap object
		jobject entrySetObject = jni_env->CallObjectMethod(javaNameValueMap, entrySetMethod);
		// Invoke the "iterator" method on the jobject_of_entryset variable of type Set
		jobject iteratorObject = jni_env->CallObjectMethod(entrySetObject, iteratorMethod);
		while(bHasNext)
		{
			//Gewt key and value from map entry iterator
			jstring name = (jstring) jni_env->CallObjectMethod(iteratorObject, getKeyMethod);
			jstring value = (jstring) jni_env->CallObjectMethod(iteratorObject, getValueMethod);

			//Pack key and value to C++ class
			std::pair<IPushMessaging::Name, IPushMessaging::Value> coreEntry;
			IPushMessaging::Name coreName = String(jni_env->GetStringUTFChars(name, NULL));
			IPushMessaging::Value coreValue = String(jni_env->GetStringUTFChars(value, NULL));
			coreEntry = std::make_pair(coreName, coreValue);
			returnMap.insert(coreEntry);
			jni_env->ReleaseStringUTFChars(name, coreName);
			jni_env->ReleaseStringUTFChars(value, coreValue);
			jni_env->DeleteLocalRef(name);
			jni_env->DeleteLocalRef(value);

			// Invoke - Get the value hasNextMethod
			bHasNext = jni_env->CallBooleanMethod(iteratorObject, hasNextMethod);
			//Invoke next() method to obtain entry
			iteratorObject = jni_env->CallObjectMethod(iteratorObject, nextMethod);

		}


	}
	return returnMap;
}

IPushPresence::NameValueMap OpenPeerCoreManager::presenceNameValueMapToCore(jobject javaNameValueMap)
{
	IPushPresence::NameValueMap returnMap;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager presenceNameValueMapToCore called");

	jni_env = getEnv();
	if(jni_env)
	{
		jclass hashMapClass = findClass("java/util/HashMap");
		// size method fetch
		jmethodID sizeMethodID = jni_env->GetMethodID(hashMapClass, "size", "()I");


		// Get the Set Class
		jclass setClass = findClass("java/util/Set");
		// Get the "Iterator" class
		jclass iteratorClass = findClass("java/util/Iterator");
		// Get the Map.Entry class
		jclass mapEntryClass = findClass("java/util/Map/Entry");

		// Get link to Method "entrySet"
		jmethodID entrySetMethod = jni_env->GetMethodID(hashMapClass, "entrySet", "()Ljava/util/Set;");
		// Get link to Method "iterator"
		jmethodID iteratorMethod = jni_env->GetMethodID(setClass, "iterator", "()Ljava/util/Iterator;");
		// Get link to Method "hasNext"
		jmethodID hasNextMethod = jni_env->GetMethodID(iteratorClass, "hasNext", "()Z");
		// Get link to Method "next"
		jmethodID nextMethod = jni_env->GetMethodID(iteratorClass, "next", "()Ljava/util/Map/Entry;");
		// Get link to GetKey/GetValue methods
		jmethodID getKeyMethod = jni_env->GetMethodID(mapEntryClass, "getKey", "()Ljava/lang/Object");
		jmethodID getValueMethod = jni_env->GetMethodID(mapEntryClass, "getValue", "()Ljava/lang/Object");

		int mapItemsCount = (int)jni_env->CallIntMethod( javaNameValueMap, sizeMethodID );

		jboolean bHasNext = false;
		if (mapItemsCount > 0)
		{
			bHasNext = true;
		}
		// Invoke the "entrySet" method on the HashMap object
		jobject entrySetObject = jni_env->CallObjectMethod(javaNameValueMap, entrySetMethod);
		// Invoke the "iterator" method on the jobject_of_entryset variable of type Set
		jobject iteratorObject = jni_env->CallObjectMethod(entrySetObject, iteratorMethod);
		while(bHasNext)
		{
			//Gewt key and value from map entry iterator
			jstring name = (jstring) jni_env->CallObjectMethod(iteratorObject, getKeyMethod);
			jstring value = (jstring) jni_env->CallObjectMethod(iteratorObject, getValueMethod);

			//Pack key and value to C++ class
			std::pair<IPushPresence::Name, IPushPresence::Value> coreEntry;
			IPushPresence::Name coreName = String(jni_env->GetStringUTFChars(name, NULL));
			IPushPresence::Value coreValue = String(jni_env->GetStringUTFChars(value, NULL));
			coreEntry = std::make_pair(coreName, coreValue);
			returnMap.insert(coreEntry);
			jni_env->ReleaseStringUTFChars(name, coreName);
			jni_env->ReleaseStringUTFChars(value, coreValue);
			jni_env->DeleteLocalRef(name);
			jni_env->DeleteLocalRef(value);

			// Invoke - Get the value hasNextMethod
			bHasNext = jni_env->CallBooleanMethod(iteratorObject, hasNextMethod);
			//Invoke next() method to obtain entry
			iteratorObject = jni_env->CallObjectMethod(iteratorObject, nextMethod);

		}


	}
	return returnMap;
}


jobject OpenPeerCoreManager::nameValueMapToJava(IPushMessaging::NameValueMapPtr coreMap)
{
	jobject returnMapObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager nameValueMapToJava called");

	jni_env = getEnv();
	if(jni_env)
	{
		jclass hashMapClass = findClass("java/util/HashMap");
		jmethodID hashMapConstructorMethodID = jni_env->GetMethodID(hashMapClass, "<init>", "()V");
		jobject returnMapObject = jni_env->NewObject(hashMapClass, hashMapConstructorMethodID);

		jmethodID putMethod = jni_env->GetMethodID(
				hashMapClass,
				"put",
				"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
		);

		for( IPushMessaging::NameValueMap::iterator it = coreMap->begin(); it != coreMap->end(); ++it )
		{
			//key == Name
			jstring key = jni_env->NewStringUTF((*it).first.c_str() );

			//value == Value
			jstring value = jni_env->NewStringUTF( (*it).second.c_str() );

			jni_env->CallVoidMethod(
					returnMapObject,
					putMethod,
					key,
					value
			);
		}
	}

	return returnMapObject;
}

jobject OpenPeerCoreManager::presenceNameValueMapToJava(IPushPresence::NameValueMapPtr coreMap)
{
	jobject returnMapObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager presenceNameValueMapToJava called");

	jni_env = getEnv();
	if(jni_env)
	{
		jclass hashMapClass = findClass("java/util/HashMap");
		jmethodID hashMapConstructorMethodID = jni_env->GetMethodID(hashMapClass, "<init>", "()V");
		jobject returnMapObject = jni_env->NewObject(hashMapClass, hashMapConstructorMethodID);

		jmethodID putMethod = jni_env->GetMethodID(
				hashMapClass,
				"put",
				"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
		);

		for( IPushPresence::NameValueMap::iterator it = coreMap->begin(); it != coreMap->end(); ++it )
		{
			//key == Name
			jstring key = jni_env->NewStringUTF((*it).first.c_str() );

			//value == Value
			jstring value = jni_env->NewStringUTF( (*it).second.c_str() );

			jni_env->CallVoidMethod(
					returnMapObject,
					putMethod,
					key,
					value
			);
		}
	}

	return returnMapObject;
}

jobject OpenPeerCoreManager::presenceStatusToJava(PresenceStatusPtr statusPtr)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceStatus");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	PresenceStatusPtr* ptrToPresenceStatus = new std::shared_ptr<PresenceStatus>(statusPtr);
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong status = (jlong) ptrToPresenceStatus;
	jni_env->SetLongField(object, fid, status);

	jfieldID statusFid = jni_env->GetFieldID(cls, "mStatus", "Lcom/openpeer/javaapi/PresenceStatuses;");
	jni_env->SetObjectField(object, statusFid, getJavaEnumObject("com/openpeer/javaapi/PresenceStatuses", (int)statusPtr.get()->mStatus));

	jfieldID extendedStatusFid = jni_env->GetFieldID(cls, "mExtendedStatus", "Ljava/lang/String;");
	jni_env->SetObjectField(object, extendedStatusFid, jni_env->NewStringUTF(statusPtr.get()->mExtendedStatus));

	jfieldID statusMessageFid = jni_env->GetFieldID(cls, "mStatusMessage", "Ljava/lang/String;");
	jni_env->SetObjectField(object, statusMessageFid, jni_env->NewStringUTF(statusPtr.get()->mStatusMessage));

	jfieldID priorityFid = jni_env->GetFieldID(cls, "mPriority", "I");
	jni_env->SetIntField(object, priorityFid, (jint)statusPtr.get()->mPriority);

	return object;
}

jobject OpenPeerCoreManager::presenceTimeZoneLocationToJava(PresenceTimeZoneLocationPtr locationPtr)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceTimeZoneLocation");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	PresenceTimeZoneLocationPtr* ptrToPresenceLocation = new std::shared_ptr<PresenceTimeZoneLocation>(locationPtr);
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong location = (jlong) ptrToPresenceLocation;
	jni_env->SetLongField(object, fid, location);

	jfieldID offsetFid = jni_env->GetFieldID(cls, "mOffset", "Landroid/text/format/Time;");
	jni_env->SetObjectField(object, offsetFid, OpenPeerCoreManager::convertSecondsFromCore(locationPtr.get()->mOffset));

	jfieldID abbrevationFid = jni_env->GetFieldID(cls, "mAbbreviation", "Ljava/lang/String;");
	jni_env->SetObjectField(object, abbrevationFid, jni_env->NewStringUTF(locationPtr.get()->mAbbreviation));

	jfieldID nameFid = jni_env->GetFieldID(cls, "mName", "Ljava/lang/String;");
	jni_env->SetObjectField(object, nameFid, jni_env->NewStringUTF(locationPtr.get()->mName));

	jfieldID cityFid = jni_env->GetFieldID(cls, "mCity", "Ljava/lang/String;");
	jni_env->SetObjectField(object, cityFid, jni_env->NewStringUTF(locationPtr.get()->mCity));

	jfieldID countryFid = jni_env->GetFieldID(cls, "mCountry", "Ljava/lang/String;");
	jni_env->SetObjectField(object, countryFid, jni_env->NewStringUTF(locationPtr.get()->mCountry));

	return object;
}

jobject OpenPeerCoreManager::presenceGeographicLocationToJava(PresenceGeographicLocationPtr locationPtr)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceGeographicLocation");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	PresenceGeographicLocationPtr* ptrToPresenceLocation = new std::shared_ptr<PresenceGeographicLocation>(locationPtr);
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong location = (jlong) ptrToPresenceLocation;
	jni_env->SetLongField(object, fid, location);

	jfieldID latitudeFid = jni_env->GetFieldID(cls, "mLatitude", "D");
	jni_env->SetDoubleField(object, latitudeFid, (jdouble) locationPtr.get()->mLatitude);

	jfieldID longitudeFid = jni_env->GetFieldID(cls, "mLongitude", "D");
	jni_env->SetDoubleField(object, longitudeFid, (jdouble) locationPtr.get()->mLongitude);

	jfieldID accRadiusFid = jni_env->GetFieldID(cls, "mGeographicAccuracyRadius", "D");
	jni_env->SetDoubleField(object, accRadiusFid, (jdouble) locationPtr.get()->mGeographicAccuracyRadius);

	jfieldID altitudeFid = jni_env->GetFieldID(cls, "mAltitude", "D");
	jni_env->SetDoubleField(object, altitudeFid, (jdouble) locationPtr.get()->mAltitude);

	jfieldID altitudeAccFid = jni_env->GetFieldID(cls, "mAltitudeAccuracy", "D");
	jni_env->SetDoubleField(object, altitudeAccFid, (jdouble) locationPtr.get()->mAltitudeAccuracy);

	jfieldID directionFid = jni_env->GetFieldID(cls, "mDirection", "D");
	jni_env->SetDoubleField(object, directionFid, (jdouble) locationPtr.get()->mDirection);

	jfieldID speedFid = jni_env->GetFieldID(cls, "mSpeed", "D");
	jni_env->SetDoubleField(object, speedFid, (jdouble) locationPtr.get()->mSpeed);

	return object;
}

jobject OpenPeerCoreManager::presenceStreetLocationToJava(PresenceStreetLocationPtr locationPtr)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceStreetLocation");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	PresenceStreetLocationPtr* ptrToPresenceLocation = new std::shared_ptr<PresenceStreetLocation>(locationPtr);
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong location = (jlong) ptrToPresenceLocation;
	jni_env->SetLongField(object, fid, location);

	fid = jni_env->GetFieldID(cls, "mFriendlyName", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mFriendlyName));

	fid = jni_env->GetFieldID(cls, "mSuiteNumber", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mSuiteNumber));

	fid = jni_env->GetFieldID(cls, "mBuildingFloor", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mBuildingFloor));

	fid = jni_env->GetFieldID(cls, "mBuilding", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mBuilding));

	fid = jni_env->GetFieldID(cls, "mStreetNumber", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mStreetNumber));

	fid = jni_env->GetFieldID(cls, "mStreetNumberSuffix", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mStreetNumberSuffix));

	fid = jni_env->GetFieldID(cls, "mStreetDirectionPrefix", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mStreetDirectionPrefix));

	fid = jni_env->GetFieldID(cls, "mStreetName", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mStreetName));

	fid = jni_env->GetFieldID(cls, "mStreetSuffix", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mStreetSuffix));

	fid = jni_env->GetFieldID(cls, "mStreetDirectionSuffix", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mStreetDirectionSuffix));

	fid = jni_env->GetFieldID(cls, "mPostalCommunity", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mPostalCommunity));

	fid = jni_env->GetFieldID(cls, "mServiceCommunity", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mServiceCommunity));

	fid = jni_env->GetFieldID(cls, "mProvince", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mProvince));

	fid = jni_env->GetFieldID(cls, "mCountry", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(locationPtr.get()->mCountry));

	return object;
}

jobject OpenPeerCoreManager::presenceResourceToJava(PresenceResources::Resource resource)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceResource");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	jfieldID fid = jni_env->GetFieldID(cls, "mID", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mID));

	fid = jni_env->GetFieldID(cls, "mRelatedID", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mRelatedID));

	fid = jni_env->GetFieldID(cls, "mType", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mType));

	fid = jni_env->GetFieldID(cls, "mFriendlyName", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mFriendlyName));

	fid = jni_env->GetFieldID(cls, "mResourceURL", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mResourceURL));

	fid = jni_env->GetFieldID(cls, "mMimeType", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mMimeType));

	fid = jni_env->GetFieldID(cls, "mSize", "I");
	jni_env->SetIntField(object, fid, (jint)resource.mSize);

	fid = jni_env->GetFieldID(cls, "mWidth", "I");
	jni_env->SetIntField(object, fid, (jint)resource.mWidth);

	fid = jni_env->GetFieldID(cls, "mHeight", "I");
	jni_env->SetIntField(object, fid, (jint)resource.mHeight);

	fid = jni_env->GetFieldID(cls, "mLength", "Landroid/text/format/Time;");
	jni_env->SetObjectField(object, fid, OpenPeerCoreManager::convertSecondsFromCore(resource.mLength));

	fid = jni_env->GetFieldID(cls, "mExternalLinkURL", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mExternalLinkURL));

	fid = jni_env->GetFieldID(cls, "mEncoding", "Ljava/lang/String;");
	jni_env->SetObjectField(object, fid, jni_env->NewStringUTF(resource.mEncoding));

	return object;
}

jobject OpenPeerCoreManager::presenceResourceListToJava(PresenceResources::ResourceList resourceList)
{
	jobject returnListObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager presenceResourceListToJava called");

	jni_env = getEnv();

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);

		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		//fill java list
		for(PresenceResources::ResourceList::iterator coreListIter = resourceList.begin();
				coreListIter != resourceList.end(); coreListIter++)
		{
			jobject javaItemObject = presenceResourceToJava(*coreListIter);
			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , javaItemObject);
			jni_env->DeleteLocalRef(javaItemObject);
		}
	}
	return returnListObject;
}

jobject OpenPeerCoreManager::presenceResourcesToJava(PresenceResourcesPtr resourcesPtr)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	cls = findClass("com/openpeer/javaapi/OPPresenceResources");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	PresenceResourcesPtr* ptrToPresenceResources = new std::shared_ptr<PresenceResources>(resourcesPtr);
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong resources = (jlong) ptrToPresenceResources;
	jni_env->SetLongField(object, fid, resources);

	fid = jni_env->GetFieldID(cls, "mResources", "Ljava/util/List;");
	jni_env->SetObjectField(object, fid, presenceResourceListToJava(resourcesPtr->mResources));

	return object;
}

IPushMessaging::RegisterDeviceInfo OpenPeerCoreManager::registerDeviceInfoToMessaging(jobject javaRegisterDeviceInfo)
{
	IPushMessaging::RegisterDeviceInfo returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager registerDeviceInfoToMessaging called");

	jni_env = getEnv();
	if (jni_env)
	{
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPRegisterDeviceInfo");

		//mDeviceToken
		jmethodID getDeviceTokenMethodID = jni_env->GetMethodID( javaItemClass, "getDeviceToken", "()Ljava/lang/String;" );
		jstring deviceToken = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getDeviceTokenMethodID);
		returnObject.mDeviceToken = String(jni_env->GetStringUTFChars(deviceToken, NULL));
		jni_env->ReleaseStringUTFChars(deviceToken, returnObject.mDeviceToken);
		jni_env->DeleteLocalRef(deviceToken);

		//mExpires
		jmethodID getExpiresMethodID = jni_env->GetMethodID( javaItemClass, "getExpires", "()Landroid/text/format/Time;" );
		jobject expires = jni_env->CallObjectMethod(javaRegisterDeviceInfo, getExpiresMethodID);
		returnObject.mExpires = OpenPeerCoreManager::convertTimeFromJava(expires);

		//mMappedType
		jmethodID getMappedTypeMethodID = jni_env->GetMethodID( javaItemClass, "getMappedType", "()Ljava/lang/String;" );
		jstring mappedType = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getMappedTypeMethodID);
		returnObject.mMappedType = String(jni_env->GetStringUTFChars(mappedType, NULL));
		jni_env->ReleaseStringUTFChars(mappedType, returnObject.mMappedType);
		jni_env->DeleteLocalRef(mappedType);

		//mUnreadBadge
		jmethodID getUnreadBadgeMethodID = jni_env->GetMethodID( javaItemClass, "getUnreadBadge", "()Z" );
		returnObject.mUnreadBadge = (jboolean) jni_env->CallBooleanMethod(javaRegisterDeviceInfo, getUnreadBadgeMethodID);

		//mSound
		jmethodID getSoundMethodID = jni_env->GetMethodID( javaItemClass, "getSound", "()Ljava/lang/String;" );
		jstring sound = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getSoundMethodID);
		returnObject.mSound = String(jni_env->GetStringUTFChars(sound, NULL));
		jni_env->ReleaseStringUTFChars(sound, returnObject.mSound);
		jni_env->DeleteLocalRef(sound);

		//mAction
		jmethodID getActionMethodID = jni_env->GetMethodID( javaItemClass, "getAction", "()Ljava/lang/String;" );
		jstring action = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getActionMethodID);
		returnObject.mAction = String(jni_env->GetStringUTFChars(action, NULL));
		jni_env->ReleaseStringUTFChars(action, returnObject.mAction);
		jni_env->DeleteLocalRef(action);

		//mLaunchImage
		jmethodID getLaunchImageMethodID = jni_env->GetMethodID( javaItemClass, "getLaunchImage", "()Ljava/lang/String;" );
		jstring launchImage = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getLaunchImageMethodID);
		returnObject.mLaunchImage = String(jni_env->GetStringUTFChars(launchImage, NULL));
		jni_env->ReleaseStringUTFChars(launchImage, returnObject.mLaunchImage);
		jni_env->DeleteLocalRef(launchImage);

		//mPriority
		jmethodID getPriorityMethodID = jni_env->GetMethodID( javaItemClass, "getPriority", "()I" );
		returnObject.mPriority = (jint) jni_env->CallIntMethod(javaRegisterDeviceInfo, getPriorityMethodID);

		//mPushInfos
		jmethodID getValueNamesMethodID = jni_env->GetMethodID( javaItemClass, "getValueNames", "()Ljava/util/List;" );
		jobject valueNames = jni_env->CallObjectMethod(javaRegisterDeviceInfo, getValueNamesMethodID);
		returnObject.mValueNames = OpenPeerCoreManager::valueNameListToCore(valueNames);

	}

	return returnObject;
}
IPushPresence::RegisterDeviceInfo OpenPeerCoreManager::registerDeviceInfoToPresence(jobject javaRegisterDeviceInfo)
{
	IPushPresence::RegisterDeviceInfo returnObject;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OpenPeerCoreManager registerDeviceInfoToPresence called");

	jni_env = getEnv();
	if (jni_env)
	{
		jclass javaItemClass = findClass("com/openpeer/javaapi/OPRegisterDeviceInfo");

		//mDeviceToken
		jmethodID getDeviceTokenMethodID = jni_env->GetMethodID( javaItemClass, "getDeviceToken", "()Ljava/lang/String;" );
		jstring deviceToken = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getDeviceTokenMethodID);
		returnObject.mDeviceToken = String(jni_env->GetStringUTFChars(deviceToken, NULL));
		jni_env->ReleaseStringUTFChars(deviceToken, returnObject.mDeviceToken);
		jni_env->DeleteLocalRef(deviceToken);

		//mExpires
		jmethodID getExpiresMethodID = jni_env->GetMethodID( javaItemClass, "getExpires", "()Landroid/text/format/Time;" );
		jobject expires = jni_env->CallObjectMethod(javaRegisterDeviceInfo, getExpiresMethodID);
		returnObject.mExpires = OpenPeerCoreManager::convertTimeFromJava(expires);

		//mMappedType
		jmethodID getMappedTypeMethodID = jni_env->GetMethodID( javaItemClass, "getMappedType", "()Ljava/lang/String;" );
		jstring mappedType = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getMappedTypeMethodID);
		returnObject.mMappedType = String(jni_env->GetStringUTFChars(mappedType, NULL));
		jni_env->ReleaseStringUTFChars(mappedType, returnObject.mMappedType);
		jni_env->DeleteLocalRef(mappedType);

		//mUnreadBadge
		jmethodID getUnreadBadgeMethodID = jni_env->GetMethodID( javaItemClass, "getUnreadBadge", "()Z" );
		returnObject.mUnreadBadge = (jboolean) jni_env->CallBooleanMethod(javaRegisterDeviceInfo, getUnreadBadgeMethodID);

		//mSound
		jmethodID getSoundMethodID = jni_env->GetMethodID( javaItemClass, "getSound", "()Ljava/lang/String;" );
		jstring sound = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getSoundMethodID);
		returnObject.mSound = String(jni_env->GetStringUTFChars(sound, NULL));
		jni_env->ReleaseStringUTFChars(sound, returnObject.mSound);
		jni_env->DeleteLocalRef(sound);

		//mAction
		jmethodID getActionMethodID = jni_env->GetMethodID( javaItemClass, "getAction", "()Ljava/lang/String;" );
		jstring action = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getActionMethodID);
		returnObject.mAction = String(jni_env->GetStringUTFChars(action, NULL));
		jni_env->ReleaseStringUTFChars(action, returnObject.mAction);
		jni_env->DeleteLocalRef(action);

		//mLaunchImage
		jmethodID getLaunchImageMethodID = jni_env->GetMethodID( javaItemClass, "getLaunchImage", "()Ljava/lang/String;" );
		jstring launchImage = (jstring)jni_env->CallObjectMethod(javaRegisterDeviceInfo, getLaunchImageMethodID);
		returnObject.mLaunchImage = String(jni_env->GetStringUTFChars(launchImage, NULL));
		jni_env->ReleaseStringUTFChars(launchImage, returnObject.mLaunchImage);
		jni_env->DeleteLocalRef(launchImage);

		//mPriority
		jmethodID getPriorityMethodID = jni_env->GetMethodID( javaItemClass, "getPriority", "()I" );
		returnObject.mPriority = (jint) jni_env->CallIntMethod(javaRegisterDeviceInfo, getPriorityMethodID);

		//mPushInfos
		jmethodID getValueNamesMethodID = jni_env->GetMethodID( javaItemClass, "getValueNames", "()Ljava/util/List;" );
		jobject valueNames = jni_env->CallObjectMethod(javaRegisterDeviceInfo, getValueNamesMethodID);
		returnObject.mValueNames = OpenPeerCoreManager::presenceValueNameListToCore(valueNames);

	}

	return returnObject;
}
