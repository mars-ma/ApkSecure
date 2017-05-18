#include <jni.h>
#include <string>
#include <android/log.h>


extern "C" {
JNIEXPORT void JNICALL
Java_dev_mars_testapplication_MyApplication_test(JNIEnv *env, jobject instance) {

    // TODO
    jclass Methods2Class = env->FindClass("dev/mars/testapplication/Methods2");
    if(Methods2Class!=NULL){
        __android_log_print(ANDROID_LOG_DEBUG,"dev_mars_native","找到dev/mars/testapplication/Methods2");
    }else{
        __android_log_print(ANDROID_LOG_DEBUG,"dev_mars_native","未找到dev/mars/testapplication/Methods2");
        return;
    }
    jmethodID init = env->GetMethodID(Methods2Class,"<init>","()V");
    jobject methods2Obj = env->NewObject(Methods2Class,init);
    jmethodID l = env->GetMethodID(Methods2Class,"l","()V");
    env->CallVoidMethod(methods2Obj,l);


}

}




