#include "globals.h"
#include <iostream>

JavaVM *android_jvm;
jobject jni_object;

SettingsDelegateWrapperPtr settingsDelegatePtr;
CacheDelegateWrapperPtr cacheDelegatePtr;
CallDelegateWrapperPtr callDelegatePtr;
ConversationThreadDelegateWrapperPtr conversationThreadDelegatePtr;
LoggerDelegateWrapperPtr loggerDelegatePtr;
MediaEngineDelegateWrapperPtr mediaEngineDelegatePtr;
BackgroundingDelegateWrapperPtr backgroundingDelegatePtr;
BackgroundingCompletionDelegateWrapperPtr backgroundingCompletionDelegatePtr;

IMediaEnginePtr mediaEnginePtr;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    android_jvm = vm;

    int getEnvStat = android_jvm->GetEnv((void **)&gEnv, JNI_VERSION_1_6);
	if (getEnvStat == JNI_EDETACHED) {
		std::cout << "GetEnv: not attached" << std::endl;
		if (android_jvm->AttachCurrentThread(&gEnv, NULL) != 0) {
			std::cout << "Failed to attach" << std::endl;
		}
	} else if (getEnvStat == JNI_OK) {
		//
	} else if (getEnvStat == JNI_EVERSION) {
		std::cout << "GetEnv: version not supported" << std::endl;
	}

	jclass randomClass = gEnv->FindClass("com/openpeer/javaapi/OPAccount");
	jclass classClass = gEnv->FindClass("java/lang/Class");
	jclass classLoaderClass = gEnv->FindClass("java/lang/ClassLoader");
	jmethodID getClassLoaderMethod = gEnv->GetMethodID(classClass, "getClassLoader",
											 "()Ljava/lang/ClassLoader;");

	jobject tmpClassLoader = gEnv->CallObjectMethod(randomClass, getClassLoaderMethod);
	gClassLoader = (jclass)gEnv->NewGlobalRef(tmpClassLoader);

	gFindClassMethod = gEnv->GetMethodID(classLoaderClass, "findClass",
									"(Ljava/lang/String;)Ljava/lang/Class;");

    return JNI_VERSION_1_6;
}

JNIEnv* getEnv() {
    JNIEnv *env;
    int status = android_jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if(status < 0) {
        status = android_jvm->AttachCurrentThread(&env, NULL);
        if(status < 0) {
            return NULL;
        }
    }
    return env;
}

jclass findClass(const char* name) {
    return static_cast<jclass>(getEnv()->CallObjectMethod(gClassLoader, gFindClassMethod, getEnv()->NewStringUTF(name)));
}
