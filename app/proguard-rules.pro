# PinPulse keeps the default ProGuard rules; Compose + Hilt + Room ship their own
# consumer rules. Pinterest DTOs are deserialized via kotlinx.serialization.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.pinpulse.** { *; }
