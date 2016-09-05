# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, these files are no longer used. Newer
# versions are distributed with the plugin and unpacked at build time. Files in this directory are
# no longer maintained.

# 是否混淆第三方jar
-dontskipnonpubliclibraryclasses
# 是否使用大小写混合
-dontusemixedcaseclassnames
-dontwarn
# 忽略警告，避免打包时某些警告出现
-ignorewarnings
# 混淆时是否记录日志
-verbose
# 指定代码的压缩级别
-optimizationpasses 5
# 混淆时是否做预校验
-dontpreverify

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *JavascriptInterface*

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持Parcelable不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * extends android.webkit.WebChromeClient
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * implements com.lphtsccft.android.simple.app.ConfigInterface
-keep class com.lphtsccft.android.simple.tool.web.** { *; }
-keep public class * extends android.support.v4.widget
-keep class android.support.v4.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class cn.com.infosec.mobile.android.** { *; }
-keep class cn.microdone.sercurity.** { *; }
-keep class ext.org.bouncycastle.** { *; }
-keep class org.webrtc.videoengine.** { *; }
-keep class org.webrtc.voiceengine.** { *; }
-keep class com.apexsoft.cowork.** { *; }
-keep class com.bairuitech.anychat.** { *; }
-keep class com.lphtsccft.rtdl.share.**{ *; }
-keep class com.lphtsccft.rtdl.palmhall.**{ *; }

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-keepattributes Signature
-keepattributes InnerClasses

# 环信 SDK 混淆代码
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
# 2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}

# 小米推送混淆代码，这里是环信sdk 内部实现的，直接写这个类
-keep class com.hyphenate.chat.EMMipushReceiver {*;}


# TalkingData 数据统计平台混淆代码
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
    public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}


# 使用EventBus 时要防止注解的方法被混淆
-keepclassmembers class ** {
	public <fields>;
    public void onEvent*(**);
}
# -keepclassmembers class ** {
#     @org.greenrobot.eventbus.Subscribe <methods>;
# }
# -keep enum org.greenrobot.eventbus.ThreadMode {
#       *;
# }
# # Only required if you use AsyncExecutor
# -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#     <init>(java.lang.Throwable);
# }