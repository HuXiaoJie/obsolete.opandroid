/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_openpeer_javaapi_OPCache */

#ifndef _Included_com_openpeer_javaapi_OPCache
#define _Included_com_openpeer_javaapi_OPCache
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    setup
 * Signature: (Lcom/openpeer/javaapi/OPCacheDelegate;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCache_setup
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    singleton
 * Signature: ()Lcom/openpeer/javaapi/OPCache;
 */
JNIEXPORT jobject JNICALL Java_com_openpeer_javaapi_OPCache_singleton
  (JNIEnv *, jclass);

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    fetch
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_openpeer_javaapi_OPCache_fetch
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    store
 * Signature: (Ljava/lang/String;Landroid/text/format/Time;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCache_store
  (JNIEnv *, jobject, jstring, jobject, jstring);

/*
 * Class:     com_openpeer_javaapi_OPCache
 * Method:    clear
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_openpeer_javaapi_OPCache_clear
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif