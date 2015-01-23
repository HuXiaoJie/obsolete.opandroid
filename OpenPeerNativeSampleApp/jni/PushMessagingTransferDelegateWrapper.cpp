#include "PushMessagingTransferDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushMessagingTransferDelegateWrapper implementation
PushMessagingTransferDelegateWrapper::PushMessagingTransferDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushMessagingTransferDelegate implementation
void PushMessagingTransferDelegateWrapper::onPushMessagingTransferUploadFileDataToURL(
		IPushMessagingPtr session,
		const char *postURL,
		const char *fileNameContainingData,
		ULONGEST totalFileSizeInBytes,
		ULONGEST remainingBytesToUpload,
		IPushMessagingTransferNotifierPtr notifier
)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingTransferUploadFileDataToURL called");

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
		//create new OPPushMessagingQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushMessaging");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingPtr* ptrToPushMessaging = new std::shared_ptr<IPushMessaging>(session);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingObject, fid, (jlong)ptrToPushMessaging);

		jstring postURLJavaString =  jni_env->NewStringUTF(postURL);
		jstring fileNameContainingDataJavaString =  jni_env->NewStringUTF(fileNameContainingData);

		//create new OPPushMessagingQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushMessagingTranferNotifier");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingTransferNotifierObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingTransferNotifierPtr* ptrToPushMessagingTransferNotifier = new std::shared_ptr<IPushMessagingTransferNotifier>(notifier);
		fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingTransferNotifierObject, fid, (jlong)ptrToPushMessagingTransferNotifier);


		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingTransferUploadFileDataToURL", "(Lcom/openpeer/javaapi/OPPushMessaging;Ljava/lang/String;Ljava/lang/String;JJLcom/openpeer/javaapi/OPPushMessagingTransferNotifier;)V");
		jni_env->CallVoidMethod(javaDelegate, method,
				pushMessagingObject,
				postURLJavaString,
				fileNameContainingDataJavaString,
				(jlong) totalFileSizeInBytes,
				(jlong) remainingBytesToUpload,
				pushMessagingTransferNotifierObject
		);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingTransferUploadFileDataToURL Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void PushMessagingTransferDelegateWrapper::onPushMessagingTransferDownloadDataFromURL(
		IPushMessagingPtr session,
		const char *getURL,
		const char *fileNameToAppendData,
		ULONGEST finalFileSizeInBytes,
		ULONGEST remainingBytesToBeDownloaded,
		IPushMessagingTransferNotifierPtr notifier
)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushMessagingTransferDownloadDataFromURL called");

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
		//create new OPPushMessagingQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushMessaging");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingPtr* ptrToPushMessaging = new std::shared_ptr<IPushMessaging>(session);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingObject, fid, (jlong)ptrToPushMessaging);

		jstring getURLJavaString =  jni_env->NewStringUTF(getURL);
		jstring fileNameToAppendDataJavaString =  jni_env->NewStringUTF(fileNameToAppendData);

		//create new OPPushMessagingQuery java object
		cls = findClass("com/openpeer/javaapi/OPPushMessagingTranferNotifier");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushMessagingTransferNotifierObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushMessagingTransferNotifierPtr* ptrToPushMessagingTransferNotifier = new std::shared_ptr<IPushMessagingTransferNotifier>(notifier);
		fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushMessagingTransferNotifierObject, fid, (jlong)ptrToPushMessagingTransferNotifier);


		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushMessagingTransferUploadFileDataToURL", "(Lcom/openpeer/javaapi/OPPushMessaging;Ljava/lang/String;Ljava/lang/String;JJLcom/openpeer/javaapi/OPPushMessagingTransferNotifier;)V");
		jni_env->CallVoidMethod(javaDelegate, method,
				pushMessagingObject,
				getURLJavaString,
				fileNameToAppendDataJavaString,
				(jlong) finalFileSizeInBytes,
				(jlong) remainingBytesToBeDownloaded,
				pushMessagingTransferNotifierObject
		);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushMessagingTransferDownloadDataFromURL Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushMessagingTransferDelegateWrapper::~PushMessagingTransferDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
