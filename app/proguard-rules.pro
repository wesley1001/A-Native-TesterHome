# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/vclub/android/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class com.testerhome.nativeandroid.fragments.MarkdownFragment$JavascriptInterface {
   public *;
}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

-dontwarn com.squareup.okhttp.**

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**


-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class com.testerhome.nativeandroid.models.** {*;}
-dontwarn com.testerhome.nativeandroid.models.**

-keep class com.testerhome.nativeandroid.dao.** {*;}
-dontwarn com.testerhome.nativeandroid.dao.**

-dontwarn com.tencent.**
-keep class com.tencent.** {*;}



# RxJava 1.1.0 --start--
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
# RxJava 1.1.0 --end--

#Gson
-keepattributes Signature

-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static int v(...);
public static int i(...);
public static int w(...);
public static int d(...);
public static int e(...);
}

-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties


## Android 6.0
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient

-dontwarn com.google.android.**
-keep class com.google.android.** {*;}

-keep class android.support.v7.** {*;}

## retrolambda
-dontwarn java.lang.invoke.*

-dontwarn com.tonicartos.**
-keep class com.tonicartos.** {*;}

## bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}