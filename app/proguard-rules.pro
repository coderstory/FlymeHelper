-dontobfuscate
-allowaccessmodification
-dontwarn **
-dontnote **
#指定代码的压缩级别
-optimizationpasses 7
#包明不混合大小写
#-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#优化 不优化输入的类文件
#-dontoptimize
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary  dictionary.txt
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!code/simplication/cast,!field/*,!class/mergin/*
#避免混淆Annotation、内部类、泛型、匿名类
#-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

#保护注解
#-keepattributes Annotation
#保持哪些类不被混淆
-keep class com.coderstory.flyme.plugins.start {
     void handleInitPackageResources(de.robv.android.xposed.callbacks.XC_InitPackageResources$InitPackageResourcesParam);
     void handleLoadPackage(de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam);
     void initZygote(de.robv.android.xposed.IXposedHookZygoteInit$StartupParam);
     }

 -keep class com.coderstory.flyme.activity.MainActivity {
          boolean isEnable();
  }
