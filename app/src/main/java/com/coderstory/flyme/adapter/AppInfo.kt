package com.coderstory.flyme.adapter


import android.graphics.drawable.Drawable

class AppInfo {
    var name: String? = null
        private set
    var imageId: Drawable? = null
        private set
    var disable = false
    var packageName = ""
        private set
    var version = ""
        private set
    var appDir: String? = null
    var versionCode = 0
        private set
    var fileSize = ""
    var releaseDate: String? = null

    constructor()
    constructor(name: String, version: String, fileSize: String, releaseDate: String?) {
        this.name = if (name.length > 25) name.substring(0, 25) + "..." else name
        this.version = version
        this.fileSize = fileSize
        this.releaseDate = releaseDate
    }

    constructor(name: String, imageId: Drawable?, packageName: String, Disable: Boolean, version: String) {
        this.name = if (name.length > 25) name.substring(0, 25) + "..." else name
        this.imageId = imageId
        this.packageName = packageName
        disable = Disable
        this.version = version
    }
}