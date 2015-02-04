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

#openssl:begin
include $(CLEAR_VARS)
OPENSSL_LIB_PATH := libs/op/libs/ortc-lib/libs/build/android/openssl
LOCAL_MODULE := libcrypto
LOCAL_SRC_FILES := \
    $(OPENSSL_LIB_PATH)/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
OPENSSL_LIB_PATH := libs/op/libs/ortc-lib/libs/build/android/openssl
LOCAL_MODULE := libssl
LOCAL_SRC_FILES := \
    $(OPENSSL_LIB_PATH)/libssl.a
include $(PREBUILT_STATIC_LIBRARY)
#openssl:end

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
LOCAL_MODULE := libneteq
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libneteq.a
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
LOCAL_MODULE := libvideo_capture_module_internal_impl
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_capture_module_internal_impl.a
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
LOCAL_MODULE := libvideo_render_module_internal_impl
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvideo_render_module_internal_impl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libvoice_engine
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libvoice_engine.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebrtc_common
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_common.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := librtc_base_approved
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/librtc_base_approved.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libred
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libred.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_encoder_interface
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_encoder_interface.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaudio_decoder_interface
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libaudio_decoder_interface.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libfield_trial_default
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libfield_trial_default.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libmetrics_default
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libmetrics_default.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libjpeg_turbo
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libjpeg_turbo.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libopenmax_dl
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libopenmax_dl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libopenmax_dl_armv7
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libopenmax_dl_armv7.a
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
LOCAL_MODULE := libwebrtc_vp9
LOCAL_SRC_FILES := \
    $(WEBRTC_LIBS_PATH)/libwebrtc_vp9.a
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
LOCAL_MODULE := libcryptopp
LOCAL_SRC_FILES := \
    $(CRYPTOPP_LIBS_PATH)/libcryptopp.a
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

#Boost libs
include $(CLEAR_VARS)
LOCAL_MODULE := libboost_atomic-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_atomic-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_chrono-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_chrono-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_date_time-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_date_time-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_filesystem-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_filesystem-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_graph-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_graph-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_iostreams-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_iostreams-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_program_options-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_program_options-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_random-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_random-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_regex-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_regex-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_signals-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_signals-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_system-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_system-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_thread-gcc-mt-1_53
LOCAL_SRC_FILES := \
    $(BOOST_LIBS_PATH)/libboost_thread-gcc-mt-1_53.a
include $(PREBUILT_STATIC_LIBRARY)

#hfsservices
include $(CLEAR_VARS)
SERVICES_LIBS_PATH := libs/op/libs/ortc-lib/libs/build/android/op-services-cpp
LOCAL_MODULE := libhfservices_android
LOCAL_SRC_FILES := \
    $(SERVICES_LIBS_PATH)/libhfservices_android.a
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

LOCAL_CFLAGS	:= -Wall \
-W \
-Wno-unused-parameter \
-Wno-reorder \
-std=gnu++11 \
-O2 \
-pipe \
-fPIC \
-frtti \
-fexceptions \
-D_ANDROID \

LOCAL_MODULE    := openpeer

LOCAL_C_INCLUDES:= $(LOCAL_PATH)/libs/op/libs/ortc-lib/libs \
$(LOCAL_PATH)/libs/op/libs/op-stack-cpp/ \
$(LOCAL_PATH)/libs/op/libs/op-core-cpp/ \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/op-services-cpp \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/build/android/curl/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/build/android/boost/include/boost-1_53 \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/zsLib \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/build/android/cryptopp/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc/voice_engine/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc/video_engine/include \
$(LOCAL_PATH)/libs/op/libs/ortc-lib/libs/webrtc/webrtc/modules/video_capture/include \
$(ANDROIDNDK_PATH)/sources/cxx-stl/gnu-libstdc++/4.7/include \
$(ANDROIDNDK_PATH)/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/include \
$(ANDROIDNDK_PATH)/platforms/android-9/arch-arm/usr/include \

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

LOCAL_LDLIBS += $(ANDROIDNDK_PATH)/sources/cxx-stl/gnu-libstdc++/4.7/libs/armeabi/libgnustl_static.a
LOCAL_LDLIBS += -llog -lGLESv2 -lOpenSLES \

LOCAL_WHOLE_STATIC_LIBRARIES := \
libaudio_conference_mixer \
libaudio_processing_neon \
libaudio_processing \
libaudio_coding_module \
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
libneteq \
libopus \
libpaced_sender \
libPCM16B \
libremote_bitrate_estimator \
librbe_components \
librtp_rtcp \
libsystem_wrappers \
libvideo_capture_module \
libvideo_capture_module_internal_impl \
libvideo_coding_utility \
libvideo_engine_core \
libvideo_processing \
libvideo_render_module \
libvideo_render_module_internal_impl \
libvoice_engine \
libwebrtc \
libwebrtc_common \
librtc_base_approved \
libred \
libaudio_encoder_interface \
libaudio_decoder_interface \
libfield_trial_default \
libmetrics_default \
libjpeg_turbo \
libopenmax_dl \
libopenmax_dl_armv7 \
libvpx \
libvpx_intrinsics_neon \
libwebrtc_i420 \
libwebrtc_opus \
libwebrtc_utility \
libwebrtc_video_coding \
libwebrtc_vp8 \
libwebrtc_vp9 \
libyuv \
libyuv_neon \
libortc_android \
libcryptopp \
libudns_android \
libboost_atomic-gcc-mt-1_53 \
libboost_chrono-gcc-mt-1_53 \
libboost_date_time-gcc-mt-1_53 \
libboost_filesystem-gcc-mt-1_53 \
libboost_graph-gcc-mt-1_53 \
libboost_iostreams-gcc-mt-1_53 \
libboost_program_options-gcc-mt-1_53 \
libboost_random-gcc-mt-1_53 \
libboost_regex-gcc-mt-1_53 \
libboost_signals-gcc-mt-1_53 \
libboost_system-gcc-mt-1_53 \
libboost_thread-gcc-mt-1_53 \
libcurl \
libssl \
libcrypto \
libzslib_android \
libhfservices_android \
libhfstack_android \
libhfcore_android \ 


LOCAL_SHARED_LIBRARIES := \
z_shared \

include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
#include $(BUILD_EXECUTABLE)

