# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html
# http://developer.android.com/guide/developing/tools/proguard.html
# http://stackoverflow.com/questions/20144244/warning-library-class-android-net-http-androidhttpclient-extends-or-implements

# Add any project specific keep options here:

-optimizationpasses 5

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontwarn android.support.**
-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#keep all classes that might be used in XML layouts
-keep public class * extends android.view.View
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.Fragment

#keep all public and protected methods that could be used by java reflection
-keepclassmembernames class * {
    public protected <methods>;
}

-keepclasseswithmembernames class * {
   native <methods>;
}

-keepclasseswithmembernames class * {
   public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
  public static <fields>;
}

-dontwarn **CompatHoneycomb
-dontwarn org.htmlcleaner.*
-dontwarn com.android.**
-dontwarn android.telephony.**
-dontwarn com.google.android.gms.**

# Warning ! Problem with this library ! Warning
-dontwarn com.squareup.picasso.**
-dontwarn org.brickred.**
-dontwarn org.joda.convert.**

# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ACRA specifics !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# Restore some Source file names and restore approximate line numbers in the stack traces,
# otherwise the stack traces are pretty useless
-keepattributes SourceFile,LineNumberTable

# ACRA needs "annotations" so add this...
# Note: This may already be defined in the default "proguard-android-optimize.txt"
# file in the SDK. If it is, then you don't need to duplicate it. See your
# "project.properties" file to get the path to the default "proguard-android-optimize.txt".
-keepattributes *Annotation*

# keep this class so that logging will show 'ACRA' and not a obfuscated name like 'a'.
# Note: if you are removing log messages elsewhere in this file then this isn't necessary
-keep class org.acra.ACRA {
    *;
}

# keep this around for some enums that ACRA needs
-keep class org.acra.ReportingInteractionMode {
    *;
}

-keepnames class org.acra.sender.HttpSender$** {
    *;
}

-keepnames class org.acra.ReportField {
    *;
}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter {
    public void addCustomData(java.lang.String,java.lang.String);
    public void putCustomData(java.lang.String,java.lang.String);
    public void removeCustomData(java.lang.String);
}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter {
    public void handleSilentException(java.lang.Throwable);
}