#include <jni.h>
#include <string>
#include <pthread.h>
#include <unistd.h>
#include <cstdio>
#include <cstdlib>
#include <sys/ptrace.h>
#include <android/log.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "test", __VA_ARGS__)

const int handledSignals[] = {SIGSEGV, SIGABRT, SIGFPE, SIGILL, SIGBUS};
const int handledSignalsNum = sizeof(handledSignals) / sizeof(handledSignals[0]);
struct sigaction old_handlers[sizeof(handledSignals) / sizeof(handledSignals[0])];
//校验签名
static int is_verify = 0;
const char *PACKAGE_NAME = "com.coderstory.flyme";
const char *app_signature = "308203933082027ba00302010202043936013b300d06092a864886f70d01010b0500307a310b30090603550406130238363111300f060355040813087a68656a69616e673111300f0603550407130868616e677a686f7531133011060355040a130a636f64657273746f7279311b3019060355040b1312626c6f672e636f64657273746f72792e636e311330110603550403130a636f64657273746f7279301e170d3136303631303036333833365a170d3431303630343036333833365a307a310b30090603550406130238363111300f060355040813087a68656a69616e673111300f0603550407130868616e677a686f7531133011060355040a130a636f64657273746f7279311b3019060355040b1312626c6f672e636f64657273746f72792e636e311330110603550403130a636f64657273746f727930820122300d06092a864886f70d01010105000382010f003082010a02820101008605cd61ef0a52d0c5e6a34a504bae45763506b610c95f2cb616a1f8c8aef554e436b46d8e3a9f5cbe4bbecd1b1bf2ed5df98aa2b8ab21a71791deb0d03969de6343258892b879f85a3b6c302b1df5e8d9526c8302dd01d7c255a752223b14a5cdbb3975a278b50a8e9d3ac29dde3958fd35491f3c11b5f2f64fed81d50b7d498fceef303586335c37b521267ff6d76e2b9086a2edb9feb4c84e97d4fbf9792e92810ac8dacbe7468c80eee7d6383e07883b3b790fa4987db876719c67f11f1028cf5e53706c03150311df080823bdf288c9f768e5d04de00b6a8e49e13c61e2bb08d8c549d4aa1663e02979d795fa18723b3f869d3e338804f56cda6bafadf70203010001a321301f301d0603551d0e04160414ddf940cecf89e1ae5eabcd9046ae0c3bcd7937d6300d06092a864886f70d01010b0500038201010010396c8c5ce8322ccb5c21b4e22ddababb967b6336f2d49c5a90bb20ea1ce0ddd94aa345dc62c3f6a27bc79172ac580cccfa51ff5d3af6d561e06d1710dcd22420e0a0c3ebfe867a0f70cafd9264ae863c95e304b6b660e52600a70432517fecb4bebfa239e0748eaf47e4d89ac56a681f984985c2c35eb1aaea291f63bf37f6ea045f91faec39b57f20c306d95579861759bd4dab27fcaff8fcd6e75d1f48059472b64841e946c4d8d59e95674116b3005743b799c66c1b93e50f165a0670c697c8e2f18e1963b47abb65b48214b887b415b16fbb199f3a40e787c1f2b7f1db7e740079351f09177e776f313a9770ed42af6e10529fc2ecaca12ed2e847cdd9";

extern "C"
JNIEXPORT void JNICALL
Java_com_coderstory_flyme_tools_Cpp_firstCpp(JNIEnv *env, jclass type, jobject context) {
    // 1. 获取包名
    jclass j_clz = env->GetObjectClass(context);
    jmethodID j_mid = env->GetMethodID(j_clz, "getPackageName", "()Ljava/lang/String;");
    auto j_package_name = (jstring) env->CallObjectMethod(context, j_mid);
    // 2 . 比对包名是否一样
    const char *c_package_name = env->GetStringUTFChars(j_package_name, nullptr);
    if (strcmp(c_package_name, PACKAGE_NAME) != 0) {
        return;
    }
    // 3. 获取签名
    // 3.1 获取 PackageManager
    j_mid = env->GetMethodID(j_clz, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pack_manager = env->CallObjectMethod(context, j_mid);
    // 3.2 获取 PackageInfo
    j_clz = env->GetObjectClass(pack_manager);
    j_mid = env->GetMethodID(j_clz, "getPackageInfo",
                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    //后两个代表构造函数中的参数,0x00000040看 PackageManager.GET_SIGNATURES源码就可以知道了0x00000040==PackageManager.GET_SIGNATURES
    jobject package_info = env->CallObjectMethod(pack_manager, j_mid, j_package_name, 0x00000040);
    // 3.3 获取 signatures 数组
    j_clz = env->GetObjectClass(package_info);
    if (is_verify == JNI_TRUE) {
        exit(0);
    }
    jfieldID j_fid = env->GetFieldID(j_clz, "signatures", "[Landroid/content/pm/Signature;");
    auto signatures = (jobjectArray) env->GetObjectField(package_info, j_fid);
    // 3.4 获取 signatures[0]
    jobject signatures_first = env->GetObjectArrayElement(signatures, 0);
    // 3.5 调用 signatures[0].toCharsString();
    j_clz = env->GetObjectClass(signatures_first);
    j_mid = env->GetMethodID(j_clz, "toCharsString", "()Ljava/lang/String;");
    auto j_signature_str = (jstring) env->CallObjectMethod(signatures_first, j_mid);
    const char *c_signature_str = env->GetStringUTFChars(j_signature_str, NULL);
    // 4. 比对签名是否一样
    if (strcmp(c_signature_str, app_signature) != 0) {
        return;
    }

    //__android_log_print(ANDROID_LOG_ERROR, "JNI_TAG", "签名校验成功: %s", c_signature_str);
    // 签名认证成功
    is_verify = 1;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coderstory_flyme_tools_Cpp_runTest(JNIEnv *env, jclass type) {
    if (is_verify == JNI_FALSE) {
        exit(0);
    }
}

extern "C" JNIEXPORT jstring
JNICALL Java_com_coderstory_flyme_tools_Cpp_helloWorld(JNIEnv *env, jclass clazz) {
    if (is_verify == JNI_FALSE) {
        exit(0);
    }
    std::string hello = "aHR0cDovLzExOC4yNS4xMDkuMTIxOjEwMDg2L3NtYXJ0LWFkbWluLWFwaS9lbXBsb3llZS9jaGVja1Yy";
    return env->NewStringUTF(hello.c_str());
}
