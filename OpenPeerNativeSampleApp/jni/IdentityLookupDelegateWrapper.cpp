#include "IdentityLookupDelegateWrapper.h"
#include "OpenPeerCoreManager.h"
#include "android/log.h"
#include "globals.h"


//IIdentityLookupDelegate implementation
IdentityLookupDelegateWrapper::IdentityLookupDelegateWrapper(jobject delegate)
{
	JNIEnv *jni_env = getEnv();
	javaDelegate = jni_env->NewGlobalRef(delegate);
}

//IIdentityLookupDelegate implementation
void IdentityLookupDelegateWrapper::onIdentityLookupCompleted(IIdentityLookupPtr identityLookup)
{
	jclass cls;
	jmethodID method;
	jobject object;
	JNIEnv *jni_env = 0;

	__android_log_print(ANDROID_LOG_DEBUG, "com.openpeer.jni", "onIdentityLookupCompleted called");

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
		//create new OPCall java object
		cls = findClass("com/openpeer/javaapi/OPIdentityLookup");
		method = jni_env->GetMethodID(cls, "<init>", "()V");
		jobject identityLookupObject = jni_env->NewObject(cls, method);

		//fill new field with pointer to core pointer
		IIdentityLookupPtr* ptrToIdentityLookup = new std::shared_ptr<IIdentityLookup>(identityLookup);
		jfieldID fid = jni_env->GetFieldID(cls, "nativeClassPointer", "J");
		jni_env->SetLongField(identityLookupObject, fid, (jlong)ptrToIdentityLookup);

		//get delegate implementation class name in order to get method
		String className = OpenPeerCoreManager::getObjectClassName(javaDelegate);

		jclass callbackClass = findClass(className.c_str());
		method = jni_env->GetMethodID(callbackClass, "onIdentityLookupCompleted", "(Lcom/openpeer/javaapi/OPIdentityLookup;)V");
		jni_env->CallVoidMethod(javaDelegate, method, identityLookupObject);
	}
	else
	{
		__android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni", "onIdentityLookupCompleted Java delegate is NULL !!!");
	}

	if (jni_env->ExceptionCheck()) {
		jni_env->ExceptionDescribe();
	}

	if(attached)
	{
		android_jvm->DetachCurrentThread();
	}
}

IdentityLookupDelegateWrapper::~IdentityLookupDelegateWrapper()
{
	JNIEnv *jni_env = getEnv();
	jni_env->DeleteGlobalRef(javaDelegate);

}
