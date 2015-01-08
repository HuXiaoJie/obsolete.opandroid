LOCAL_PATH := $(call my-dir)/../..
WEBRTC_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/webrtc
BOOST_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/boost/lib
ANDROIDNDK_PATH := /usr/ndk/x86_64/android-ndk-r8e

#zLib shared library
include $(CLEAR_VARS)
ZLIB_LIB_PATH := ./openpeer-android-sdk/jni
LOCAL_MODULE := z_shared
LOCAL_SRC_FILES := \
    $(ZLIB_LIB_PATH)/libz_shared.so
include $(PREBUILT_SHARED_LIBRARY)

#curl lib
include $(CLEAR_VARS)
CURL_LIB_PATH := libs/op/libs/ortc-lib/libs/build/android/curl
LOCAL_MODULE := libcurl
LOCAL_SRC_FILES := \
    $(CURL_LIB_PATH)/libcurl.a
include $(PREBUILT_STATIC_LIBRARY)

#webrtc libs
include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_coding_module
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_coding_module.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libacm2
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libacm2.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_conference_mixer
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_conference_mixer.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcpu_features
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libcpu_features.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcpu_features_android
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libcpu_features_android.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_device
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_device.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_processing
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_processing.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_processing_neon
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_processing_neon.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libbitrate_controller
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libbitrate_controller.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libchannel_transport
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libchannel_transport.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libCNG
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libCNG.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcommon_audio
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libcommon_audio.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcommon_audio_neon
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libcommon_audio_neon.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcommon_video
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libcommon_video.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libG711
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libG711.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libG722
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libG722.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libiLBC
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libiLBC.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libisac_neon
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libisac_neon.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libiSAC
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libiSAC.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libiSACFix
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libiSACFix.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libmedia_file
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libmedia_file.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libNetEq
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libNetEq.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libNetEq4
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libNetEq4.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libopus
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libopus.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpaced_sender
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libpaced_sender.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libPCM16B
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libPCM16B.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libremote_bitrate_estimator
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libremote_bitrate_estimator.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := librbe_components
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/librbe_components.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := librtp_rtcp
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/librtp_rtcp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libsystem_wrappers
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libsystem_wrappers.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvideo_capture_module
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_capture_module.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvideo_coding_utility
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_coding_utility.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvideo_engine_core
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_engine_core.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvideo_processing
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_processing.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvideo_render_module
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_render_module.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvoice_engine
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvoice_engine.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvpx
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvpx.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvpx_intrinsics_neon
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvpx_intrinsics_neon.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc_i420
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_i420.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc_opus
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_opus.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc_utility
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_utility.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc_video_coding
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_video_coding.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc_vp8
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_vp8.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libyuv
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libyuv.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libyuv_neon
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libyuv_neon.a
include $(PREBUILT_STATIC_LIBRARY)

#cryptopp
include $(CLEAR_VARS)
CRYPTOPP_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/cryptopp
LOCAL_MODULE := libcryptopp_android
LOCAL_SRC_FILES := \
    $(CRYPTOPP_LIBS_PATH)/libcryptopp_android.a
include $(PREBUILT_STATIC_LIBRARY)

#udns
include $(CLEAR_VARS)
UDNS_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/udns
LOCAL_MODULE := libudns_android
LOCAL_SRC_FILES := \
    $(UDNS_LIBS_PATH)/libudns_android.a
include $(PREBUILT_STATIC_LIBRARY)

#ZsLib
include $(CLEAR_VARS)
ZSLIB_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/zsLib
LOCAL_MODULE := libzslib_android
LOCAL_SRC_FILES := \
    $(ZSLIB_LIBS_PATH)/libzslib_android.a
include $(PREBUILT_STATIC_LIBRARY)

#idnkit
include $(CLEAR_VARS)
ZSLIB_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/idnkit
LOCAL_MODULE := libidnkitandroid
LOCAL_SRC_FILES := \
    $(ZSLIB_LIBS_PATH)/libidnkit_android.a
include $(PREBUILT_STATIC_LIBRARY)

#hfsservices
include $(CLEAR_VARS)
SERVICES_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/op-services-cpp
LOCAL_MODULE := libhfservices_android
LOCAL_SRC_FILES := \
    $(SERVICES_LIBS_PATH)/libhfservices_android.a
include $(PREBUILT_STATIC_LIBRARY)

#sqlite
include $(CLEAR_VARS)
STACK_LIBS_PATH := libs/op/libs/build/android/sqlite
LOCAL_MODULE := libsqlite_android
LOCAL_SRC_FILES := \
    $(STACK_LIBS_PATH)/libsqlite_android.a
include $(PREBUILT_STATIC_LIBRARY)

#easysqlite
include $(CLEAR_VARS)
STACK_LIBS_PATH := libs/op/libs/build/android/easysqlite
LOCAL_MODULE := libeasysqlite_android
LOCAL_SRC_FILES := \
    $(STACK_LIBS_PATH)/libeasysqlite_android.a
include $(PREBUILT_STATIC_LIBRARY)

#hfstack
include $(CLEAR_VARS)
STACK_LIBS_PATH := libs/op/libs/build/android/op-stack-cpp
LOCAL_MODULE := libhfstack_android
LOCAL_EXPORT_C_INCLUDES:= $(STACK_LIBS_PATH)
LOCAL_SRC_FILES := \
    $(STACK_LIBS_PATH)/libhfstack_android.a
include $(PREBUILT_STATIC_LIBRARY)

#hfcore
include $(CLEAR_VARS)
CORE_LIBS_PATH := libs/op/libs/build/android/op-core-cpp
LOCAL_MODULE := libhfcore_android
LOCAL_EXPORT_C_INCLUDES:= $(CORE_LIBS_PATH)
LOCAL_SRC_FILES := \
    $(CORE_LIBS_PATH)/libhfcore_android.a
include $(PREBUILT_STATIC_LIBRARY)

#ortc_android lib
#include $(CLEAR_VARS)
#ORTC_LIBS_PATH := ../ortc/build/android
#LOCAL_MODULE := libortc_android
#LOCAL_SRC_FILES := \
#    $(ORTC_LIBS_PATH)/libortc_android.a
#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_ARM_MODE := arm
LOCAL_CLANG := true

LOCAL_CFLAGS	:= -Wall \
-W \
-Wno-unused-parameter \
-Wno-reorder \
-O2 \
-pipe \
-fPIC \
-frtti \
-fexceptions \
-D_ANDROID \

LOCAL_CPPFLAGS += -std=c++11

LOCAL_MODULE    := openpeer

LOCAL_C_INCLUDES:= $(LOCAL_PATH)/libs/op/libs/ortc-lib/libs \
$(LOCAL_PATH)/libs/op/libs/op-stack-cpp/ \
$(LOCAL_PATH)/libs/op/libs/op-core-cpp/ \
$(LOCAL_PATH)/libs/op/libs/easysqlite/easySQLite/easySQLite/ \
$(LOCAL_PATH)/libs/op/libs/sqlite/build/ \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/op-services-cpp \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/build/android/curl/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/zsLib \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/zsLib/zsLib/extras \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/build/android/cryptopp/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/idnkit/idnkit/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc/voice_engine/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc/video_engine/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc/modules/video_capture/include \
$(ANDROIDNDK_PATH)/sources/android/support/include \
$(ANDROIDNDK_PATH)/sources/cxx-stl/llvm-libc++/libcxx/include \
$(ANDROIDNDK_PATH)/platforms/android-19/arch-arm/usr/include \

LOCAL_SRC_FILES := \
		openpeer-android-sdk/jni/OpenPeerCoreManager.cpp \
		openpeer-android-sdk/jni/globals.cpp \
		openpeer-android-sdk/jni/JniUtils.cpp \
		openpeer-android-sdk/jni/BackgroundingDelegateWrapper.cpp \
		openpeer-android-sdk/jni/BackgroundingCompletionDelegateWrapper.cpp \
		openpeer-android-sdk/jni/StackDelegateWrapper.cpp \
		openpeer-android-sdk/jni/StackMessageQueueDelegateWrapper.cpp \
		openpeer-android-sdk/jni/LoggerDelegateWrapper.cpp \
		openpeer-android-sdk/jni/AccountDelegateWrapper.cpp \
		openpeer-android-sdk/jni/CacheDelegateWrapper.cpp \
		openpeer-android-sdk/jni/SettingsDelegateWrapper.cpp \
		openpeer-android-sdk/jni/CallDelegateWrapper.cpp \
		openpeer-android-sdk/jni/MediaEngineDelegateWrapper.cpp \
		openpeer-android-sdk/jni/IdentityDelegateWrapper.cpp \
		openpeer-android-sdk/jni/IdentityLookupDelegateWrapper.cpp \
		openpeer-android-sdk/jni/ConversationThreadDelegateWrapper.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPBackgrounding.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPBackgroundingQuery.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPBackgroundingSubscription.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPBackgroundingNotifier.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPStackMessageQueue.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPStack.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPAccount.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPIdentity.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPIdentityLookup.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPMediaEngine.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPLogger.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPSettings.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPCache.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPCall.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPContact.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPConversationThread.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPComposingStatus.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPSystemMessage.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPCallSystemMessage.cpp \
		openpeer-android-sdk/jni/com_openpeer_javaapi_OPElement.cpp \

LOCAL_LDLIBS += $(ANDROIDNDK_PATH)/sources/cxx-stl/llvm-libc++/libs/armeabi/libc++_static.a
LOCAL_LDLIBS += -llog -lGLESv2 -lOpenSLES \

LOCAL_WHOLE_STATIC_LIBRARIES := \
libvoice_engine \
libaudio_conference_mixer \
libaudio_processing_neon \
libaudio_processing \
libaudio_coding_module \
libacm2 \
libcpu_features \
libcpu_features_android \
libaudio_device \
libbitrate_controller \
libchannel_transport \
libCNG \
libcommon_audio \
libcommon_audio_neon \
libcommon_video \
libG711 \
libG722 \
libiLBC \
libisac_neon \
libiSAC \
libiSACFix \
libmedia_file \
libNetEq \
libNetEq4 \
libopus \
libpaced_sender \
libPCM16B \
libremote_bitrate_estimator \
librbe_components \
librtp_rtcp \
libsystem_wrappers \
libvideo_capture_module \
libvideo_coding_utility \
libvideo_engine_core \
libvideo_processing \
libvideo_render_module \
libvpx \
libvpx_intrinsics_neon \
libwebrtc_i420 \
libwebrtc_opus \
libwebrtc_utility \
libwebrtc_video_coding \
libwebrtc_vp8 \
libyuv \
libyuv_neon \
libortc_android \
libcryptopp_android \
libudns_android \
libidnkit_android \
libcurl \
libzslib_android \
libhfservices_android \
libsqlite_android \
libeasysqlite_android \
libhfstack_android \
libhfcore_android \ 


LOCAL_SHARED_LIBRARIES := \
z_shared \

include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
#include $(BUILD_EXECUTABLE)

