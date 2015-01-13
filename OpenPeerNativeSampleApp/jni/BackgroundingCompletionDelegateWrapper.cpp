#include "globals.h"
#include "BackgroundingCompletionDelegateWrapper.h"
#include <android/log.h>
#include "OpenPeerCoreManager.h"
#include "openpeer/services/IHelper.h"

//IBackgroundingCompletionDelegate implementation
BackgroundingCompletionDelegateWrapper::BackgroundingCompletionDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

void BackgroundingCompletionDelegateWrapper::onBackgroundingReady(IBackgroundingQueryPtr query)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onBackgroundingReady called");

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

		//create new OPBackgroundingQuery java object
		cls = findClass("com/openpeer/javaapi/OPBackgroundingQuery");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject queryObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IBackgroundingQueryPtr* ptrToQuery = new std::shared_ptr<IBackgroundingQuery>(query);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(queryObject, fid, (jlong)ptrToQuery);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass,
				"onBackgroundingReady",
				"(Lcom/openpeer/javaapi/OPBackgroundingQuery;)V");
		jni_env->CallVoidMethod(javaDelegate, method, queryObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onBackgroundingReady Java delegate is NULL !!!");
	}
	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

BackgroundingCompletionDelegateWrapper::~BackgroundingCompletionDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);
}
