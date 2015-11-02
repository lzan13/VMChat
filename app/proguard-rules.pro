# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\develop\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# -keep class com.easemob.** {*;}
# -keep class org.jivesoftware.** {*;}
# -keep class org.apache.** {*;}
# -dontwarn  com.easemob.**
# #2.0.9后的不需要加下面这个keep
# #-keep class org.xbill.DNS.** {*;}
# #另外，demo中发送表情的时候使用到反射，需要keep SmileUtils
# -keep class com.easemob.chatuidemo.utils.SmileUtils {*;}
# #注意前面的包名，如果把这个类复制到自己的项目底下，比如放在com.example.utils底下，应该这么写(实际要去掉#)
# #-keep class com.example.utils.SmileUtils {*;}
# #如果使用easeui库，需要这么写
# -keep class com.easemob.easeui.utils.EaseSmileUtils {*;}
#
# #2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
# -dontwarn ch.imvs.**
# -dontwarn org.slf4j.**
# -keep class org.ice4j.** {*;}
# -keep class net.java.sip.** {*;}
# -keep class org.webrtc.voiceengine.** {*;}
# -keep class org.bitlet.** {*;}
# -keep class org.slf4j.** {*;}
# -keep class ch.imvs.** {*;}
