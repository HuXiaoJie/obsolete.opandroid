#include <jni.h>
#include "globals.h"
#include "AccountDelegateWrapper.h"
#include "CacheDelegateWrapper.h"
#include "SettingsDelegateWrapper.h"
#include "CallDelegateWrapper.h"
#include "ConversationThreadDelegateWrapper.h"
#include "IdentityDelegateWrapper.h"
#include "IdentityLookupDelegateWrapper.h"
#include "StackDelegateWrapper.h"
#include "StackMessageQueueDelegateWrapper.h"
#include "LoggerDelegateWrapper.h"
#include "MediaEngineDelegateWrapper.h"
#include "BackgroundingDelegateWrapper.h"
#include "BackgroundingCompletionDelegateWrapper.h"

#include "openpeer/core/IConversationThread.h"
#include "openpeer/core/IContact.h"
#include "openpeer/core/IHelper.h"
#include "openpeer/core/ILogger.h"
#include "openpeer/core/types.h"
#include "OpenPeerCoreManager.h"

#include <android/log.h>

#ifndef _ANDROID_OPENPEER_JNI_UTILS_H_
#define _ANDROID_OPENPEER_JNI_UTILS_H_

//const char * JCLS_ARRAY_LIST = "java/util/ArrayList";
//const char * JCLS_OPCONTACT = "com/openpeer/javaapi/OPContact";
//const char * JCLS_OPIDENTITY_CONTACT = "com/openpeer/javaapi/OPIdentityContact";
//const char * JCLS_OPCONTACT_PROFILE_INFO =
//    "com/openpeer/javaapi/OPContactProfileInfo";

IdentityContactList identityContactListFromJava(jobject identityContacts);
IdentityContact identityContactFromJava(jobject identityContactObject);
RolodexContact::AvatarList avatarListFromJava(jobject javaAvatarList);
RolodexContact::Avatar avatarFromJava(jobject javaAvatar);
#endif
