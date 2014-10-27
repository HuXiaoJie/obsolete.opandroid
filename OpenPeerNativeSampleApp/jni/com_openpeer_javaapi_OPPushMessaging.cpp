#include "com_openpeer_javaapi_OPPushMessaging.h"
#include "openpeer/core/IPushMessaging.h"
#include "openpeer/core/ILogger.h"
#include "OpenPeerCoreManager.h"
#include <android/log.h>


#include "globals.h"

using namespace openpeer::core;
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    create
 * Signature: (Lcom/openpeer/javaapi/OPPushMessagingDelegate;Lcom/openpeer/javaapi/OPPushMessagingDatabaseAbstractionDelegate;Lcom/openpeer/javaapi/OPAccount;)Lcom/openpeer/javaapi/OPPushMessaging;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_create
(JNIEnv *, jclass, jobject javaMessagingDelegate, jobject javaDatabaseDelegate, jobject javaAccount)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native create called");

	jni_env = getEnv();

	if (javaMessagingDelegate == NULL || javaDatabaseDelegate == NULL)
	{
		return object;
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//set java delegate to push messaging delegate wrapper and init shared pointer for wrappers
	PushMessagingDelegateWrapperPtr messagingDelegatePtr = PushMessagingDelegateWrapperPtr(new PushMessagingDelegateWrapper(javaMessagingDelegate));

	//TODO: Remove line bellow as soon as DB abstraction delegate is implemented
	//PushMessagingDatabaseAbstractionDelegateWrapperPtr messagingDBAbstractionDelegatePtr = PushMessagingDatabaseAbstractionDelegateWrapperPtr(new PushMessagingDatabaseAbstractionDelegateWrapper(javaDatabaseDelegate));
	//IPushMessagingPtr messagingPtr = IPushMessaging::create(messagingDelegatePtr, messagingDBAbstractionDelegatePtr, *coreAccountPtr);
	IPushMessagingPtr messagingPtr = IPushMessaging::create(messagingDelegatePtr,IPushMessagingDatabaseAbstractionDelegatePtr() , *coreAccountPtr);

	if(messagingPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPPushMessaging");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IPushMessagingPtr* ptrToMessaging = new boost::shared_ptr<IPushMessaging>(messagingPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong messaging = (jlong) ptrToMessaging;
			jni_env->SetLongField(object, fid, messaging);


			PushMessagingDelegateWrapperPtr* ptrToPushMessagingDelegateWrapperPtr= new boost::shared_ptr<PushMessagingDelegateWrapper>(messagingDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToPushMessagingDelegateWrapperPtr;
			jni_env->SetLongField(object, delegateFid, delegate);

//			PushMessagingDatabaseAbstractionDelegateWrapperPtr* ptrToPushMessagingDBAbstractionDelegateWrapperPtr= new boost::shared_ptr<PushMessagingDatabaseAbstractionDelegateWrapper>(messagingDBAbstractionDelegatePtr);
//			jfieldID dbDelegateFid = jni_env->GetFieldID(cls, "nativeDatabaseAbstractionDelegatePointer", "J");
//			jlong dbDelegate = (jlong) ptrToPushMessagingDBAbstractionDelegateWrapperPtr;
//			jni_env->SetLongField(object, dbDelegateFid, dbDelegate);


		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native create core pointer is NULL!!!");
	}
	return object;
}


/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    getID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPPushMessaging_getID
(JNIEnv *, jobject owner)
{
	jlong ret;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native getID called");

	jni_env = getEnv();
	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;

	if (coreMessagingPtr)
	{
		ret = (jlong) coreMessagingPtr->get()->getID();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native getID core pointer is NULL!!!");
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    getState
 * Signature: (ILjava/lang/String;)Lcom/openpeer/javaapi/PushMessagingStates;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_getState
(JNIEnv *, jobject owner, jint, jstring)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jint state = 0;
	unsigned short int outErrorCode;
	String outErrorReason;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native getState called");

	jni_env = getEnv();
	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;

	if (coreMessagingPtr)
	{
		state = (jint) coreMessagingPtr->get()->getState(&outErrorCode, &outErrorReason);
		if(jni_env)
		{
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/PushMessagingStates", state);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native getState core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushMessaging_shutdown
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native shutdown called");

	jni_env = getEnv();
	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;
	if (coreMessagingPtr)
	{
		coreMessagingPtr->get()->shutdown();
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native shutdown core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    registerDevice
 * Signature: (Lcom/openpeer/javaapi/OPPushMessagingRegisterQueryDelegate;Ljava/lang/String;Landroid/text/format/Time;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;)Lcom/openpeer/javaapi/OPPushMessagingRegisterQuery;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_registerDevice
(JNIEnv *, jobject owner,
		jobject inDelegate,
		jstring inDeviceToken,
		jobject inExpires,
		jstring inMappedType,
		jboolean inUnreadBadge,
		jstring inSound,
		jstring inAction,
		jstring inLaunchImage,
		jint inPriority,
		jobject inValueNames)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native registerDevice called");

	jni_env = getEnv();

	if (inDelegate == NULL)
	{
		return object;
	}

	const char *inDeviceTokenStr;
	inDeviceTokenStr = jni_env->GetStringUTFChars(inDeviceToken, NULL);
	if (inDeviceTokenStr == NULL) {
		return object;
	}

	if (inExpires == NULL)
	{
		return object;
	}

	const char *inMappedTypeStr;
	inMappedTypeStr = jni_env->GetStringUTFChars(inMappedType, NULL);
	if (inMappedTypeStr == NULL) {
		return object;
	}

	const char *inSoundStr;
	inSoundStr = jni_env->GetStringUTFChars(inSound, NULL);
	if (inSoundStr == NULL) {
		return object;
	}

	const char *inActionStr;
	inActionStr = jni_env->GetStringUTFChars(inAction, NULL);
	if (inActionStr == NULL) {
		return object;
	}

	const char *inLaunchImageStr;
	inLaunchImageStr = jni_env->GetStringUTFChars(inLaunchImage, NULL);
	if (inLaunchImageStr == NULL) {
		return object;
	}

	if (inValueNames == NULL)
	{
		return object;
	}

	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;

	//convert time from java to c++
	Time t;
	jclass timeCls = findClass("android/text/format/Time");
	if(jni_env->IsInstanceOf(inExpires, timeCls) == JNI_TRUE)
	{
		jmethodID timeMethodID   = jni_env->GetMethodID(cls, "toMillis", "(Z)J");
		jlong longValue = jni_env->CallLongMethod(inExpires, timeMethodID, false);
		t = boost::posix_time::from_time_t(longValue/1000) + boost::posix_time::millisec(longValue % 1000);
	}

	//create core delegate
	PushMessagingRegisterQueryDelegateWrapperPtr registerQueryDelegatePtr = PushMessagingRegisterQueryDelegateWrapperPtr(new PushMessagingRegisterQueryDelegateWrapper(inDelegate));

	if (coreMessagingPtr)
	{
		IPushMessagingRegisterQueryPtr queryPtr =
				coreMessagingPtr->get()->registerDevice(
						registerQueryDelegatePtr,
						(char const*)inDeviceTokenStr,
						t,
						(char const*)inMappedTypeStr,
						(bool) inUnreadBadge,
						(char const*)inSoundStr,
						(char const*)inActionStr,
						(char const*)inLaunchImageStr,
						(int) inPriority,
						OpenPeerCoreManager::valueNameListToCore(inValueNames));
		if(queryPtr)
		{
			jclass queryCls = findClass("com/openpeer/javaapi/OPPushMessagingRegisterQuery");
			method = jni_env->GetMethodID(queryCls, "<init>", "()V");
			object = jni_env->NewObject(queryCls, method);

			IPushMessagingRegisterQueryPtr* ptrToQuery = new boost::shared_ptr<IPushMessagingRegisterQuery>(queryPtr);
			jfieldID queryFid = jni_env->GetFieldID(queryCls, "nativeClassPointer", "J");
			jlong query = (jlong) ptrToQuery;
			jni_env->SetLongField(object, queryFid, query);


			PushMessagingRegisterQueryDelegateWrapperPtr* ptrToQueryDelegateWrapperPtr= new boost::shared_ptr<PushMessagingRegisterQueryDelegateWrapper>(registerQueryDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(queryCls, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToQueryDelegateWrapperPtr;
			jni_env->SetLongField(object, delegateFid, delegate);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native registerDevice core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    push
 * Signature: (Lcom/openpeer/javaapi/OPPushMessagingQueryDelegate;Ljava/util/List;Lcom/openpeer/javaapi/OPPushMessage;)Lcom/openpeer/javaapi/OPPushMessagingQuery;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_push
(JNIEnv *, jobject owner, jobject javaQueryDelegate, jobject javaContactList, jobject javaMessage)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native push called");

	jni_env = getEnv();

	if (javaQueryDelegate == NULL)
	{
		return object;
	}

	if (javaContactList == NULL)
	{
		return object;
	}

	if (javaMessage == NULL)
	{
		return object;
	}

	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;

	//create core delegate
	PushMessagingQueryDelegateWrapperPtr queryDelegatePtr = PushMessagingQueryDelegateWrapperPtr(new PushMessagingQueryDelegateWrapper(javaQueryDelegate));

	//fill core contact list from java contact list
	ContactList coreContacts;
	//create return object - java/util/List is interface, ArrayList is implementation
	jclass arrayListClass = findClass("java/util/ArrayList");
	if(jni_env->IsInstanceOf(javaContactList, arrayListClass) != JNI_TRUE)
	{
		return object;
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

	//create core message from java message
	IPushMessaging::PushMessage coreMessage = OpenPeerCoreManager::pushMessageToCore(javaMessage);

	if (coreMessagingPtr)
	{
		IPushMessagingQueryPtr queryPtr =
				coreMessagingPtr->get()->push(
						queryDelegatePtr,
						coreContacts,
						coreMessage);
		if(queryPtr)
		{
			jclass queryCls = findClass("com/openpeer/javaapi/OPPushMessagingQuery");
			method = jni_env->GetMethodID(queryCls, "<init>", "()V");
			object = jni_env->NewObject(queryCls, method);

			IPushMessagingQueryPtr* ptrToQuery = new boost::shared_ptr<IPushMessagingQuery>(queryPtr);
			jfieldID queryFid = jni_env->GetFieldID(queryCls, "nativeClassPointer", "J");
			jlong query = (jlong) ptrToQuery;
			jni_env->SetLongField(object, queryFid, query);


			PushMessagingQueryDelegateWrapperPtr* ptrToQueryDelegateWrapperPtr= new boost::shared_ptr<PushMessagingQueryDelegateWrapper>(queryDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(queryCls, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToQueryDelegateWrapperPtr;
			jni_env->SetLongField(object, delegateFid, delegate);

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native push core pointer is NULL!!!");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    recheckNow
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushMessaging_recheckNow
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native recheckNow called");

	jni_env = getEnv();
	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;
	if (coreMessagingPtr)
	{
		coreMessagingPtr->get()->recheckNow();
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native recheckNow core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    getMessagesUpdates
 * Signature: (Ljava/lang/String;)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_getMessagesUpdates
(JNIEnv *, jobject owner, jstring inLastVersionDownloaded)
{
	JNIEnv *jni_env = 0;
	jclass cls;
	jmethodID method;
	jobject object;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native getMessagesUpdates called");

	jni_env = getEnv();

	const char *inLastVersionDownloadedStr;
	inLastVersionDownloadedStr = jni_env->GetStringUTFChars(inLastVersionDownloaded, NULL);
	if (inLastVersionDownloadedStr == NULL) {
		return object;
	}

	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;
	if (coreMessagingPtr)
	{
		IPushMessaging::PushMessageList coreMessageList;
		String outVersion;

		coreMessagingPtr->get()->getMessagesUpdates(inLastVersionDownloadedStr,outVersion, coreMessageList );

		object = OpenPeerCoreManager::pushMessageListToJava(coreMessageList);
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native getMessagesUpdates core pointer is NULL!!!");
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    getValues
 * Signature: (Lcom/openpeer/javaapi/OPPushInfo;)Ljava/util/Map;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_getValues
(JNIEnv *, jclass, jobject javaPushInfo)
{
	JNIEnv *jni_env = 0;
	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native getValues called");

	jni_env = getEnv();

	IPushMessaging::NameValueMapPtr coreMap = IPushMessaging::getValues(OpenPeerCoreManager::pushInfoToCore(javaPushInfo));

	return OpenPeerCoreManager::nameValueMapToJava(coreMap);
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    createValues
 * Signature: (Ljava/util/Map;)Lcom/openpeer/javaapi/OPElement;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPPushMessaging_createValues
(JNIEnv *, jclass, jobject javaNameValueMap)
{
	JNIEnv *jni_env = 0;
	jclass cls;
	jmethodID method;
	jobject object;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native getValues called");

	jni_env = getEnv();
	ElementPtr coreEl = IPushMessaging::createValues(OpenPeerCoreManager::nameValueMapToCore(javaNameValueMap));
	ElementPtr* ptrToElement = new boost::shared_ptr<Element>(coreEl);
	cls = findClass("com/openpeer/javaapi/OPElement");
	method = jni_env->GetMethodID(cls, "<init>", "()V");
	object = jni_env->NewObject(cls, method);

	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong element = (jlong) ptrToElement;
	jni_env->SetLongField(object, fid, element);

	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    markPushMessageRead
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushMessaging_markPushMessageRead
(JNIEnv *, jobject owner, jstring messageID)
{
	JNIEnv *jni_env = 0;
	jclass cls;
	jmethodID method;
	jobject object;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native markPushMessageRead called");

	jni_env = getEnv();

	const char *messageIDStr;
	messageIDStr = jni_env->GetStringUTFChars(messageID, NULL);
	if (messageIDStr == NULL) {
		return;
	}

	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;
	if (coreMessagingPtr)
	{
		coreMessagingPtr->get()->markPushMessageRead(messageIDStr);
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native markPushMessageRead core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    deletePushMessage
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushMessaging_deletePushMessage
(JNIEnv *, jobject owner, jstring messageID)
{
	JNIEnv *jni_env = 0;
	jclass cls;
	jmethodID method;
	jobject object;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging native deletePushMessage called");

	jni_env = getEnv();

	const char *messageIDStr;
	messageIDStr = jni_env->GetStringUTFChars(messageID, NULL);
	if (messageIDStr == NULL) {
		return;
	}

	jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
	jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, messagingFid);

	IPushMessagingPtr* coreMessagingPtr = (IPushMessagingPtr*)pointerValue;
	if (coreMessagingPtr)
	{
		coreMessagingPtr->get()->deletePushMessage(messageIDStr);
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPPushMessaging native deletePushMessage core pointer is NULL!!!");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPPushMessaging
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPPushMessaging_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass messagingClass = findClass("com/openpeer/javaapi/OPPushMessaging");
		jfieldID messagingFid = jni_env->GetFieldID(messagingClass, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, messagingFid);

		delete (IPushMessagingPtr*)pointerValue;

		jfieldID delegateFid = jni_env->GetFieldID(messagingClass, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, delegateFid);

		delete (PushMessagingDelegateWrapperPtr*)delegatePointerValue;

		jfieldID dbDelegateFid = jni_env->GetFieldID(messagingClass, "nativeDatabaseAbstractionDelegatePointer", "J");
		jlong dbDelegatePointerValue = jni_env->GetLongField(javaObject, dbDelegateFid);

		delete (PushMessagingDatabaseAbstractionDelegateWrapperPtr*)dbDelegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPPushMessaging Core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPPushMessaging Core object not deleted - already NULL!");
	}
}
#ifdef __cplusplus
}
#endif
