LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(ANDROID_NDK)/sources/ffmpeg-2.2.3
LOCAL_CFLAGS := -Wdeprecated-declarations
LOCAL_MODULE := videokit
ANDROID_LIB := -landroid
LOCAL_LDLIBS += -llog -ljnigraphics -lz
LOCAL_SRC_FILES := videokit/fr_enoent_videokit_Videokit.c
LOCAL_STATIC_LIBRARIES := libavdevice libavformat libavfilter libavcodec libavutil libswresample libswscale
include $(BUILD_SHARED_LIBRARY)
$(call import-module,ffmpeg-2.2.3/android/arm)
