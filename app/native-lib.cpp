#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_onemegasoft_greenlandrestaurant_shared_Urls_00024Companion_Java_1com_1onemegasoft_1greenlandrestaurant_1baseurl(
        JNIEnv *env, jobject thiz) {
//  std::string mUrl = "aHR0cDovLzE5Mi4xNjguMS4zL29uZW1lZ2Fzb2Z0Mi92MS9hcGkv"; //"http://localhost/";
    std::string mUrl = "aHR0cDovLzE5Mi4xNjguNDMuMTk2L29uZW1lZ2Fzb2Z0Mi92MS9hcGkv"; //"http://api.example.com/";
    return env->NewStringUTF(mUrl.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_yemen_1restaurant_greenland_shared_Urls_00024Companion_Java_1com_1onemegasoft_1greenlandrestaurant_1baseurl(
        JNIEnv *env, jobject thiz) {
    std::string mUrl = "aHR0cHM6Ly91c2VyOTkxMjMuZ3JlZW5sYW5kLXJlc3QuY29tLw=="; //"http://api.example.com/";
    return env->NewStringUTF(mUrl.c_str());
}