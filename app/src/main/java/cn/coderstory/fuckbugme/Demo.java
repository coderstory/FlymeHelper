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
 * flyme6主题无限试用补丁
 * 适配主题美化6.0.7版本
 * hook函数的注释代码为hook方法的原型
 * 因为入口都是混淆的 所以每个app版本都需要单独适配
 * Created by coder on 2017/1/29.
 */

public class Demo implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("plugin was loaded");
        if (loadPackageParam.packageName.equals("com.meizu.customizecenter")) {
            XposedBridge.log("customizecenter is loading");
            XposedBridge.log("crack by coderstory");

            //隐藏root
            /**
             public static int h() {
             Object v0 = y.d("android.content.res.flymetheme.FlymeThemeUtils", "FLYME_THEME_OS");
             try {
             int v0_3 = Integer.parseInt(((String)v0));
             return v0_3;
             }
             catch(NullPointerException v0_1) {
             }
             catch(NumberFormatException v0_2) {
             }

             ((RuntimeException)v0_1).printStackTrace();
             return 3;
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.g.ae", loadPackageParam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));//-1

            //禁止试用倒计时com.meizu.customizecenter.common.theme
            /**
             public void a(boolean arg5, boolean arg6) {
             r.b(a.b, "startThemeRestoreService");
             String v0 = this.d().a().getPackageName();
             Intent v1 = new Intent(this.d, ThemeRestoreService.class);
             v1.putExtra(h.f.a(), v0);
             v1.putExtra("is_go_to_pay_theme", arg6);
             v1.putExtra("is_restore_last_key", arg5);
             this.d.startService(v1);
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a", boolean.class, boolean.class, XC_MethodReplacement.returnConstant(null));


            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.RestoreProgressActivity", loadPackageParam.classLoader, "e", XC_MethodReplacement.returnConstant(null));//-1

            /**
             private void a(String arg5) {
             long v0 = SystemClock.elapsedRealtime();
             this.b(v0, arg5);
             this.k.putExtra("package_name", arg5);
             this.k.putExtra("start_trial_time", v0);
             this.k.putExtra("stop_trial_now", false);
             this.k.putExtra("show_pay_notification_after_stop_trial", true);
             this.d.startService(this.k);
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a", String.class, XC_MethodReplacement.returnConstant(null));//-1

            /**
             public void a(long arg4, String arg6) {
             this.k.putExtra("package_name", arg6);
             this.k.putExtra("start_trial_time", arg4);
             this.k.putExtra("stop_trial_now", false);
             this.d.startService(this.k);
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a", long.class, String.class, XC_MethodReplacement.returnConstant(null));//-1

            /**
             public void a(boolean arg5) {
             String v0 = null;
             if(arg5) {
             ThemeData v2 = this.d().a();
             if(v2 != null) {
             v0 = v2.getPackageName();
             }

             if(v0 == null) {
             goto label_10;
             }

             this.k.putExtra("package_name", v0);
             }

             label_10:
             this.k.putExtra("stop_trial_now", true);
             Intent v2_1 = this.k;
             String v3 = "show_pay_notification_after_stop_trial";
             boolean v0_1 = !arg5 || v0 == null ? false : true;
             v2_1.putExtra(v3, v0_1);
             this.d.startService(this.k);
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.a", loadPackageParam.classLoader, "a", boolean.class, XC_MethodReplacement.returnConstant(null));//-1
            //主题恢复相关
            /**
             public boolean a() {
             return 1;  //本方法以被修改
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", loadPackageParam.classLoader, "a", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    return true;
                }
            });
            // 主题恢复相关
            /**
             public void b(boolean arg10) {
             int v0 = 0;
             SharedPreferences v1 = this.e.getSharedPreferences("com.meizu.customizecenter.service", 0);
             SharedPreferences$Editor v2 = v1.edit();
             v2.putString("restore_basetheme", v1.getString("use_basetheme", com.meizu.customizecenter.common.theme.common.a.g));
             String[] v3 = this.e.getResources().getStringArray(2131427345);
             int v4 = v3.length;
             while(v0 < v4) {
             v2.putString("restore_mixed_item_" + v3[v0], v1.getString("mixed_item_" + v3[v0], com.meizu.customizecenter.common.theme.common.a.g));
             ++v0;
             }

             v2.putString("restore_mixed_item_lockscreenWallpaper", v1.getString("mixed_item_lockscreenWallpaper", com.meizu.customizecenter.common.theme.common.a.g));
             v2.putString("restore_mixed_item_launcherWallpaper", v1.getString("mixed_item_launcherWallpaper", com.meizu.customizecenter.common.theme.common.a.g));
             if(arg10) {
             c.b(this.e, c.a(this.e));
             }
             else {
             v2.remove("restore_mixed_project_name");
             }

             v2.apply();
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", loadPackageParam.classLoader, "b", boolean.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    return null;
                }
            });

            /**
             private void g() {
             Intent v0 = new Intent(((Context)this), OnlineThemeActivity.class);
             v0.putExtra(h.a.a(), h.c.a());
             v0.putExtra(h.f.a(), this.e);
             v0.putExtra("theme_trial_purchase", true);
             v0.setFlags(805306368);
             v0.putExtra("event_path", this.c);
             this.startActivity(v0);
             }
             **/
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.service.ThemeRestoreService", loadPackageParam.classLoader, "g", XC_MethodReplacement.returnConstant(null));


            /**
             private void a(Intent arg4) {
             this.d = arg4.getBooleanExtra("is_restore_last_key", true);
             this.f = arg4.getBooleanExtra("is_go_to_pay_theme", false);
             String v0 = TextUtils.isEmpty(arg4.getStringExtra("event_path")) ? this.getClass().getSimpleName() : arg4.getStringExtra("event_path");
             this.c = v0;
             this.e = this.g.d().a().getPackageName();
             this.h = this.g.f();
             v0 = TextUtils.isEmpty(this.h.d()) ? this.h.a().getName() : this.h.d();
             this.i = v0;
             ThemeRestoreService.a = 1;
             }
             */
            XposedHelpers.findAndHookMethod("com.meizu.customizecenter.service.ThemeRestoreService", loadPackageParam.classLoader, "a", int.class, XC_MethodReplacement.returnConstant(null));

        }
    }
}
