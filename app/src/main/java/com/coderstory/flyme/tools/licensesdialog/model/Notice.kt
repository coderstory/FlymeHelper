/*
 * Copyright 2013 Philip Schiffer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.coderstory.flyme.tools.licensesdialog.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.coderstory.flyme.tools.licensesdialog.licenses.License

class Notice : Parcelable {
    var name: String? = null
    var url: String? = null
    var copyright: String? = null

    // Parcelable
    //
    var license: License? = null

    constructor()

    // Setter / Getter
    constructor(name: String?, url: String?, copyright: String?, license: License?) {
        this.name = name
        this.url = url
        this.copyright = copyright
        this.license = license
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        url = `in`.readString()
        copyright = `in`.readString()
        license = `in`.readSerializable() as License?
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(url)
        dest.writeString(copyright)
        dest.writeSerializable(license)
    }

    companion object {
        var CREATOR: Creator<Notice> = object : Creator<Notice?> {
            override fun createFromParcel(source: Parcel): Notice? {
                return Notice(source)
            }

            override fun newArray(size: Int): Array<Notice?> {
                return arrayOfNulls(size)
            }
        }
    }
}