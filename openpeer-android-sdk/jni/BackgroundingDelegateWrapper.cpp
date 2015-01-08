#include "globals.h"
#include "BackgroundingDelegateWrapper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"
#include "openpeer/services/IHelper.h"

//IBackgroundingDelegate implementation
BackgroundingDelegateWrapper::BackgroundingDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

void BackgroundingDelegateWrapper::onBackgroundingGoingToBackground(
                                              IBackgroundingSubscriptionPtr subscription,
                                              IBackgroundingNotifierPtr notifier
                                              )
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onBackgroundingGoingToBackground called");

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

	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		//create new OPBackgroundingSubscription java object
		cls = findClass("com/openpeer/javaapi/OPBackgroundingSubscription");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject subsciptionObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IBackgroundingSubscriptionPtr* ptrToSubscription = new std::shared_ptr<IBackgroundingSubscription>(subscription);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(subsciptionObject, fid, (jlong)ptrToSubscription);

		//create new OPBackgroundingNotifier java object
		cls = findClass("com/openpeer/javaapi/OPBackgroundingNotifier");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject notifierObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IBackgroundingNotifierPtr* ptrToNotifier = new std::shared_ptr<IBackgroundingNotifier>(notifier);
		fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(notifierObject, fid, (jlong)ptrToNotifier);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass,
				"onBackgroundingGoingToBackground",
				"(Lcom/openpeer/javaapi/OPBackgroundingSubscription;Lcom/openpeer/javaapi/OPBackgroundingNotifier;)V");
		jni_env->CallVoidMethod(javaDelegate, method, subsciptionObject, notifierObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onBackgroundingGoingToBackground Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void BackgroundingDelegateWrapper::onBackgroundingGoingToBackgroundNow(
                                                 IBackgroundingSubscriptionPtr subscription
                                                 )
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onBackgroundingGoingToBackgroundNow called");

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

	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		//create new OPBackgroundingSubscription java object
		cls = findClass("com/openpeer/javaapi/OPBackgroundingSubscription");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject subsciptionObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IBackgroundingSubscriptionPtr* ptrToSubscription = new std::shared_ptr<IBackgroundingSubscription>(subscription);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(subsciptionObject, fid, (jlong)ptrToSubscription);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass,
				"onBackgroundingGoingToBackgroundNow",
				"(Lcom/openpeer/javaapi/OPBackgroundingSubscription;)V");
		jni_env->CallVoidMethod(javaDelegate, method, subsciptionObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onBackgroundingGoingToBackgroundNow Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void BackgroundingDelegateWrapper::onBackgroundingReturningFromBackground(
                                                    IBackgroundingSubscriptionPtr subscription
                                                    )
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onBackgroundingReturningFromBackground called");

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

	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		//create new OPBackgroundingSubscription java object
		cls = findClass("com/openpeer/javaapi/OPBackgroundingSubscription");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject subsciptionObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IBackgroundingSubscriptionPtr* ptrToSubscription = new std::shared_ptr<IBackgroundingSubscription>(subscription);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(subsciptionObject, fid, (jlong)ptrToSubscription);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass,
				"onBackgroundingReturningFromBackground",
				"(Lcom/openpeer/javaapi/OPBackgroundingSubscription;)V");
		jni_env->CallVoidMethod(javaDelegate, method, subsciptionObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onBackgroundingReturningFromBackground Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void BackgroundingDelegateWrapper::onBackgroundingApplicationWillQuit(
                                                IBackgroundingSubscriptionPtr subscription
                                                )
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onBackgroundingApplicationWillQuit called");

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

	if (javaDelegate != NULL)
	{

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		//create new OPBackgroundingSubscription java object
		cls = findClass("com/openpeer/javaapi/OPBackgroundingSubscription");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject subsciptionObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IBackgroundingSubscriptionPtr* ptrToSubscription = new std::shared_ptr<IBackgroundingSubscription>(subscription);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(subsciptionObject, fid, (jlong)ptrToSubscription);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass,
				"onBackgroundingApplicationWillQuit",
				"(Lcom/openpeer/javaapi/OPBackgroundingSubscription;)V");
		jni_env->CallVoidMethod(javaDelegate, method, subsciptionObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onBackgroundingApplicationWillQuit Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

BackgroundingDelegateWrapper::~BackgroundingDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);
}
