# Add project-specific ProGuard rules here.
-keepattributes *Annotation*

# Hilt
-keep class dagger.hilt.** { *; }
-keep class androidx.hilt.** { *; }

# Firebase / 구글 로그인 — R8 minify가 초기화·리플렉션 클래스를 스트립해
# 첫 진입에서 크래시하는 것 방지 (release 빌드 minify=true)
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keepattributes Signature, EnclosingMethod, InnerClasses
-dontwarn com.google.**
