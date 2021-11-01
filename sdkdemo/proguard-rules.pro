# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5

-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses

-verbose

-dontskipnonpubliclibraryclassmembers

-dontpreverify

-keepattributes *Annotation*,InnerClasses

-keepattributes Signature

-optimizations !code/simplification/cast,!field/*,!class/merging/*

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
#TemplateFragment
-keep public class com.huawei.hms.videoeditor.ui.template.TemplateFragment

-keep class android.support.** {*;}

# androidx
-keep class androidx.** {*;}
-keep class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder{*;}

# hvi
-keep class com.huawei.videoeditor.** {*;}
-keep class com.huawei.hvi.** {*;}
-keep class com.huawei.hvi.ability.util.** {*;}

# videoedit
-keep class com.huawei.hms.databases.** {*;}

-keep class * implements android.os.Parcelable {
     public static final android.os.Parcelable$Creator *;
 }

-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep class **.R$* {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.updatesdk.**{*;}

-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }

-keep class com.huawei.agconnect.**{*;}
-dontwarn com.huawei.agconnect.**
-keep class com.hianalytics.android.**{*;}
-keep interface com.huawei.hms.analytics.type.HAEventType{*;}
-keep interface com.huawei.hms.analytics.type.HAParamType{*;}
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable

-keep class com.huawei.hms.videoeditor.**{*;}

-repackageclasses "com.huawei.hms.videoeditor.ui.p"

-keep class org.luaj.**{*;}