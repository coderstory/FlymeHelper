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
package com.coderstory.flyme.tools.licensesdialog

import android.content.Context
import com.coderstory.flyme.R
import com.coderstory.flyme.tools.licensesdialog.licenses.License
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import com.coderstory.flyme.tools.licensesdialog.model.Notices
import java.util.*

class NoticesHtmlBuilder private constructor(private val mContext: Context) {
    private val mLicenseTextCache: MutableMap<License, String> = HashMap()
    private var mNotices: Notices? = null
    private var mNotice: Notice? = null
    private var mStyle: String
    private var mShowFullLicenseText: Boolean
    fun setNotices(notices: Notices?): NoticesHtmlBuilder {
        mNotices = notices
        mNotice = null
        return this
    }

    fun setNotice(notice: Notice?): NoticesHtmlBuilder {
        mNotice = notice
        mNotices = null
        return this
    }

    fun setStyle(style: String): NoticesHtmlBuilder {
        mStyle = style
        return this
    }

    fun setShowFullLicenseText(showFullLicenseText: Boolean): NoticesHtmlBuilder {
        mShowFullLicenseText = showFullLicenseText
        return this
    }

    fun build(): String {
        val noticesHtmlBuilder = StringBuilder(500)
        appendNoticesContainerStart(noticesHtmlBuilder)
        if (mNotice != null) {
            appendNoticeBlock(noticesHtmlBuilder, mNotice!!)
        } else if (mNotices != null) {
            for (notice in mNotices!!.notices) {
                appendNoticeBlock(noticesHtmlBuilder, notice)
            }
        } else {
            throw IllegalStateException("no notice(s) set")
        }
        appendNoticesContainerEnd(noticesHtmlBuilder)
        return noticesHtmlBuilder.toString()
    }

    //
    private fun appendNoticesContainerStart(noticesHtmlBuilder: StringBuilder) {
        noticesHtmlBuilder.append("<!DOCTYPE html><html><head>")
                .append("<style type=\"text/css\">").append(mStyle).append("</style>")
                .append("</head><body>")
    }

    private fun appendNoticeBlock(noticesHtmlBuilder: StringBuilder, notice: Notice) {
        noticesHtmlBuilder.append("<ul><li>").append(notice.name)
        val currentNoticeUrl = notice.url
        if (currentNoticeUrl != null && currentNoticeUrl.length > 0) {
            noticesHtmlBuilder.append(" (<a href=\"")
                    .append(currentNoticeUrl)
                    .append("\" target=\"_blank\">")
                    .append(currentNoticeUrl)
                    .append("</a>)")
        }
        noticesHtmlBuilder.append("</li></ul>")
        noticesHtmlBuilder.append("<pre>")
        val copyright = notice.copyright
        if (copyright != null) {
            noticesHtmlBuilder.append(copyright).append("<br/><br/>")
        }
        noticesHtmlBuilder.append(getLicenseText(notice.license)).append("</pre>")
    }

    private fun appendNoticesContainerEnd(noticesHtmlBuilder: StringBuilder) {
        noticesHtmlBuilder.append("</body></html>")
    }

    private fun getLicenseText(license: License?): String? {
        if (license != null) {
            if (!mLicenseTextCache.containsKey(license)) {
                mLicenseTextCache[license] = if (mShowFullLicenseText) license.getFullText(mContext) else license.getSummaryText(mContext)
            }
            return mLicenseTextCache[license]
        }
        return ""
    }

    companion object {
        fun create(context: Context): NoticesHtmlBuilder {
            return NoticesHtmlBuilder(context)
        }
    }

    init {
        mStyle = mContext.resources.getString(R.string.notices_default_style)
        mShowFullLicenseText = false
    }
}