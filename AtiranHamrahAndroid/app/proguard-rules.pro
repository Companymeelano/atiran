# قوانین R8 برای پروژه آتیران همراه

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keep,includedescriptorclasses class ir.atiran.hamrah.**$$serializer { *; }
-keepclassmembers class ir.atiran.hamrah.** {
    *** Companion;
}
-keepclasseswithmembers class ir.atiran.hamrah.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
