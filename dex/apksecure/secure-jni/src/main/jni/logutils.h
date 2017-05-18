//
// Created by ma.xuanwei on 2017/5/5.
//
#include <android/log.h>

#define LOG_TAG "dev_mars_native"
#define LOGOPEN 1 //日志开关，1为开，其它为关
#if(LOGOPEN==1)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#else
#define LOGD(...)  NULL
#define LOGE(...)  NULL
#define LOGI(...)  NULL
#endif
