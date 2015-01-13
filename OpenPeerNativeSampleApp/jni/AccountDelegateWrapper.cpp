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
		IAccountPtr* ptrToAccount = new std::shared_ptr<IAccount>(account);
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
		IAccountPtr* ptrToAccount = new std::shared_ptr<IAccount>(account);
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
		IAccountPtr* ptrToAccount = new std::shared_ptr<IAccount>(account);
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
