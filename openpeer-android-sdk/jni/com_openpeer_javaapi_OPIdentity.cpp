#include "com_openpeer_javaapi_OPIdentity.h"
#include "openpeer/core/IStack.h"
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
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    toString
 * Signature: (Lcom/openpeer/javaapi/IdentityStates;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentity_toString
(JNIEnv *, jclass, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    toDebugString
 * Signature: (Lcom/openpeer/javaapi/OPIdentity;Ljava/lang/Boolean;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentity_toDebugString
(JNIEnv *, jclass, jobject, jobject)
{

}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    login
 * Signature: (Lcom/openpeer/javaapi/OPAccount;Lcom/openpeer/javaapi/OPIdentityDelegate;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/openpeer/javaapi/OPIdentity;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentity_login
(JNIEnv *env, jclass,
		jobject javaAccount,
		jobject javaIdentityDelegate,
		jstring identityProviderDomain,
		jstring identityURI_or_identityBaseURI,
		jstring outerFrameURLUponReload)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();

	const char *identityProviderDomainStr;
	identityProviderDomainStr = env->GetStringUTFChars(identityProviderDomain, NULL);
	if (identityProviderDomainStr == NULL) {
		return object;
	}

	const char *identityURIStr;
	identityURIStr = env->GetStringUTFChars(identityURI_or_identityBaseURI, NULL);
	if (identityURIStr == NULL) {
		return object;
	}

	const char *outerFrameURLUponReloadStr;
	outerFrameURLUponReloadStr = env->GetStringUTFChars(outerFrameURLUponReload, NULL);
	if (outerFrameURLUponReloadStr == NULL) {
		return object;
	}

	if (javaIdentityDelegate == NULL)
	{
		return object;
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//set java delegate to identity delegate wrapper and init shared pointer for wrappers
	IdentityDelegateWrapperPtr identityDelegatePtr = IdentityDelegateWrapperPtr(new IdentityDelegateWrapper(javaIdentityDelegate));

	IIdentityPtr identityPtr = IIdentity::login(*coreAccountPtr, identityDelegatePtr, (char const *)identityProviderDomainStr,
			(char const *)identityURIStr, (char const *)outerFrameURLUponReloadStr);

	if(identityPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPIdentity");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IIdentityPtr* ptrToIdentity = new boost::shared_ptr<IIdentity>(identityPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong identity = (jlong) ptrToIdentity;
			jni_env->SetLongField(object, fid, identity);

			if (identityDelegatePtr != NULL)
			{
				IdentityDelegateWrapperPtr* ptrToIdentityDelegateWrapperPtr= new boost::shared_ptr<IdentityDelegateWrapper>(identityDelegatePtr);
				jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
				jlong delegate = (jlong) ptrToIdentityDelegateWrapperPtr;
				jni_env->SetLongField(object, delegateFid, delegate);
			}

			__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni",
					"CorePtr raw = %p, ptr as long = %Lu",identityPtr.get(), identity);
		}
	}
	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    loginWithIdentityPreauthorized
 * Signature: (Lcom/openpeer/javaapi/OPAccount;Lcom/openpeer/javaapi/OPIdentityDelegate;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/text/format/Time;)Lcom/openpeer/javaapi/OPIdentity;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentity_loginWithIdentityPreauthorized
(JNIEnv *env, jclass,
		jobject javaAccount,
		jobject javaIdentityDelegate,
		jstring identityProviderDomain,
		jstring identityURI,
		jstring identityAccessToken,
		jstring identityAccessSecret,
		jobject identityAccessSecretExpires)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	const char *identityProviderDomainStr;
	identityProviderDomainStr = env->GetStringUTFChars(identityProviderDomain, NULL);
	if (identityProviderDomainStr == NULL) {
		return object;
	}

	const char *identityURIStr;
	identityURIStr = env->GetStringUTFChars(identityURI, NULL);
	if (identityURIStr == NULL) {
		return object;
	}

	const char *identityAccessTokenStr;
	identityAccessTokenStr = env->GetStringUTFChars(identityAccessToken, NULL);
	if (identityAccessTokenStr == NULL) {
		return object;
	}

	const char *identityAccessSecretStr;
	identityAccessSecretStr = env->GetStringUTFChars(identityAccessSecret, NULL);
	if (identityAccessSecretStr == NULL) {
		return object;
	}

	//todo fix time
	Time t;
	jni_env = getEnv();

	cls = findClass("android/text/format/Time");
	if(jni_env->IsInstanceOf(identityAccessSecretExpires, cls) == JNI_TRUE)
	{
		jmethodID timeMethodID   = jni_env->GetMethodID(cls, "toMillis", "(Z)J");
		jlong longValue = jni_env->CallLongMethod(identityAccessSecretExpires, timeMethodID, false);
		t = boost::posix_time::from_time_t(longValue/1000) + boost::posix_time::millisec(longValue % 1000);
	}

	if (javaIdentityDelegate == NULL)
	{
		return object;
	}

	jclass accountClass = findClass("com/openpeer/javaapi/OPAccount");
	jfieldID accountFid = jni_env->GetFieldID(accountClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(javaAccount, accountFid);

	IAccountPtr* coreAccountPtr = (IAccountPtr*)pointerValue;

	//set java delegate to identity delegate wrapper and init shared pointer for wrappers
	IdentityDelegateWrapperPtr identityDelegatePtr = IdentityDelegateWrapperPtr(new IdentityDelegateWrapper(javaIdentityDelegate));

	IIdentityPtr identityPtr = IIdentity::loginWithIdentityPreauthorized(*coreAccountPtr,
			identityDelegatePtr,
			(char const *)identityProviderDomainStr,
			(char const *)identityURIStr,
			(char const *)identityAccessTokenStr,
			(char const *)identityAccessSecretStr,
			t);

	if(identityPtr)
	{
		if(jni_env)
		{
			cls = findClass("com/openpeer/javaapi/OPIdentity");
			method = jni_env->GetMethodID(cls, "<init>", "()V");
			object = jni_env->NewObject(cls, method);

			IIdentityPtr* ptrToIdentity = new boost::shared_ptr<IIdentity>(identityPtr);
			jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
			jlong identity = (jlong) ptrToIdentity;
			jni_env->SetLongField(object, fid, identity);

			if (identityDelegatePtr != NULL)
			{
				IdentityDelegateWrapperPtr* ptrToIdentityDelegateWrapperPtr= new boost::shared_ptr<IdentityDelegateWrapper>(identityDelegatePtr);
				jfieldID delegateFid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
				jlong delegate = (jlong) ptrToIdentityDelegateWrapperPtr;
				jni_env->SetLongField(object, delegateFid, delegate);
			}

			__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni",
					"CorePtr raw = %p, ptr as long = %Lu",identityPtr.get(), identity);

		}
	}
	return object;

}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getState
 * Signature: (ILjava/lang/String;)Lcom/openpeer/javaapi/IdentityStates;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentity_getState
(JNIEnv *, jobject owner, jint, jstring)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;
	jint state = 0;
	unsigned short int outErrorCode;
	String outErrorReason;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;

	if (coreIdentityPtr)
	{
		state = (jint) coreIdentityPtr->get()->getState(&outErrorCode, &outErrorReason);
		if(jni_env)
		{
			object = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/IdentityStates", state);

		}
	}

	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getStableID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_openpeer_javaapi_OPIdentity_getStableID
(JNIEnv *, jobject owner)
{
	jlong pid = 0;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;

	if (coreIdentityPtr)
	{
		pid = coreIdentityPtr->get()->getID();
	}

	return pid;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    isDelegateAttached
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_com_openpeer_javaapi_OPIdentity_isDelegateAttached
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;
	bool ret = false;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;

	if (coreIdentityPtr)
	{
		ret = coreIdentityPtr->get()->isDelegateAttached();
	}

	return ret;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    attachDelegate
 * Signature: (Lcom/openpeer/javaapi/OPIdentityDelegate;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_attachDelegate
(JNIEnv *, jobject owner,
		jobject javaIdentityDelegate,
		jstring outerFrameURLUponReload)
{

	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	const char *outerFrameURLUponReloadStr;
	outerFrameURLUponReloadStr = jni_env->GetStringUTFChars(outerFrameURLUponReload, NULL);
	if (outerFrameURLUponReloadStr == NULL) {
		return;
	}

	if (javaIdentityDelegate == NULL)
	{
		return;
	}

	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;

	//set java delegate to identity delegate wrapper and init shared pointer for wrappers
	IdentityDelegateWrapperPtr identityDelegatePtr = IdentityDelegateWrapperPtr(new IdentityDelegateWrapper(javaIdentityDelegate));

	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->attachDelegate(identityDelegatePtr, outerFrameURLUponReloadStr);
		if (identityDelegatePtr != NULL)
		{
			IdentityDelegateWrapperPtr* ptrToIdentityDelegateWrapperPtr= new boost::shared_ptr<IdentityDelegateWrapper>(identityDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(identityClass, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToIdentityDelegateWrapperPtr;
			jni_env->SetLongField(owner, delegateFid, delegate);
		}
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    attachDelegateAndPreauthorizedLogin
 * Signature: (Lcom/openpeer/javaapi/OPIdentityDelegate;Ljava/lang/String;Ljava/lang/String;Landroid/text/format/Time;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_attachDelegateAndPreauthorizedLogin
(JNIEnv *env, jobject owner,
		jobject javaIdentityDelegate,
		jstring identityAccessToken,
		jstring identityAccessSecret,
		jobject identityAccessSecretExpires)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	const char *identityAccessTokenStr;
	identityAccessTokenStr = env->GetStringUTFChars(identityAccessToken, NULL);
	if (identityAccessTokenStr == NULL) {
		return;
	}

	const char *identityAccessSecretStr;
	identityAccessSecretStr = env->GetStringUTFChars(identityAccessSecret, NULL);
	if (identityAccessSecretStr == NULL) {
		return;
	}

	//todo fix time
	Time t;
	jni_env = getEnv();

	cls = findClass("android/text/format/Time");
	if(jni_env->IsInstanceOf(identityAccessSecretExpires, cls) == JNI_TRUE)
	{
		jmethodID timeMethodID   = jni_env->GetMethodID(cls, "toMillis", "(Z)J");
		jlong longValue = jni_env->CallLongMethod(identityAccessSecretExpires, timeMethodID, false);
		t = boost::posix_time::from_time_t(longValue/1000) + boost::posix_time::millisec(longValue % 1000);
	}

	if (javaIdentityDelegate == NULL)
	{
		return;
	}

	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;

	//set java delegate to identity delegate wrapper and init shared pointer for wrappers
	IdentityDelegateWrapperPtr identityDelegatePtr = IdentityDelegateWrapperPtr(new IdentityDelegateWrapper(javaIdentityDelegate));

	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->attachDelegateAndPreauthorizedLogin(
				identityDelegatePtr,
				(char const *)identityAccessTokenStr,
				(char const *)identityAccessSecretStr,
				t);
		if (identityDelegatePtr != NULL)
		{
			IdentityDelegateWrapperPtr* ptrToIdentityDelegateWrapperPtr= new boost::shared_ptr<IdentityDelegateWrapper>(identityDelegatePtr);
			jfieldID delegateFid = jni_env->GetFieldID(identityClass, "nativeDelegatePointer", "J");
			jlong delegate = (jlong) ptrToIdentityDelegateWrapperPtr;
			jni_env->SetLongField(owner, delegateFid, delegate);
		}

	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getIdentityURI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentity_getIdentityURI
(JNIEnv *, jobject owner)
{
	jstring identityURI;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		identityURI =  jni_env->NewStringUTF(coreIdentityPtr->get()->getIdentityURI().c_str());
	}

	return identityURI;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getIdentityProviderDomain
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentity_getIdentityProviderDomain
(JNIEnv *, jobject owner)
{
	jstring identityProviderDomain;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{

		identityProviderDomain =  jni_env->NewStringUTF(coreIdentityPtr->get()->getIdentityProviderDomain().c_str());
	}

	return identityProviderDomain;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getSelfIdentityContact
 * Signature: ()Lcom/openpeer/javaapi/OPIdentityContact;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentity_getSelfIdentityContact
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject object;

	IdentityContact coreContact;
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->getSelfIdentityContact(coreContact);

	}
	else
	{
		__android_log_print(ANDROID_LOG_INFO, "com.openpeer.jni", "IdentityPtr is NULL!!!");
		return object;
	}
	if(jni_env)
	{
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


		//set IdentityProofBundle to OPIdentityContact
		method = jni_env->GetMethodID(cls, "setIdentityProofBundle", "(Ljava/lang/String;)V");
		jstring identityProofBundle =  jni_env->NewStringUTF(IHelper::convertToString(coreContact.mIdentityProofBundleEl).c_str());
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

		//calculate and set Expires
		zsLib::Duration expires = coreContact.mExpires - time_t_epoch;
		jobject timeExpiresObject = jni_env->NewObject(timeCls, timeMethodID);
		jni_env->CallVoidMethod(timeExpiresObject, timeSetMillisMethodID, expires.total_milliseconds());
		//Time has been converted, now call OPIdentityContact setter
		method = jni_env->GetMethodID(cls, "setExpires", "(Landroid/text/format/Time;)V");
		jni_env->CallVoidMethod(object, method, timeExpiresObject);


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
		}

		//add avatar list to OPRolodexContact
		jni_env->CallVoidMethod(object, setAvatarsMethodID, avatarListObject);




	}
	return object;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getInnerBrowserWindowFrameURL
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentity_getInnerBrowserWindowFrameURL
(JNIEnv *, jobject owner)
{
	String innerBrowserWindowFrameURLString;
	jstring innerBrowserWindowFrameURL;


	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		innerBrowserWindowFrameURLString = coreIdentityPtr->get()->getInnerBrowserWindowFrameURL();

		innerBrowserWindowFrameURL =  jni_env->NewStringUTF(innerBrowserWindowFrameURLString.c_str());
	}

	return innerBrowserWindowFrameURL;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    notifyBrowserWindowVisible
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_notifyBrowserWindowVisible
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->notifyBrowserWindowVisible();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    notifyBrowserWindowClosed
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_notifyBrowserWindowClosed
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->notifyBrowserWindowClosed();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getNextMessageForInnerBrowerWindowFrame
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPIdentity_getNextMessageForInnerBrowerWindowFrame
(JNIEnv *, jobject owner)
{
	ElementPtr nextMessageForInnerBrowerWindowFrameElement;
	jstring nextMessageForInnerBrowerWindowFrame;

	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		nextMessageForInnerBrowerWindowFrameElement = coreIdentityPtr->get()->getNextMessageForInnerBrowerWindowFrame();

		nextMessageForInnerBrowerWindowFrame =  jni_env->NewStringUTF(IHelper::convertToString(nextMessageForInnerBrowerWindowFrameElement).c_str());
	}

	return nextMessageForInnerBrowerWindowFrame;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    handleMessageFromInnerBrowserWindowFrame
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_handleMessageFromInnerBrowserWindowFrame
(JNIEnv *, jobject owner, jstring unparsedMessage)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	String unparsedMessageString;
	unparsedMessageString = jni_env->GetStringUTFChars(unparsedMessage, NULL);
	if (unparsedMessageString == NULL) {
		return;
	}

	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->handleMessageFromInnerBrowserWindowFrame(IHelper::createElement(unparsedMessageString));
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    startRolodexDownload
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_startRolodexDownload
(JNIEnv *, jobject owner, jstring inLastDownloadedVersion)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		String inLastDownloadedVersionString;
		inLastDownloadedVersionString = jni_env->GetStringUTFChars(inLastDownloadedVersion, NULL);
		if (inLastDownloadedVersionString == NULL) {

			coreIdentityPtr->get()->startRolodexDownload();

		}
		else
		{

			coreIdentityPtr->get()->startRolodexDownload(inLastDownloadedVersionString);
		}
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "IdentityPtr is NULL");
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    refreshRolodexContacts
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_refreshRolodexContacts
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->refreshRolodexContacts();
	}
	else
	{
		__android_log_write(ANDROID_LOG_ERROR, "com.openpeer.jni", "IdentityPtr is NULL");
	}

}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    getDownloadedRolodexContacts
 * Signature: ()Lcom/openpeer/javaapi/OPDownloadedRolodexContacts;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPIdentity_getDownloadedRolodexContacts
(JNIEnv *, jobject owner)
{
	jclass cls;
	jmethodID method;
	jobject returnObject;
	jobject object;
	JNIEnv *jni_env = 0;

	RolodexContact coreRolodexContact;

	bool outSuccess;
	bool outFlushAllRolodexContacts;
	String outVersionDownloaded;
	RolodexContactListPtr outRolodexContacts;

	//take contacts from core conversation thread

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		outSuccess = coreIdentityPtr->get()->getDownloadedRolodexContacts(outFlushAllRolodexContacts,
				outVersionDownloaded,
				outRolodexContacts);

	}
	if(jni_env)
	{
		//create return structure - OPDownloadedRolodexContacts
		jclass returnObjectClass = findClass("com/openpeer/javaapi/OPDownloadedRolodexContacts");
		jmethodID returnObjectConstructorMethodID = jni_env->GetMethodID(returnObjectClass, "<init>", "()V");
		returnObject = jni_env->NewObject(returnObjectClass, returnObjectConstructorMethodID);

		//create return object list- java/util/List is interface, ArrayList is implementation
		jclass rolodexListClass = findClass("java/util/ArrayList");
		jmethodID listConstructorMethodID = jni_env->GetMethodID(rolodexListClass, "<init>", "()V");
		jobject rolodexListObject = jni_env->NewObject(rolodexListClass, listConstructorMethodID);
		if (outRolodexContacts)
		{
			//fetch List.add object
			jmethodID listAddMethodID = jni_env->GetMethodID(rolodexListClass, "add", "(Ljava/lang/Object;)Z");

			///////////////////////////////////////////////////////////////
			//FETCH DOWNLOADED ROLODEX CONTACTS METHODS TO SET INFO TO JAVA
			///////////////////////////////////////////////////////////////

			//Fetch setIsSuccess method from OPDownloadedRolodexContacts class
			jmethodID setIsSuccessMethodID = jni_env->GetMethodID( returnObjectClass, "setIsSuccess", "(Z)V" );
			//Fetch setFlushAllRolodexContacts method from OPDownloadedRolodexContacts class
			jmethodID setFlushAllRolodexContactsMethodID = jni_env->GetMethodID( returnObjectClass, "setFlushAllRolodexContacts", "(Z)V" );
			//Fetch setVersionDownloaded method from OPDownloadedRolodexContacts class
			jmethodID setVersionDownloadedMethodID = jni_env->GetMethodID( returnObjectClass, "setVersionDownloaded", "(Ljava/lang/String;)V" );
			//Fetch setRolodexContacts method from OPDownloadedRolodexContacts class
			jmethodID setRolodexContactsMethodID = jni_env->GetMethodID( returnObjectClass, "setRolodexContacts", "(Ljava/util/ArrayList;)V" );

			///////////////////////////////////////////////////////////////
			//CALL DOWNLOADED ROLODEX CONTACTS METHODS TO SET INFO TO JAVA
			///////////////////////////////////////////////////////////////

			// Call setIsSuccess method to set to OPDownloadedRolodexContacts
			jni_env->CallVoidMethod( returnObject, setIsSuccessMethodID, outSuccess);

			// Call setFlushAllRolodexContacts method to set to OPDownloadedRolodexContacts
			jni_env->CallVoidMethod( returnObject, setFlushAllRolodexContactsMethodID, outFlushAllRolodexContacts);

			// Call setVersionDownloaded method to set to OPDownloadedRolodexContacts
			jstring versionStr = jni_env->NewStringUTF(outVersionDownloaded.c_str());
			jni_env->CallVoidMethod( returnObject, setVersionDownloadedMethodID, versionStr);


			///////////////////////////////////////////////////////////////
			//FETCH ROLODEX CONTACT METHODS TO SET INFO TO JAVA
			///////////////////////////////////////////////////////////////

			//fetch OPRolodexContact class and constructor
			jclass rolodexContactClass = findClass("com/openpeer/javaapi/OPRolodexContact");
			jmethodID rolodexContactConstructorMethodID = jni_env->GetMethodID(rolodexContactClass, "<init>", "()V");

			//Fetch setDisposition method from OPDownloadedRolodexContacts class
			//jclass dispositionClass = findClass("com/openpeer/javaapi/OPRolodexContact$Dispositions");
			//jmethodID dispositionConstructorMethodID = jni_env->GetMethodID(cls, "<init>", "()V");
			jmethodID setDispositionMethodID = jni_env->GetMethodID( rolodexContactClass, "setDisposition", "(Lcom/openpeer/javaapi/OPRolodexContact$Dispositions;)V" );
			//Fetch setIdentityURI method from OPDownloadedRolodexContacts class
			jmethodID setIdentityURIMethodID = jni_env->GetMethodID( rolodexContactClass, "setIdentityURI", "(Ljava/lang/String;)V" );
			//Fetch setIdentityProvider method from OPDownloadedRolodexContacts class
			jmethodID setIdentityProviderMethodID = jni_env->GetMethodID( rolodexContactClass, "setIdentityProvider", "(Ljava/lang/String;)V" );
			//Fetch setName method from OPDownloadedRolodexContacts class
			jmethodID setNameMethodID = jni_env->GetMethodID( rolodexContactClass, "setName", "(Ljava/lang/String;)V" );
			//Fetch setProfileURL method from OPDownloadedRolodexContacts class
			jmethodID setProfileURLMethodID = jni_env->GetMethodID( rolodexContactClass, "setProfileURL", "(Ljava/lang/String;)V" );
			//Fetch setVProfileURL method from OPDownloadedRolodexContacts class
			jmethodID setVProfileURLMethodID = jni_env->GetMethodID( rolodexContactClass, "setVProfileURL", "(Ljava/lang/String;)V" );
			//Fetch setAvatars method from OPDownloadedRolodexContacts class
			jmethodID setAvatarsMethodID = jni_env->GetMethodID( rolodexContactClass, "setAvatars", "(Ljava/util/List;)V");


			//avatar list fetch
			jclass avatarListClass = findClass("java/util/ArrayList");
			jmethodID avatarListConstructorMethodID = jni_env->GetMethodID(avatarListClass, "<init>", "()V");

			jmethodID avatarListAddMethodID = jni_env->GetMethodID(avatarListClass, "add", "(Ljava/lang/Object;)Z");


			//OPAvatar class and methods fetch
			jclass avatarClass = findClass("com/openpeer/javaapi/OPRolodexContact$OPAvatar");
			jmethodID avatarConstructorMethodID = jni_env->GetMethodID(avatarClass, "<init>", "()V");
			jmethodID setAvatarNameMethodID = jni_env->GetMethodID(avatarClass, "setName", "(Ljava/lang/String;)V");
			jmethodID setAvatarURLMethodID = jni_env->GetMethodID(avatarClass, "setURL", "(Ljava/lang/String;)V");
			jmethodID setAvatarWidthMethodID = jni_env->GetMethodID(avatarClass, "setWidth", "(I)V");
			jmethodID setAvatarHeightMethodID = jni_env->GetMethodID(avatarClass, "setHeight", "(I)V");



			//Fill in Rolodex list with list from core
			for(RolodexContactList::iterator iter = outRolodexContacts->begin(); iter != outRolodexContacts->end(); iter ++)
			{
				coreRolodexContact = *iter;

				//create OPRolodexContact object
				jobject rolodexContactObject = jni_env->NewObject(rolodexContactClass, rolodexContactConstructorMethodID);

				//set Disposition to OPRolodexContact
				jobject dispositionObject = OpenPeerCoreManager::getJavaEnumObject("com/openpeer/javaapi/OPRolodexContact$Dispositions", (jint)coreRolodexContact.mDisposition);
				jni_env->CallVoidMethod(rolodexContactObject, setDispositionMethodID, dispositionObject);
				jni_env->DeleteLocalRef(dispositionObject);

				//set identity URI to OPRolodexContact
				jstring identityUriStr = jni_env->NewStringUTF(coreRolodexContact.mIdentityURI.c_str());
				jni_env->CallVoidMethod(rolodexContactObject, setIdentityURIMethodID, identityUriStr);
				jni_env->DeleteLocalRef(identityUriStr);

				//set identity provider to OPRolodexContact
				jstring identityProviderStr = jni_env->NewStringUTF(coreRolodexContact.mIdentityProvider.c_str());
				jni_env->CallVoidMethod(rolodexContactObject, setIdentityProviderMethodID, identityProviderStr);
				jni_env->DeleteLocalRef(identityProviderStr);

				//set name to OPRolodexContact
				jstring nameStr = jni_env->NewStringUTF(coreRolodexContact.mName.c_str());
				jni_env->CallVoidMethod(rolodexContactObject, setNameMethodID, nameStr);
				jni_env->DeleteLocalRef(nameStr);

				//set profile URL to OPRolodexContact
				jstring profileURLStr = jni_env->NewStringUTF(coreRolodexContact.mProfileURL.c_str());
				jni_env->CallVoidMethod(rolodexContactObject, setProfileURLMethodID, profileURLStr);
				jni_env->DeleteLocalRef(profileURLStr);

				//set v profile URL to OPRolodexContact
				jstring vProfileURLStr = jni_env->NewStringUTF(coreRolodexContact.mVProfileURL.c_str());
				jni_env->CallVoidMethod(rolodexContactObject, setVProfileURLMethodID, vProfileURLStr);
				jni_env->DeleteLocalRef(vProfileURLStr);

				//Avatar List object
				jobject avatarListObject = jni_env->NewObject(avatarListClass, avatarListConstructorMethodID);

				//set avatars to OPAvatarList
				for (RolodexContact::AvatarList::iterator avatarIter = coreRolodexContact.mAvatars.begin();
						avatarIter != coreRolodexContact.mAvatars.end(); ++avatarIter)
				{
					RolodexContact::Avatar coreAvatar = *avatarIter;
					//create OPAvatar object
					jobject avatarObject = jni_env->NewObject(avatarClass, avatarConstructorMethodID);

					//set avatar name to OPRolodexContact::OPAvatar
					jstring avatarNameStr = jni_env->NewStringUTF(coreAvatar.mName.c_str());
					jni_env->CallVoidMethod(avatarObject, setAvatarNameMethodID, avatarNameStr);
					jni_env->DeleteLocalRef(avatarNameStr);

					//set avatar URL to OPRolodexContact::OPAvatar
					//__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "avatar = %s",coreAvatar.mURL.c_str());
					jstring avatarURLStr = jni_env->NewStringUTF(coreAvatar.mURL.c_str());
					jni_env->CallVoidMethod(avatarObject, setAvatarURLMethodID, avatarURLStr);
					jni_env->DeleteLocalRef(avatarURLStr);

					//set avatar width to OPRolodexContact::OPAvatar
					jni_env->CallVoidMethod(avatarObject, setAvatarWidthMethodID, (jint)coreAvatar.mWidth);

					//set avatar height to OPRolodexContact::OPAvatar
					jni_env->CallVoidMethod(avatarObject, setAvatarHeightMethodID, (jint)coreAvatar.mHeight);

					//add avatar object to avatar list
					jboolean success = jni_env->CallBooleanMethod(avatarListObject, avatarListAddMethodID , avatarObject);
					jni_env->DeleteLocalRef(avatarObject);
				}

				//add avatar list to OPRolodexContact
				jni_env->CallVoidMethod(rolodexContactObject, setAvatarsMethodID, avatarListObject);
				jni_env->DeleteLocalRef(avatarListObject);

				// Call set method to set to OPDownloadedRolodexContacts
				jni_env->CallBooleanMethod( rolodexListObject, listAddMethodID, rolodexContactObject);
				jni_env->DeleteLocalRef(rolodexContactObject);

			}

			//add rolodex contact list to return object
			jni_env->CallVoidMethod(returnObject, setRolodexContactsMethodID, rolodexListObject);
		}

	}
	return returnObject;
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    cancel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_cancel
(JNIEnv *, jobject owner)
{
	JNIEnv *jni_env = 0;

	jni_env = getEnv();
	jclass identityClass = findClass("com/openpeer/javaapi/OPIdentity");
	jfieldID identityFid = jni_env->GetFieldID(identityClass, "nativeClassPointer", "J");
	jlong pointerValue = jni_env->GetLongField(owner, identityFid);

	IIdentityPtr* coreIdentityPtr = (IIdentityPtr*)pointerValue;
	if (coreIdentityPtr)
	{
		coreIdentityPtr->get()->cancel();
	}
}

/*
 * Class:     com_openpeer_javaapi_OPIdentity
 * Method:    releaseCoreObjects
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPIdentity_releaseCoreObjects
(JNIEnv *, jobject javaObject)
{
	if(javaObject != NULL)
	{
		JNIEnv *jni_env = getEnv();
		jclass cls = findClass("com/openpeer/javaapi/OPIdentity");
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jlong pointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IIdentityPtr*)pointerValue;

		fid = jni_env->GetFieldID(cls, "nativeDelegatePointer", "J");
		jlong delegatePointerValue = jni_env->GetLongField(javaObject, fid);

		delete (IdentityDelegateWrapperPtr*)delegatePointerValue;
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
