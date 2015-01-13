#include "openpeer/core/IIdentityLookup.h"
#include "openpeer/core/ILogger.h"
#include "openpeer/core/IHelper.h"
#include "OpenPeerCoreManager.h"
#include <android/log.h>

#include "globals.h"

using namespace openpeer::core;

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    toDebugString
 * Signature: (Lcom/openpeer/javaapi/OPIdentityLookup;Z)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_toDebugString
(JNIEnv *, jclass, jobject, jboolean)
{

}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    create
 * Signature: (Lcom/openpeer/javaapi/OPAccount;Lcom/openpeer/javaapi/OPIdentityLookupDelegate;Ljava/util/List;Ljava/lang/String;)Lcom/openpeer/javaapi/OPIdentityLookup;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_create
(JNIEnv *env, jclass,
		jobject javaAccount,
		jobject javaIdentityLookupDelegate,
		jobject identityLookupInfos,
		jstring identityServiceDomain)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native create called");

	const char *identityServiceDomainStr;
	identityServiceDomainStr = env->GetStringUTFChars(identityServiceDomain, NULL);
	if (identityServiceDomainStr == NULL) {
		return object;
	}

	if (javaIdentityLookupDelegate == NULL)
	{
		return object;
	}

	//Core contact profile list
	IIdentityLookup::IdentityLookupInfoList identityLookupInfosForCore;

	//fetch JNI env
	jni_env = getEnv();
	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass arrayListClass = findClass("java/util/ArrayList");
		if(jni_env->IsInstanceOf(identityLookupInfos, arrayListClass) != JNI_TRUE)
		{
			return object;
		}
		// Fetch "java.util.List.get(int location)" MethodID
		jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");
		// Fetch "int java.util.List.size()" MethodID
		jmethodID sizeMethodID = jni_env->GetMethodID( arrayListClass, "size", "()I" );

		// Call "int java.util.List.size()" method and get count of items in the list.
		int listItemsCount = (int)jni_env->CallIntMethod( identityLookupInfos, sizeMethodID );

		//Fetch OPIdentityLookupInfo class
		jclass identityLookupInfoClass = findClass("com/openpeer/javaapi/OPIdentityLookupInfo");
		//Fetch getIdentityURI method from OPIdentityLookupInfo class
		jmethodID getIdentityURIMethodID = jni_env->GetMethodID( identityLookupInfoClass, "getIdentityURI", "()Ljava/lang/String;" );
		//Fetch getLastUpdated method from OPIdentityLookupInfo class
		jmethodID getLastUpdatedMethodID = jni_env->GetMethodID( identityLookupInfoClass, "getLastUpdated", "()Landroid/text/format/Time;" );
		jclass timeCls = findClass("android/text/format/Time");
		jmethodID timeMethodID   = jni_env->GetMethodID(timeCls, "toMillis", "(Z)J");

		for( int i=0; i<listItemsCount; ++i )
		{
			// Call "java.util.List.get" method and get Contact object by index.
			jobject identityLookupInfoObject = jni_env->CallObjectMethod( identityLookupInfos, listGetMethodID, i );

			if( identityLookupInfoObject != NULL )
			{
				// Call "getIdentityURI method to fetch contact from Identity lookup info
				jstring identityURI = (jstring)jni_env->CallObjectMethod( identityLookupInfoObject, getIdentityURIMethodID );

				// Call "getLastUpdated method to fetch last updated from Identity lookup info
				jobject lastUpdated = jni_env->CallObjectMethod( identityLookupInfoObject, getLastUpdatedMethodID );

				jni_env->DeleteLocalRef(identityLookupInfoObject);

				//Add identity URI to IdentityLookupInfo structure
				IIdentityLookup::IdentityLookupInfo identityLookupInfo;
				const char * tempURI = jni_env->GetStringUTFChars(identityURI, NULL);
				identityLookupInfo.mIdentityURI = tempURI;
				jni_env->ReleaseStringUTFChars(identityURI, tempURI);
				jni_env->DeleteLocalRef(identityURI);

				//Add last updated to IdentityLookupInfo structure
				jlong longValue = jni_env->CallLongMethod(lastUpdated, timeMethodID, false);
				jni_env->DeleteLocalRef(lastUpdated);
				Time t = boost::posix_time::from_time_t(longValue/1000) + boost::posix_time::millisec(longValue % 1000);
				identityLookupInfo.mLastUpdated = t;

				//add core contacts to list for removal
				identityLookupInfosForCore.push_front(identityLookupInfo);
			}

		}
		jni_env->DeleteLocalRef(timeCls);
		jni_env->DeleteLocalRef(identityLookupInfoClass);
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//set java delegate to identity delegate wrapper and init shared pointer for wrappers
	IdentityLookupDelegateWrapperPtr identityLookupDelegatePtr = IdentityLookupDelegateWrapperPtr(new IdentityLookupDelegateWrapper(javaIdentityLookupDelegate));

	IIdentityLookupPtr identityLookup = IIdentityLookup::create(*coreAccountPtr,
			identityLookupDelegatePtr,
			identityLookupInfosForCore,
			identityServiceDomainStr);

	if(identityLookup)
	{
		IIdentityLookupPtr* ptrToIdentityLookup = new std::shared_ptr<IIdentityLookup>(identityLookup);
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPIdentityLookup");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong lookup = (jlong) ptrToIdentityLookup;
			jni_env->SetLongField(object, fid, lookup);

			if (identityLookupDelegatePtr != NULL)
			{
				IdentityLookupDelegateWrapperPtr* ptrToIdentityLookupDelegateWrapperPtr= new std::shared_ptr<IdentityLookupDelegateWrapper>(identityLookupDelegatePtr);
				jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
				jlong delegate = (jlong) ptrToIdentityLookupDelegateWrapperPtr;
				jni_env->SetLongField(object, delegateFid, delegate);
			}

		}
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native create called");
	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    getStableID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_getStableID
(JNIEnv *, jobject owner)
{
	jlong pid = 0;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native getStableID called");

	jni_env = getEnv();
	jclass identityLookupClass = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID identityLookupFid = jni_env->GetFieldID(identityLookupClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityLookupFid);

	IIdentityLookupPtr* coreIdentityLookupPtr = (IIdentityLookupPtr*)pointerValue;
	if (coreIdentityLookupPtr)
	{
		pid = coreIdentityLookupPtr->get()->getID();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native getStableID called");
	}
	return pid;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    isComplete
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_isComplete
(JNIEnv *, jobject owner)
{
	jboolean ret = 0;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native isComplete called");

	jni_env = getEnv();
	jclass identityLookupClass = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID identityLookupFid = jni_env->GetFieldID(identityLookupClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityLookupFid);

	IIdentityLookupPtr* coreIdentityLookupPtr = (IIdentityLookupPtr*)pointerValue;

	if (coreIdentityLookupPtr)
	{
		ret = coreIdentityLookupPtr->get()->isComplete();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native isComplete called");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    wasSuccessful
 * Signature: (ILjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_wasSuccessful
(JNIEnv *, jobject owner, jint outErrorCode, jstring outErrorReason)
{
	jboolean ret = 0;
	String outReasonCore;
	WORD ourCodeCore;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native wasSuccessful called");

	jni_env = getEnv();
	jclass identityLookupClass = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID identityLookupFid = jni_env->GetFieldID(identityLookupClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityLookupFid);

	IIdentityLookupPtr* coreIdentityLookupPtr = (IIdentityLookupPtr*)pointerValue;

	if (coreIdentityLookupPtr)
	{
		ret = coreIdentityLookupPtr->get()->wasSuccessful(&ourCodeCore, &outReasonCore);
		outErrorCode = ourCodeCore;
		outErrorReason = jni_env->NewStringUTF(outReasonCore);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native wasSuccessful called");
	}
	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    cancel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_cancel
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native cancel called");

	jni_env = getEnv();
	jclass identityLookupClass = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID identityLookupFid = jni_env->GetFieldID(identityLookupClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityLookupFid);

	IIdentityLookupPtr* coreIdentityLookupPtr = (IIdentityLookupPtr*)pointerValue;
	if (coreIdentityLookupPtr)
	{
		coreIdentityLookupPtr->get()->cancel();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native cancel called");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    getUpdatedIdentities
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_getUpdatedIdentities
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject returnListObject;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native getUpdatedIdentities called");

	jni_env = getEnv();
	cls = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, fid);

	IIdentityLookupPtr* identityLookupPtr = (IIdentityLookupPtr*)pointerValue;

	IdentityContact coreContact;
	IdentityContactListPtr coreContactList;
	if(identityLookupPtr)
	{
		coreContactList = identityLookupPtr->get()->getUpdatedIdentities();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native getUpdatedIdentities called");
	}

	if(jni_env)
	{
		//create return object - java/util/List is interface, ArrayList is implementation
		jclass returnListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(returnListClass, "<init>", "()V");
		returnListObject = jni_env->NewObject(returnListClass, listConstructorMethodID);


		//fetch List.add object
		jmethodID listAddMethodID = jni_env->GetMethodID(returnListClass, "add", "(Ljava/lang/Object;)Z");

		for(IdentityContactList::iterator iter = coreContactList->begin(); iter != coreContactList->end(); iter ++)
		{
			coreContact = *iter;

			cls = findClass("com/openpeer/javaapi/OPIdentityContact");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			//set Stable ID to OPIdentityContact
			method = jni_env->GetMethodID(cls, "setStableID", "(Ljava/lang/String;)V");
			jstring stableID =  jni_env->NewStringUTF(coreContact.mStableID.c_str());
			jni_env->CallVoidMethod(object, method, stableID);

			//set Public Peer File to OPIdentityContact
			//TODO export peer file public to ElementPtr and then convert to String
			jclass peerFileCls = findClass("com/openpeer/javaapi/OPPeerFilePublic");
			jmethodID peerFileMethodID = jni_env->GetMethodID(peerFileCls, "<init>", "()V");
			jobject peerFileObject = jni_env->NewObject(peerFileCls, peerFileMethodID);

			jfieldID fid = jni_env->GetFieldID(peerFileCls, "mPeerFileString", "Ljava/lang/String;");
			ElementPtr peerFilePublicEl = IHelper::convertToElement(coreContact.mPeerFilePublic);
			jstring peerFileString = jni_env->NewStringUTF(IHelper::convertToString(peerFilePublicEl).c_str());
			//jlong peerFilePtr = (jlong) coreContact.mPeerFilePublic;
			jni_env->SetObjectField(peerFileObject, fid, peerFileString);

			method = jni_env->GetMethodID(cls, "setPeerFilePublic", "(Lcom/openpeer/javaapi/OPPeerFilePublic;)V");
			jni_env->CallVoidMethod(object, method, peerFileObject);

			jni_env->DeleteLocalRef(peerFileObject);


			//set IdentityProofBundle to OPIdentityContact
			method = jni_env->GetMethodID(cls, "setIdentityProofBundle", "(Ljava/lang/String;)V");
			String proofElementString = IHelper::convertToString(coreContact.mIdentityProofBundleEl);
			jstring identityProofBundle =  jni_env->NewStringUTF(proofElementString.c_str());
			jni_env->CallVoidMethod(object, method, identityProofBundle);

			//set Priority to OPIdentityContact
			method = jni_env->GetMethodID(cls, "setPriority", "(I)V");
			jni_env->CallVoidMethod(object, method, (int)coreContact.mPriority);

			//set Weight to OPIdentityContact
			method = jni_env->GetMethodID(cls, "setWeight", "(I)V");
			jni_env->CallVoidMethod(object, method, (int)coreContact.mWeight);

			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");

			//calculate and set Last Updated
			zsLib::Duration lastUpdated = coreContact.mLastUpdated - time_t_epoch;
			jobject timeLastUpdatedObject = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(timeLastUpdatedObject, timeSetMillisMethodID, lastUpdated.total_milliseconds());
			//Time has been converted, now call OPIdentityContact setter
			method = jni_env->GetMethodID(cls, "setLastUpdated", "(Landroid/text/format/Time;)V");
			jni_env->CallVoidMethod(object, method, timeLastUpdatedObject);

			jni_env->DeleteLocalRef(timeLastUpdatedObject);

			//calculate and set Expires
			zsLib::Duration expires = coreContact.mExpires - time_t_epoch;
			jobject timeExpiresObject = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(timeExpiresObject, timeSetMillisMethodID, expires.total_milliseconds());
			//Time has been converted, now call OPIdentityContact setter
			method = jni_env->GetMethodID(cls, "setExpires", "(Landroid/text/format/Time;)V");
			jni_env->CallVoidMethod(object, method, timeExpiresObject);

			jni_env->DeleteLocalRef(timeExpiresObject);

			///////////////////////////////////////////////////////////////
			// SET ROLODEX CONTACT FIELDS
			//////////////////////////////////////////////////////////////

			//Fetch setDisposition method from OPDownloadedRolodexContacts class
			//jclass dispositionClass = findClass("com/openpeer/javaapi/OPRolodexContact$Dispositions");
			//jmethodID dispositionConstructorMethodID = jni_env->GetMethodID(cls, "<init>", "()V");
			jmethodID setDispositionMethodID = jni_env->GetMethodID( cls, "setDisposition", "(Lcom/openpeer/javaapi/OPRolodexContact$Dispositions;)V" );
			//Fetch setIdentityURI method from OPDownloadedRolodexContacts class
			jmethodID setIdentityURIMethodID = jni_env->GetMethodID( cls, "setIdentityURI", "(Ljava/lang/String;)V" );
			//Fetch setIdentityProvider method from OPDownloadedRolodexContacts class
			jmethodID setIdentityProviderMethodID = jni_env->GetMethodID( cls, "setIdentityProvider", "(Ljava/lang/String;)V" );
			//Fetch setName method from OPDownloadedRolodexContacts class
			jmethodID setNameMethodID = jni_env->GetMethodID( cls, "setName", "(Ljava/lang/String;)V" );
			//Fetch setProfileURL method from OPDownloadedRolodexContacts class
			jmethodID setProfileURLMethodID = jni_env->GetMethodID( cls, "setProfileURL", "(Ljava/lang/String;)V" );
			//Fetch setVProfileURL method from OPDownloadedRolodexContacts class
			jmethodID setVProfileURLMethodID = jni_env->GetMethodID( cls, "setVProfileURL", "(Ljava/lang/String;)V" );
			//Fetch setAvatars method from OPDownloadedRolodexContacts class
			jmethodID setAvatarsMethodID = jni_env->GetMethodID( cls, "setAvatars", "(Ljava/util/List;)V");


			//avatar list fetch
			jclass avatarListClass = findClass("java/util/ArrayList");
			jmethodID avatarListConstructorMethodID = jni_env->GetMethodID(avatarListClass, "<init>", "()V");
			jobject avatarListObject = jni_env->NewObject(avatarListClass, avatarListConstructorMethodID);
			jmethodID avatarListAddMethodID = jni_env->GetMethodID(avatarListClass, "add", "(Ljava/lang/Object;)Z");


			//OPAvatar class and methods fetch
			jclass avatarClass = findClass("com/openpeer/javaapi/OPRolodexContact$OPAvatar");
			jmethodID avatarConstructorMethodID = jni_env->GetMethodID(avatarClass, "<init>", "()V");
			jmethodID setAvatarNameMethodID = jni_env->GetMethodID(avatarClass, "setName", "(Ljava/lang/String;)V");
			jmethodID setAvatarURLMethodID = jni_env->GetMethodID(avatarClass, "setURL", "(Ljava/lang/String;)V");
			jmethodID setAvatarWidthMethodID = jni_env->GetMethodID(avatarClass, "setWidth", "(I)V");
			jmethodID setAvatarHeightMethodID = jni_env->GetMethodID(avatarClass, "setHeight", "(I)V");

			//set Disposition to OPRolodexContact
			jobject dispositionObject = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/OPRolodexContact$Dispositions", (jint)coreContact.mDisposition);
			jni_env->CallVoidMethod(object, setDispositionMethodID, dispositionObject);

			//set identity URI to OPRolodexContact
			jstring identityUriStr = jni_env->NewStringUTF(coreContact.mIdentityURI.c_str());
			jni_env->CallVoidMethod(object, setIdentityURIMethodID, identityUriStr);

			//set identity provider to OPRolodexContact
			jstring identityProviderStr = jni_env->NewStringUTF(coreContact.mIdentityProvider.c_str());
			jni_env->CallVoidMethod(object, setIdentityProviderMethodID, identityProviderStr);

			//set name to OPRolodexContact
			jstring nameStr = jni_env->NewStringUTF(coreContact.mName.c_str());
			jni_env->CallVoidMethod(object, setNameMethodID, nameStr);

			//set profile URL to OPRolodexContact
			jstring profileURLStr = jni_env->NewStringUTF(coreContact.mProfileURL.c_str());
			jni_env->CallVoidMethod(object, setProfileURLMethodID, profileURLStr);

			//set v profile URL to OPRolodexContact
			jstring vProfileURLStr = jni_env->NewStringUTF(coreContact.mVProfileURL.c_str());
			jni_env->CallVoidMethod(object, setVProfileURLMethodID, vProfileURLStr);

			//set avatars to OPAvatarList
			for (RolodexContact::AvatarList::iterator avatarIter = coreContact.mAvatars.begin();
					avatarIter != coreContact.mAvatars.end(); avatarIter++)
			{
				RolodexContact::Avatar coreAvatar = *avatarIter;
				//create OPAvatar object
				jobject avatarObject = jni_env->NewObject(avatarClass, avatarConstructorMethodID);

				//set avatar name to OPRolodexContact::OPAvatar
				jstring avatarNameStr = jni_env->NewStringUTF(coreAvatar.mName.c_str());
				jni_env->CallVoidMethod(avatarObject, setAvatarNameMethodID, avatarNameStr);

				//set avatar URL to OPRolodexContact::OPAvatar
				jstring avatarURLStr = jni_env->NewStringUTF(coreAvatar.mURL.c_str());
				jni_env->CallVoidMethod(avatarObject, setAvatarURLMethodID, avatarURLStr);

				//set avatar width to OPRolodexContact::OPAvatar
				jni_env->CallVoidMethod(avatarObject, setAvatarWidthMethodID, (jint)coreAvatar.mWidth);

				//set avatar height to OPRolodexContact::OPAvatar
				jni_env->CallVoidMethod(avatarObject, setAvatarHeightMethodID, (jint)coreAvatar.mHeight);

				//add avatar object to avatar list
				jboolean success = jni_env->CallBooleanMethod(avatarListObject, avatarListAddMethodID , avatarObject);

				jni_env->DeleteLocalRef(avatarObject);
			}

			//add avatar list to OPRolodexContact
			jni_env->CallVoidMethod(object, setAvatarsMethodID, avatarListObject);

			jni_env->DeleteLocalRef(avatarListObject);

			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , object);

		}

	}
	return returnListObject;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    getUnchangedIdentities
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_getUnchangedIdentities
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject returnListObject;

	//Core Identity lookup info list
	IIdentityLookup::IdentityLookupInfoListPtr coreIdentityLookupInfoList;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native getUnchangedIdentities called");

	jni_env = getEnv();
	jclass identityLookupClass = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID identityLookupFid = jni_env->GetFieldID(identityLookupClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityLookupFid);

	IIdentityLookupPtr* coreIdentityLookupPtr = (IIdentityLookupPtr*)pointerValue;

	//take list from core identity lookup
	if (coreIdentityLookupPtr)
	{
		coreIdentityLookupInfoList = coreIdentityLookupPtr->get()->getUnchangedIdentities();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native getUnchangedIdentities called");
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
		for(IIdentityLookup::IdentityLookupInfoList::iterator coreListIter = coreIdentityLookupInfoList->begin();
				coreListIter != coreIdentityLookupInfoList->end(); coreListIter++)
		{
			//fetch List item object / OPIdentityLookupInfo
			jclass identityLookupInfoClass = findClass("com/openpeer/javaapi/OPIdentityLookupInfo");
			jmethodID identityLookupInfoConstructorMethodID = jni_env->GetMethodID(identityLookupInfoClass, "<init>", "()V");
			jobject identityLookupInfoObject = jni_env->NewObject(identityLookupInfoClass, identityLookupInfoConstructorMethodID);

			//Fetch setIdentityURI method from OPIdentityLookupInfo class
			jmethodID setIdentityURIMethodID = jni_env->GetMethodID( identityLookupInfoClass, "setIdentityURI", "(Ljava/lang/String;)V" );
			//Fetch setLastUpdated method from OPIdentityLookupInfo class
			jmethodID setLastUpdatedMethodID = jni_env->GetMethodID( identityLookupInfoClass, "setLastUpdated", "(Landroid/text/format/Time;)V" );

			// Call "setIdentityURI method to fetch contact from Identity lookup info
			jstring identityURI = jni_env->NewStringUTF(coreListIter->mIdentityURI.c_str());
			jni_env->CallObjectMethod( identityLookupInfoObject, setIdentityURIMethodID, identityURI );

			// Call "setLastUpdated method to fetch contact from Identity lookup info
			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");
			//calculate and set Ring time
			zsLib::Duration creationTimeDuration = coreListIter->mLastUpdated - time_t_epoch;
			jobject lastUpdatedObject = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(lastUpdatedObject, timeSetMillisMethodID, creationTimeDuration.total_milliseconds());
			jni_env->CallObjectMethod( identityLookupInfoObject, setLastUpdatedMethodID, lastUpdatedObject);

			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , identityLookupInfoObject);

		}
	}
	return returnListObject;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    getInvalidIdentities
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_getInvalidIdentities
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject returnListObject;

	//Core Identity lookup info list
	IIdentityLookup::IdentityLookupInfoListPtr coreIdentityLookupInfoList;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup native getInvalidIdentities called");

	jni_env = getEnv();
	jclass identityLookupClass = findClass("com/openpeer/javaapi/OPIdentityLookup");
	jfieldID identityLookupFid = jni_env->GetFieldID(identityLookupClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityLookupFid);

	IIdentityLookupPtr* coreIdentityLookupPtr = (IIdentityLookupPtr*)pointerValue;

	//take list from core identity lookup
	if (coreIdentityLookupPtr)
	{
		coreIdentityLookupInfoList = coreIdentityLookupPtr->get()->getInvalidIdentities();
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "OPIdentityLookup native getInvalidIdentities called");
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
		for(IIdentityLookup::IdentityLookupInfoList::iterator coreListIter = coreIdentityLookupInfoList->begin();
				coreListIter != coreIdentityLookupInfoList->end(); coreListIter++)
		{
			//fetch List item object / OPIdentityLookupInfo
			jclass identityLookupInfoClass = findClass("com/openpeer/javaapi/OPIdentityLookupInfo");
			jmethodID identityLookupInfoConstructorMethodID = jni_env->GetMethodID(identityLookupInfoClass, "<init>", "()V");
			jobject identityLookupInfoObject = jni_env->NewObject(identityLookupInfoClass, identityLookupInfoConstructorMethodID);

			//Fetch setIdentityURI method from OPIdentityLookupInfo class
			jmethodID setIdentityURIMethodID = jni_env->GetMethodID( identityLookupInfoClass, "setIdentityURI", "(Ljava/lang/String;)V" );
			//Fetch setLastUpdated method from OPIdentityLookupInfo class
			jmethodID setLastUpdatedMethodID = jni_env->GetMethodID( identityLookupInfoClass, "setLastUpdated", "(Landroid/text/format/Time;)V" );

			// Call "setIdentityURI method to fetch contact from Identity lookup info
			jstring identityURI = jni_env->NewStringUTF(coreListIter->mIdentityURI.c_str());
			jni_env->CallObjectMethod( identityLookupInfoObject, setIdentityURIMethodID, identityURI );

			// Call "setLastUpdated method to fetch contact from Identity lookup info
			//Convert and set time from C++ to Android; Fetch methods needed to accomplish this
			Time time_t_epoch = boost::posix_time::time_from_string("1970-01-01 00:00:00.000");
			jclass timeCls = findClass("android/text/format/Time");
			jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "<init>", "()V");
			jmethodID timeSetMillisMethodID   = jni_env->GetMethodID(timeCls, "set", "(J)V");
			//calculate and set Ring time
			zsLib::Duration creationTimeDuration = coreListIter->mLastUpdated - time_t_epoch;
			jobject lastUpdatedObject = jni_env->NewObject(timeCls, timeMethodID);
			jni_env->CallVoidMethod(lastUpdatedObject, timeSetMillisMethodID, creationTimeDuration.total_milliseconds());
			jni_env->CallObjectMethod( identityLookupInfoObject, setLastUpdatedMethodID, lastUpdatedObject);

			//add to return List
			jboolean success = jni_env->CallBooleanMethod(returnListObject,listAddMethodID , identityLookupInfoObject);

		}
	}
	return returnListObject;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentityLookup
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentityLookup_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPIdentityLookup");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IIdentityLookupPtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IdentityLookupDelegateWrapperPtr*)delegatePointerValue;
		__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "OPIdentityLookup core object deleted.");

	}
	else
	{
		__android_log_print(ANDROID_LOG_WARN, "com.openpeer.jni", "OPIdentityLookup ore object not deleted - already NULL!");
	}
}

#ifdef __cplusplus
}
#endif
