package com.coderstory.FTool.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.coderstory.FTool.R;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;


public class XposedSettingActivity extends PreferenceActivity {

    static String ApplicationName = "com.coderstory.FTool";
    public static final String PREFS_FOLDER = " /data/data/" + ApplicationName + "/shared_prefs\n";
    static String SharedPreferencesName = "com.coderstory.FTool_preferences";
    public static final String PREFS_FILE = " /data/data/" + ApplicationName + "/shared_prefs/" + SharedPreferencesName + ".xml\n";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.launcher_setting);
        findPreference("enableThemePatch").setOnPreferenceClickListener(preference -> {
            sudoFixPermissions();
            return true;
        });

        findPreference("enableCheckInstaller").setOnPreferenceClickListener(preference -> {
            sudoFixPermissions();
            return true;
        });

        findPreference("enableCTS").setOnPreferenceClickListener(preference -> {
            sudoFixPermissions();
            return true;
        });

        findPreference("launcherMMO").setOnPreferenceClickListener(preference -> {
            sudoFixPermissions();
            return true;
        });
    }


    protected void sudoFixPermissions() {
        new Thread(() -> {
            File pkgFolder = new File("/data/data/" + ApplicationName);
            if (pkgFolder.exists()) {
                pkgFolder.setExecutable(true, false);
                pkgFolder.setReadable(true, false);
            }
            Shell.SU.run("chmod  755 " + PREFS_FOLDER);
            // Set preferences file permissions to be world readable
            Shell.SU.run("chmod  644 " + PREFS_FILE);
        }).start();
    }
}
