#include "com_jikexueyuan_callandroidapi_JniBridge.h"
#include <android/log.h>

#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

void Java_com_jikexueyuan_callandroidapi_JniBridge_showLog
(JNIEnv *, jclass){
    //输出日志，此处输出了一个错误日志，方便过滤器过滤
    LOGE("This is an error log shown from C++.");
}