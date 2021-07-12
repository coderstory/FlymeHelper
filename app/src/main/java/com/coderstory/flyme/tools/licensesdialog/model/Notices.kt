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
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import java.util.*

class Notices : Parcelable {
    private val mNotices: MutableList<Notice?>

    // Setter / Getter
    constructor() {
        mNotices = ArrayList()
    }

    protected constructor(`in`: Parcel) {
        mNotices = ArrayList()
        `in`.readList(mNotices, Notice::class.java.classLoader)
    }

    // Parcelable
    fun addNotice(notice: Notice?) {
        mNotices.add(notice)
    }

    val notices: List<Notice?>
        get() = mNotices

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(mNotices)
    }

    companion object {
        var CREATOR: Creator<Notices> = object : Creator<Notices?> {
            override fun createFromParcel(source: Parcel): Notices? {
                return Notices(source)
            }

            override fun newArray(size: Int): Array<Notices?> {
                return arrayOfNulls(size)
            }
        }
    }
}