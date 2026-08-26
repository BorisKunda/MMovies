# Gson reads DTO fields reflectively, so R8 must not rename or strip them.
# Without this the release build parses every TMDB response into all-null DTOs.
-keep class com.bk.mmovies.data.source.remote.dto.** { *; }
-keepclassmembers,allowobfuscation class com.bk.mmovies.data.source.remote.dto.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson's TypeToken relies on generic signatures surviving.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-dontwarn sun.misc.**
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Retrofit interfaces are proxied at runtime; keep their signatures/annotations.
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Navigation destinations are resolved through kotlinx.serialization.
-keepclassmembers class com.bk.mmovies.ui.navigation.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep line numbers so Crashlytics stack traces stay readable.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
