#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring
JNICALL Java_com_coderstory_flyme_utils_Cpp_hello(JNIEnv *env, jclass clazz) {
    std::string hello = "aHR0cDovLzExOC4yNS4xMDkuMTIxOjEwMDg2L3NtYXJ0LWFkbWluLWFwaS9lbXBsb3llZS9jaGVja1Yy";
    return env->NewStringUTF(hello.c_str());
}