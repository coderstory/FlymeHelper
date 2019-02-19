package com.coderstory.purify.adapter;

import android.graphics.drawable.Drawable;

public class AppInfo {

    private String name;
    private Drawable imageId;
    private boolean isDisable = false;
    private String packageName = "";
    private String Version = "";
    private String AppDir;
    private int VersionCode = 0;
    private String fileSize = "";
    private String releaseDate;

    public AppInfo() {

    }

    public AppInfo(String name, String version, String fileSize, String releaseDate) {
        this.name = name;
        this.Version = version;
        this.fileSize = fileSize;
        this.releaseDate = releaseDate;
    }

    public AppInfo(String name, Drawable imageId, String packageName, boolean Disable, String version) {
        this.name = name;
        this.imageId = imageId;
        this.packageName = packageName;
        this.isDisable = Disable;
        this.Version = version;
    }

    public AppInfo(String name, Drawable imageId, String packageName, boolean Disable, String appDir, String version, int VersionCode) {
        this.name = name;
        this.imageId = imageId;
        this.packageName = packageName;
        this.isDisable = Disable;
        this.setAppDir(appDir);
        this.Version = version;
        this.setVersionCode(VersionCode);
    }

    Drawable getImageId() {
        return this.imageId;
    }

    public String getVersion() {
        return this.Version;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return this.name;
    }

    public boolean getDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        this.isDisable = disable;
    }

    public String getAppDir() {
        return AppDir;
    }

    public void setAppDir(String appDir) {
        AppDir = appDir;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    private void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
