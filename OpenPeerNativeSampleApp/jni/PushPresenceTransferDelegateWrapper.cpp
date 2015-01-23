#include "PushPresenceTransferDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//PushPresenceTransferDelegateWrapper implementation
PushPresenceTransferDelegateWrapper::PushPresenceTransferDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IPushPresenceTransferDelegate implementation
void PushPresenceTransferDelegateWrapper::onPushPresenceTransferUploadFileDataToURL(
		IPushPresencePtr session,
		const char *postURL,
		const char *fileNameContainingData,
		ULONGEST totalFileSizeInBytes,
		ULONGEST remainingBytesToUpload,
		IPushPresenceTransferNotifierPtr notifier
)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushPresenceTransferUploadFileDataToURL called");

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
		//create new OPPushPresence java object
		cls = findClass("com/openpeer/javaapi/OPPushPresence");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresencePtr* ptrToPushPresence = new std::shared_ptr<IPushPresence>(session);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceObject, fid, (jlong)ptrToPushPresence);

		jstring postURLJavaString =  jni_env->NewStringUTF(postURL);
		jstring fileNameContainingDataJavaString =  jni_env->NewStringUTF(fileNameContainingData);

		//create new OPPushPresenceTransferNotifier java object
		cls = findClass("com/openpeer/javaapi/OPPushPresenceTransferNotifier");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceTransferNotifierObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresenceTransferNotifierPtr* ptrToPushPresenceTransferNotifier = new std::shared_ptr<IPushPresenceTransferNotifier>(notifier);
		fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceTransferNotifierObject, fid, (jlong)ptrToPushPresenceTransferNotifier);


		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushPresenceTransferUploadFileDataToURL", "(Lcom/openpeer/javaapi/OPPushPresence;Ljava/lang/String;Ljava/lang/String;JJLcom/openpeer/javaapi/OPPushPresenceTransferNotifier;)V");
		jni_env->CallVoidMethod(javaDelegate, method,
				pushPresenceObject,
				postURLJavaString,
				fileNameContainingDataJavaString,
				(jlong) totalFileSizeInBytes,
				(jlong) remainingBytesToUpload,
				pushPresenceTransferNotifierObject
		);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushPresenceTransferUploadFileDataToURL Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

void PushPresenceTransferDelegateWrapper::onPushPresenceTransferDownloadDataFromURL(
		IPushPresencePtr session,
		const char *getURL,
		const char *fileNameToAppendData,
		ULONGEST finalFileSizeInBytes,
		ULONGEST remainingBytesToBeDownloaded,
		IPushPresenceTransferNotifierPtr notifier
)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onPushPresenceTransferDownloadDataFromURL called");

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
		//create new OPPushPresencejava object
		cls = findClass("com/openpeer/javaapi/OPPushPresence");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresencePtr* ptrToPushPresence = new std::shared_ptr<IPushPresence>(session);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceObject, fid, (jlong)ptrToPushPresence);

		jstring getURLJavaString =  jni_env->NewStringUTF(getURL);
		jstring fileNameToAppendDataJavaString =  jni_env->NewStringUTF(fileNameToAppendData);

		//create new OPPushPresenceTransferNotifier java object
		cls = findClass("com/openpeer/javaapi/OPPushPresenceTransferNotifier");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject pushPresenceTransferNotifierObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IPushPresenceTransferNotifierPtr* ptrToPushPresenceTransferNotifier = new std::shared_ptr<IPushPresenceTransferNotifier>(notifier);
		fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(pushPresenceTransferNotifierObject, fid, (jlong)ptrToPushPresenceTransferNotifier);


		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onPushPresenceTransferUploadFileDataToURL", "(Lcom/openpeer/javaapi/OPPushPresence;Ljava/lang/String;Ljava/lang/String;JJLcom/openpeer/javaapi/OPPushPresenceTransferNotifier;)V");
		jni_env->CallVoidMethod(javaDelegate, method,
				pushPresenceObject,
				getURLJavaString,
				fileNameToAppendDataJavaString,
				(jlong) finalFileSizeInBytes,
				(jlong) remainingBytesToBeDownloaded,
				pushPresenceTransferNotifierObject
		);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onPushPresenceTransferDownloadDataFromURL Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

PushPresenceTransferDelegateWrapper::~PushPresenceTransferDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
