# --------------------------------------------基本指令区--------------------------------------------#
-ignorewarning                                      # 是否忽略警告
-optimizationpasses 5                               # 指定代码的压缩级别(在0~7之间，默认为5)
-dontusemixedcaseclassnames                         # 是否使用大小写混合(windows大小写不敏感，建议加入)
-dontskipnonpubliclibraryclasses                    # 是否混淆非公共的库的类
-dontskipnonpubliclibraryclassmembers               # 是否混淆非公共的库的类的成员
-dontpreverify                                      # 混淆时是否做预校验(Android不需要预校验，去掉可以加快混淆速度)
-verbose                                            # 混淆时是否记录日志(混淆后会生成映射文件)

#混淆时所采用的算法(谷歌推荐算法)
-optimizations !code/simplification/arithmetic,!field,!class/merging,!code/allocation/variable

#添加支持的jar(引入libs下的所有jar包)
-libraryjars libs(*.jar;)

#将文件来源重命名为“SourceFile”字符串
#-renamesourcefileattribute SourceFile

#保持注解不被混淆
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

#保持泛型不被混淆
-keepattributes Signature
#保持反射不被混淆
-keepattributes EnclosingMethod
#保持异常不被混淆
-keepattributes Exceptions
#保持内部类不被混淆
-keepattributes Exceptions,InnerClasses
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#Support包规则
-dontwarn android.support.**
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
  native <methods>;
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持R(资源)下的所有类及其方法不能被混淆
-keep class **.R$* { *; }

#保持 Parcelable 序列化的类不被混淆(注：aidl文件不能去混淆)
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#需要序列化和反序列化的类不能被混淆(注：Java反射用到的类也不能被混淆)
-keepnames class * implements java.io.Serializable

#持 Serializable 序列化的类成员不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-dontwarn kotlin.Unit

-dontwarn javax.annotation.**
-dontwarn javax.inject.**

#不混淆某个类
-keep public class com.eaphone.lib_sdk.sdk.EaphoneInterface { *; }

