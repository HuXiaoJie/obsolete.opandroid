#include "com_openpeer_javaapi_OPPushPresence.h"
#include "openpeer/core/IPushPresence.h"
#include "openpeer/core/ILogger.h"
#include "OpenPeerCoreManager.h"
#include <android/log.h>


#include "globals.h"

using namespace openpeer::core;
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    create
 * Signature: (Lcom/openpeer/javaapi/OPPushPresenceDelegate;Lcom/openpeer/javaapi/OPPushPresenceDatabaseAbstractionDelegate;Lcom/openpeer/javaapi/OPAccount;)Lcom/openpeer/javaapi/OPPushPresence;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresence_create
(JNIEnv *, jclass, jobject javaPresenceDelegate, jobject javaDatabaseDelegate, jobject javaAccount)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native create called");

	jni_env = getEnv();

	if (javaPresenceDelegate == NULL || javaDatabaseDelegate == NULL)
	{
		return object;
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//set java delegate to push presence delegate wrapper and init shared pointer for wrappers
	PushPresenceDelegateWrapperPtr presenceDelegatePtr = PushPresenceDelegateWrapperPtr(new PushPresenceDelegateWrapper(javaPresenceDelegate));

	//TODO: Remove line bellow as soon as DB abstraction delegate is implemented
	//PushMessagingDatabaseAbstractionDelegateWrapperPtr messagingDBAbstractionDelegatePtr = PushMessagingDatabaseAbstractionDelegateWrapperPtr(new PushMessagingDatabaseAbstractionDelegateWrapper(javaDatabaseDelegate));
	//IPushMessagingPtr messagingPtr = IPushMessaging::create(messagingDelegatePtr, messagingDBAbstractionDelegatePtr, *coreAccountPtr);
	IPushPresencePtr presencePtr = IPushPresence::create(presenceDelegatePtr,IPushPresenceTransferDelegatePtr() , *coreAccountPtr);

	if(presencePtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPPushPresence");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IPushPresencePtr* ptrToPresence = new std::shared_ptr<IPushPresence>(presencePtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong messaging = (jlong) ptrToPresence;
			jni_env->SetLongField(object, fid, messaging);


			PushPresenceDelegateWrapperPtr* ptrToPushPresenceDelegateWrapperPtr= new std::shared_ptr<PushPresenceDelegateWrapper>(presenceDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToPushPresenceDelegateWrapperPtr;
			jni_env->SetLongField(object, delegateFid, delegate);

			//			PushMessagingDatabaseAbstractionDelegateWrapperPtr* ptrToPushMessagingDBAbstractionDelegateWrapperPtr= new std::shared_ptr<PushMessagingDatabaseAbstractionDelegateWrapper>(messagingDBAbstractionDelegatePtr);
			//			jfieldID dbDelegateFid = jni_env->GetFieldID(cls, "nativeDatabaseAbstractionDelegatePointer", "J");
			//			jlong dbDelegate = (jlong) ptrToPushMessagingDBAbstractionDelegateWrapperPtr;
			//			jni_env->SetLongField(object, dbDelegateFid, dbDelegate);


		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native create core pointer is NULL!!!");
	}
	return object;
}


/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    getID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPPushPresence_getID
(JNIEnv *, jobject owner)
{
	jlong ret;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native getID called");

	jni_env = getEnv();
	jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
	jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, presenceFid);

	IPushPresencePtr* corePresencePtr = (IPushPresencePtr*)pointerValue;

	if (corePresencePtr)
	{
		ret = (jlong) corePresencePtr->get()->getID();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native getID core pointer is NULL!!!");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    getState
 * Signature: (ILjava/lang/String;)Lcom/openpeer/javaapi/PushPresenceStates;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresence_getState
(JNIEnv *, jobject owner, jint, jstring)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jint state = 0;
	unsigned short int outErrorCode;
	String outErrorReason;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native getState called");

	jni_env = getEnv();
	jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
	jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, presenceFid);

	IPushPresencePtr* corePresencePtr = (IPushPresencePtr*)pointerValue;

	if (corePresencePtr)
	{
		state = (jint) corePresencePtr->get()->getState(&outErrorCode, &outErrorReason);
		if(jni_env)
		{
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/PushPresenceStates", state);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native getState core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushPresence_shutdown
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native shutdown called");

	jni_env = getEnv();
	jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
	jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, presenceFid);

	IPushPresencePtr* corePresencePtr = (IPushPresencePtr*)pointerValue;
	if (corePresencePtr)
	{
		corePresencePtr->get()->shutdown();
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native shutdown core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    registerDevice
 * Signature: (Lcom/openpeer/javaapi/OPPushPresenceRegisterQueryDelegate;Lcom/openpeer/javaapi/OPRegisterDeviceInfo;)Lcom/openpeer/javaapi/OPPushPresenceRegisterQuery;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresence_registerDevice
(JNIEnv *, jobject owner, jobject inDelegate, jobject inDeviceInfo)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native registerDevice called");

	jni_env = getEnv();

	if (inDelegate == NULL)
	{
		return object;
	}

	jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
	jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, presenceFid);

	IPushPresencePtr* corePresencePtr = (IPushPresencePtr*)pointerValue;

	//create core delegate
	PushPresenceRegisterQueryDelegateWrapperPtr registerQueryDelegatePtr = PushPresenceRegisterQueryDelegateWrapperPtr(new PushPresenceRegisterQueryDelegateWrapper(inDelegate));

	if (corePresencePtr)
	{
		IPushPresenceRegisterQueryPtr queryPtr =
				corePresencePtr->get()->registerDevice(
						registerQueryDelegatePtr,
						OpenPeerCoreManager::registerDeviceInfoToPresence(inDeviceInfo));
		if(queryPtr)
		{
			jclass queryCls = findClass("com/openpeer/javaapi/OPPushPresenceRegisterQuery");
			method = jni_env->GetMethodID(queryCls, "<init>", "()V");
			object = jni_env->NewObject(queryCls, method);

			IPushPresenceRegisterQueryPtr* ptrToQuery = new std::shared_ptr<IPushPresenceRegisterQuery>(queryPtr);
			jfieldID queryFid = jni_env->GetFieldID(queryCls, "nativeClassPointer", "J");
			jlong query = (jlong) ptrToQuery;
			jni_env->SetLongField(object, queryFid, query);


			PushPresenceRegisterQueryDelegateWrapperPtr* ptrToQueryDelegateWrapperPtr= new std::shared_ptr<PushPresenceRegisterQueryDelegateWrapper>(registerQueryDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(queryCls, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToQueryDelegateWrapperPtr;
			jni_env->SetLongField(object, delegateFid, delegate);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native registerDevice core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    send
 * Signature: (Ljava/util/List;Lcom/openpeer/javaapi/OPPushPresenceStatus;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushPresence_send
(JNIEnv *, jobject owner, jobject javaContactList, jobject javaStatus)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native push called");

	jni_env = getEnv();

	if (javaContactList == NULL)
	{
		return;
	}

	if (javaStatus == NULL)
	{
		return;
	}

	jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
	jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, presenceFid);

	IPushPresencePtr* corePresencePtr = (IPushPresencePtr*)pointerValue;

	//fill core contact list from java contact list
	ContactList coreContacts;
	//create return object - java/util/List is interface, ArrayList is implementation
	jclass arrayListClass = findClass("java/util/ArrayList");
	if(jni_env->IsInstanceOf(javaContactList, arrayListClass) != JNI_TRUE)
	{
		return;
	}
	// Fetch "java.util.List.get(int location)" MethodID
	jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
	// Fetch "int java.util.List.size()" MethodID
	jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

	// Call "int java.util.List.size()" method and get count of items in the list.
	int listItemsCount = (int)jni_env->CallIntMethod( javaContactList, sizeMethodID );

	for( int i=0; i<listItemsCount; ++i )
	{
		// Call "java.util.List.get" method and get Contact object by index.
		jobject contactObject = jni_env->CallObjectMethod( javaContactList, listGetMethodID, i);
		if( contactObject != NULL )
		{
			cls = findClass("com/openpeer/javaapi/OPContact");
			jfieldID contactfid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong pointerValue = jni_env->GetLongField(contactObject, contactfid);

			IContactPtr* coreContactPtr = (IContactPtr*)pointerValue;
			//add core contacts to list for removal
			coreContacts.push_front(*coreContactPtr);
		}
	}

	//get core status from java status
	jclass statusCls = findClass("com/openpeer/javaapi/OPPushPresenceStatus");
	jfieldID statusfid = jni_env->GetFieldID(statusCls, "nativeClassPointer", "J");
	jlong statusPointerValue = jni_env->GetLongField(javaStatus, statusfid);

	IPushPresence::StatusPtr* coreStatusPtr = (IPushPresence::StatusPtr*)statusPointerValue;

	if (corePresencePtr)
	{
		corePresencePtr->get()->send(
				coreContacts,
				*coreStatusPtr->get());
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native push core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    recheckNow
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushPresence_recheckNow
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native recheckNow called");

	jni_env = getEnv();
	jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
	jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, presenceFid);

	IPushPresencePtr* corePresencePtr = (IPushPresencePtr*)pointerValue;
	if (corePresencePtr)
	{
		corePresencePtr->get()->recheckNow();
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushPresence native recheckNow core pointer is NULL!!!");
	}
}


/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    getValues
 * Signature: (Lcom/openpeer/javaapi/OPPushPresencePushInfo;)Ljava/util/Map;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresence_getValues
(JNIEnv *, jclass, jobject javaPushInfo)
{
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native getValues called");

	jni_env = getEnv();

	IPushPresence::NameValueMapPtr coreMap = IPushPresence::getValues(OpenPeerCoreManager::presencePushInfoToCore(javaPushInfo));

	return OpenPeerCoreManager::presenceNameValueMapToJava(coreMap);
}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    createValues
 * Signature: (Ljava/util/Map;)Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushPresence_createValues
(JNIEnv *, jclass, jobject javaNameValueMap)
{
	JNIEnv *jni_env = 0;
	jclass cls;
	jmethodID method;
	jobject object;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence native getValues called");

	jni_env = getEnv();
	ElementPtr coreEl = IPushPresence::createValues(OpenPeerCoreManager::presenceNameValueMapToCore(javaNameValueMap));
	ElementPtr* ptrToElement = new std::shared_ptr<Element>(coreEl);
	cls = findClass("com/openpeer/javaapi/OPElement");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong element = (jlong) ptrToElement;
	jni_env->SetLongField(object, fid, element);

	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPPushPresence
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushPresence_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass presenceClass = findClass("com/openpeer/javaapi/OPPushPresence");
		jfieldID presenceFid = jni_env->GetFieldID(presenceClass, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, presenceFid);

		delete (IPushPresencePtr*)pointerValue;

		jfieldID delegateFid = jni_env->GetFieldID(presenceClass, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, delegateFid);

		delete (PushPresenceDelegateWrapperPtr*)delegatePointerValue;

		jfieldID dbDelegateFid = jni_env->GetFieldID(presenceClass, "nativeDatabaseAbstractionDelegatePointer", "J");
		jlong dbDelegatePointerValue = jni_env->GetLongField(javaObject, dbDelegateFid);

		delete (PushPresenceDatabaseAbstractionDelegateWrapperPtr*)dbDelegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushPresence Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPPushPresence Core object not deleted - already NULL!");
	}
}
#ifdef __cplusplus
}
#endif
