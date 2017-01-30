package cn.coderstory.fuckbugme;


import android.content.Context;

import org.json.JSONObject;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by coder on 2017/1/29.
 */

public class Demo implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("plugin was loaded");
        if (loadPackageParam.packageName.equals("com.meizu.customizecenter")) {
            XposedBridge.log("customizecenter is loading");
            XposedBridge.log("crack by coderstory");
            //隐藏root 已废弃
            XposedHelpers.findAndHookMethod("com.meizu.statsapp.util.Utils", loadPackageParam.classLoader, "isRoot", Context.class, XC_MethodReplacement.returnConstant(false));

            //隐藏root 已废弃
            XposedHelpers.findAndHookMethod("com.meizu.statsapp.toolsfortablet.DeviceUtils", loadPackageParam.classLoader, "getDeviceRoot", XC_MethodReplacement.returnConstant("0"));

            //修改主题价格
            // XposedHelpers.findAndHookMethod("com.meizu.customizecenter.model.theme.ThemeInfo", loadPackageParam.classLoader, "getPrice", XC_MethodReplacement.returnConstant(0.0d));

            //设置为免费主题
            //XposedHelpers.findAndHookMethod("com.meizu.customizecenter.model.theme.ThemeInfo", loadPackageParam.classLoader, "isFree", XC_MethodReplacement.returnConstant(true));

            // XposedHelpers.findAndHookMethod("com.meizu.customizecenter.g.af", loadPackageParam.classLoader, "c",Object.class, XC_MethodReplacement.returnConstant(true));

            //XposedHelpers.findAndHookMethod("com.meizu.customizecenter.g.af", loadPackageParam.classLoader, "e",JSONObject.class,String.class, XC_MethodReplacement.returnConstant(true));

            //隐藏root
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.g.ae", loadPackageParam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));//-1

            //禁止试用倒计时com.meizu.customizecenter.common.theme
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a", boolean.class, boolean.class, XC_MethodReplacement.returnConstant(null));
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.RestoreProgressActivity", loadPackageParam.classLoader, "e", XC_MethodReplacement.returnConstant(null));//-1

            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a",String.class, XC_MethodReplacement.returnConstant(null));//-1
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a",long.class,String.class, XC_MethodReplacement.returnConstant(null));//-1
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a", boolean.class,XC_MethodReplacement.returnConstant(null));//-1
          //  XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "e", XC_MethodReplacement.returnConstant(null));//-1
            //主题恢复相关
//            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", loadPackageParam.classLoader, "a", new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                    return true;
//                }
//            });
            //主题恢复相关
//            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", loadPackageParam.classLoader, "b", boolean.class, new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                    return null;
//                }
//            });
        }
    }
}
