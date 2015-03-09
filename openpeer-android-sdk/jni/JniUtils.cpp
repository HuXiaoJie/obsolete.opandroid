#include "globals.h"
#include "JniUtils.h"
IdentityContactList identityContactListFromJava(jobject identityContacts) {
  JNIEnv* jni_env = getEnv();
  //create return object - java/util/List is interface, ArrayList is implementation
  jclass arrayListClass = findClass("java/util/ArrayList");
  IdentityContactList coreIdentityContacts;

  if (jni_env->IsInstanceOf(identityContacts, arrayListClass) != JNI_TRUE) {
    return coreIdentityContacts;
  }

  // Fetch "java.util.List.get(int location)" MethodID
  jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get",
      "(I)Ljava/lang/Object;");
  // Fetch "int java.util.List.size()" MethodID
  jmethodID sizeMethodID = jni_env->GetMethodID(arrayListClass, "size",
      "()I");

  // Call "int java.util.List.size()" method and get count of items in the list.
  int listItemsCount = (int) jni_env->CallIntMethod(identityContacts,
      sizeMethodID);

  if (listItemsCount == 0) {
    __android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni",
        "OPConversationThread native addContacts no IdentityContqcts!!!");
    return coreIdentityContacts;
  }

  for (int i = 0; i < listItemsCount; ++i) {

    // Call "java.util.List.get" method and get IdentityContact object by index.
    jobject identityContactObject = jni_env->CallObjectMethod(
        identityContacts, listGetMethodID, i);

    if (identityContactObject != NULL) {
      IdentityContact coreIdentityContact = identityContactFromJava(
          identityContactObject);
      //add core identity contacts to list
      coreIdentityContacts.push_front(coreIdentityContact);
    }
  }
  return coreIdentityContacts;
}

IdentityContact identityContactFromJava(jobject identityContactObject) {
  JNIEnv* jni_env = getEnv();
  IdentityContact coreIdentityContact;

  if (identityContactObject != NULL) {
    //Fetch OPIdentityContact class
    jclass identityContactClass = findClass(
        "com/openpeer/javaapi/OPIdentityContact");

    //FETCH METHODS TO GET INFO FROM JAVA
    //Fetch getStableID method from OPIdentityContact class
    jmethodID getStableIDMethodID = jni_env->GetMethodID(identityContactClass,
        "getStableID", "()Ljava/lang/String;");
    //Fetch getPeerFilePublic method from OPIdentityContact class
    jmethodID getPeerFilePublicMethodID = jni_env->GetMethodID(
        identityContactClass, "getPeerFilePublic",
        "()Lcom/openpeer/javaapi/OPPeerFilePublic;");
    //Fetch getIdentityProofBundle method from OPIdentityContact class
    jmethodID getIdentityProofBundleMethodID = jni_env->GetMethodID(
        identityContactClass, "getIdentityProofBundle", "()Ljava/lang/String;");
    //Fetch getPriority method from OPIdentityContact class
    jmethodID getPriorityMethodID = jni_env->GetMethodID(identityContactClass,
        "getPriority", "()I");
    //Fetch getWeight method from OPIdentityContact class
    jmethodID getWeightMethodID = jni_env->GetMethodID(identityContactClass,
        "getWeight", "()I");
    //Fetch getLastUpdated method from OPIdentityContact class
    jmethodID getLastUpdatedMethodID = jni_env->GetMethodID(
        identityContactClass, "getLastUpdated", "()Landroid/text/format/Time;");
    //Fetch getExpires method from OPIdentityContact class
    jmethodID getExpiresMethodID = jni_env->GetMethodID(identityContactClass,
        "getExpires", "()Landroid/text/format/Time;");

    //get rolodex contact setter methods
    ///////////////////////////////////////////////////////////////
    // GET ROLODEX CONTACT FIELDS
    //////////////////////////////////////////////////////////////

    //Fetch setDisposition method from OPDownloadedRolodexContacts class
    //jclass dispositionClass = findClass("com/openpeer/javaapi/OPRolodexContact$Dispositions");
    //jmethodID dispositionConstructorMethodID = jni_env->GetMethodID(cls, "<init>", "()V");
    jmethodID getDispositionMethodID = jni_env->GetMethodID(
        identityContactClass, "getDisposition",
        "()Lcom/openpeer/javaapi/OPRolodexContact$Dispositions;");
    //Fetch setIdentityURI method from OPDownloadedRolodexContacts class
    jmethodID getIdentityURIMethodID = jni_env->GetMethodID(
        identityContactClass, "getIdentityURI", "()Ljava/lang/String;");
    //Fetch setIdentityProvider method from OPDownloadedRolodexContacts class
    jmethodID getIdentityProviderMethodID = jni_env->GetMethodID(
        identityContactClass, "getIdentityProvider", "()Ljava/lang/String;");
    //Fetch setName method from OPDownloadedRolodexContacts class
    jmethodID getNameMethodID = jni_env->GetMethodID(identityContactClass,
        "getName", "()Ljava/lang/String;");
    //Fetch setProfileURL method from OPDownloadedRolodexContacts class
    jmethodID getProfileURLMethodID = jni_env->GetMethodID(identityContactClass,
        "getProfileURL", "()Ljava/lang/String;");
    //Fetch setVProfileURL method from OPDownloadedRolodexContacts class
    jmethodID getVProfileURLMethodID = jni_env->GetMethodID(
        identityContactClass, "getVProfileURL", "()Ljava/lang/String;");
    //Fetch setAvatars method from OPDownloadedRolodexContacts class
    jmethodID getAvatarsMethodID = jni_env->GetMethodID(identityContactClass,
        "getAvatars", "()Ljava/util/List;");
    //CALL METHODS TO FETCH INFO FROM JAVA
    // Call getStableID method to fetch stable ID from OPIdentityContact
    jstring stableID = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getStableIDMethodID);
    const char *nativeString = jni_env->GetStringUTFChars(stableID, 0);
    jni_env->ReleaseStringUTFChars(stableID, nativeString);

    // Call getPeerFilePublic method to fetch peer file public from OPIdentityContact
    jobject peerFilePublic = jni_env->CallObjectMethod(
        identityContactObject, getPeerFilePublicMethodID);

    // Call getIdentityProofBundle method to fetch identity proof bundle from OPIdentityContact
    jstring identityProofBundle = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getIdentityProofBundleMethodID);

    // Call getPriority method to fetch priority from OPIdentityContact
    jint priority = jni_env->CallIntMethod(identityContactObject,
        getPriorityMethodID);

    // Call getWeight method to fetch priority from OPIdentityContact
    jint weight = jni_env->CallIntMethod(identityContactObject,
        getWeightMethodID);

    // Call getLastUpdated method to fetch last updated from OPIdentityContact
    jobject lastUpdated = jni_env->CallObjectMethod(identityContactObject,
        getLastUpdatedMethodID);

    // Call getExpires method to fetch expires from OPIdentityContact
    jobject expires = jni_env->CallObjectMethod(identityContactObject,
        getExpiresMethodID);

    //GET ROLODEX CONTACTS FIELDS
    jobject disposition = jni_env->CallObjectMethod(identityContactObject,
        getDispositionMethodID);
    // Call getStableID method to fetch stable ID from OPIdentityContact
    jstring identityURI = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getIdentityURIMethodID);

    jstring identityProvider = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getIdentityProviderMethodID);

    jstring profileURL = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getProfileURLMethodID);

    jstring vProfileURL = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getVProfileURLMethodID);

    jstring name = (jstring) jni_env->CallObjectMethod(
        identityContactObject, getNameMethodID);

    jobject avatarList = jni_env->CallObjectMethod(identityContactObject, getAvatarsMethodID);
    coreIdentityContact.mDisposition =
        (RolodexContact::Dispositions) OpenPeerCoreManager::getIntValueFromEnumObject(
            disposition,
            "com/openpeer/javaapi/OPRolodexContact$Dispositions");
    coreIdentityContact.mIdentityProvider = jni_env->GetStringUTFChars(
        identityProvider, NULL);
    coreIdentityContact.mIdentityURI = jni_env->GetStringUTFChars(
        identityURI, NULL);
    coreIdentityContact.mProfileURL = jni_env->GetStringUTFChars(profileURL,
        NULL);
    coreIdentityContact.mVProfileURL = jni_env->GetStringUTFChars(
        vProfileURL, NULL);
    coreIdentityContact.mName = jni_env->GetStringUTFChars(name, NULL);
    coreIdentityContact.mAvatars = avatarListFromJava(avatarList);

    jclass cls = findClass("com/openpeer/javaapi/OPPeerFilePublic");
    jfieldID fid = jni_env->GetFieldID(cls, "mPeerFileString",
        "Ljava/lang/String;");
    jstring peerFileString = (jstring) jni_env->GetObjectField(
        peerFilePublic, fid);
    String corePeerFileString = jni_env->GetStringUTFChars(peerFileString,
        NULL);
    ElementPtr peerFilePublicEl = IHelper::createElement(
        corePeerFileString);
    coreIdentityContact.mPeerFilePublic = IHelper::createPeerFilePublic(
        peerFilePublicEl);

    //FILL IN CORE IDENTITY CONTACT STRUCTURE WITH DATA FROM JAVA

    //Add stableID to IdentityContact structure
    coreIdentityContact.mStableID = jni_env->GetStringUTFChars(stableID,
        NULL);

    //Add peerFilePublic to IdentityContact structure
    //TODO will not implement now

    //Add identityProofBundle to IdentityContact structure
    String identityProofBundleString = jni_env->GetStringUTFChars(
        identityProofBundle, NULL);
    coreIdentityContact.mIdentityProofBundleEl = IHelper::createElement(
        identityProofBundleString);

    //Add priority to IdentityContact structure
    coreIdentityContact.mPriority = priority;

    //Add weight to IdentityContact structure
    coreIdentityContact.mWeight = weight;

    //Add last updated to IdentityContact structure
    jclass timeCls = findClass("android/text/format/Time");
    jmethodID timeMethodID = jni_env->GetMethodID(timeCls, "toMillis",
        "(Z)J");
    jlong longValue = jni_env->CallLongMethod(lastUpdated, timeMethodID,
        false);
    Time t = std::chrono::time_point<std::chrono::system_clock>(std::chrono::milliseconds(longValue));
    coreIdentityContact.mLastUpdated = t;

    //Add expires to IdentityContact structure
    longValue = jni_env->CallLongMethod(expires, timeMethodID, false);
    t = std::chrono::time_point<std::chrono::system_clock>(std::chrono::milliseconds(longValue));
    coreIdentityContact.mExpires = t;

  }
  return coreIdentityContact;
}

RolodexContact::AvatarList avatarListFromJava(jobject javaAvatarList) {
  JNIEnv* jni_env = getEnv();
  RolodexContact::AvatarList coreAvatarList;

  jclass arrayListClass = findClass("java/util/ArrayList");

  jmethodID listGetMethodID = jni_env->GetMethodID(arrayListClass, "get",
      "(I)Ljava/lang/Object;");
  // Fetch "int java.util.List.size()" MethodID
  jmethodID sizeMethodID = jni_env->GetMethodID(arrayListClass, "size",
      "()I");

  // Call "int java.util.List.size()" method and get count of items in the list.
  int listItemsCount = (int) jni_env->CallIntMethod(javaAvatarList,
      sizeMethodID);

  if (listItemsCount == 0) {
    __android_log_print(ANDROID_LOG_ERROR, "com.openpeer.jni",
        "OPConversationThread native addContacts no IdentityContqcts!!!");
    return coreAvatarList;
  }

  for (int i = 0; i < listItemsCount; ++i) {

    // Call "java.util.List.get" method and get IdentityContact object by index.
    jobject avatarObject = jni_env->CallObjectMethod(
        javaAvatarList, listGetMethodID, i);

    if (avatarObject != NULL) {
      RolodexContact::Avatar coreAvatar = avatarFromJava(avatarObject);
      //add core identity contacts to list
      coreAvatarList.push_front(coreAvatar);
    }
  }
  return coreAvatarList;
}
RolodexContact::Avatar avatarFromJava(jobject javaAvatar) {
  JNIEnv* jni_env = getEnv();
  RolodexContact::Avatar coreAvatar;

  jclass avatarClass = findClass(
      "com/openpeer/javaapi/OPRolodexContact$OPAvatar");

  jmethodID getNameMethodID = jni_env->GetMethodID(avatarClass,
      "getName", "()Ljava/lang/String;");
  jmethodID getURLMethodID = jni_env->GetMethodID(avatarClass,
      "getURL", "()Ljava/lang/String;");
  jmethodID getWidthMethodID = jni_env->GetMethodID(avatarClass,
      "getWidth", "()I");
  jmethodID getHeightMethodID = jni_env->GetMethodID(avatarClass,
      "getHeight", "()I");
  jstring name = (jstring) jni_env->CallObjectMethod(
      javaAvatar, getNameMethodID);
  jstring url = (jstring) jni_env->CallObjectMethod(
      javaAvatar, getURLMethodID);

  coreAvatar.mName = jni_env->GetStringUTFChars(name, NULL);
  coreAvatar.mURL = jni_env->GetStringUTFChars(url, NULL);
  return coreAvatar;
}
