-mergeinterfacesaggressively
-dontusemixedcaseclassnames
#指定代码的压缩级别
-optimizationpasses 7
-overloadaggressively
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#优化 不优化输入的类文件
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary  dictionary.txt
#预校验
-dontpreverify
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!code/simplication/cast,!field/*,!class/mergin/*
#保持哪些类不被混淆
-keep class com.coderstory.flyme10.xposed.Start

-keep class com.coderstory.flyme10.activity.MainActivity {
          boolean isEnable();
 }
#-dontwarn

-keep class com.umeng.** {*;}

-keep class org.repackage.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.coderstory.flyme10.R$*{
    public static final int *;
}
