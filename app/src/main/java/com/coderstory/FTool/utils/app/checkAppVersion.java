package com.coderstory.FTool.utils.app;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.coderstory.FTool.utils.MyConfig;

import java.util.List;

/**
 * Created by coder on 2017/5/25.
 */

public class checkAppVersion {
    private static final String TAG = "checkAppVersion";
    //检查系统安装的主题美化版本是否受支持
    public boolean isSupport(Context mContext) {
        boolean result = false;
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        if (packages != null) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (packageInfo.packageName.equals("com.meizu.customizecenter")) {

                    for (String s : MyConfig.versions) {
                        if (s.equals(packageInfo.versionName)) {
                            result = true;
                            break;
                        }
                    }
                    if (result) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}
