#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_securebankapplication_BankActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://60102f166c21e10017050128.mockapi.io/labbbank/accounts/,https://60102f166c21e10017050128.mockapi.io/labbbank/config/";
    return env->NewStringUTF(hello.c_str());
}