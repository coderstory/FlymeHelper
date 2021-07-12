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

import android.app.*
import android.content.*
import android.net.Uri
import android.os.*
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.coderstory.flyme.R
import com.coderstory.flyme.tools.licensesdialog.LicensesDialog
import com.coderstory.flyme.tools.licensesdialog.NoticesHtmlBuilder
import com.coderstory.flyme.tools.licensesdialog.licenses.ApacheSoftwareLicense20
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import com.coderstory.flyme.tools.licensesdialog.model.Notices

class LicensesDialog  // ==========================================================================================================================
// Constructor
// ==========================================================================================================================
private constructor(private val mContext: Context, private val mLicensesText: String, private val mTitleText: String, private val mCloseText: String,
                    private val mThemeResourceId: Int,
                    private val mDividerColor: Int) {
    private var mOnDismissListener: DialogInterface.OnDismissListener? = null
    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): LicensesDialog {
        mOnDismissListener = onDismissListener
        return this
    }

    // ==========================================================================================================================
    // Private API
    // ==========================================================================================================================
    fun create(): Dialog {
        //Get resources
        val webView: WebView = LicensesDialog.Companion.createWebView(mContext)
        webView.loadDataWithBaseURL(null, mLicensesText, "text/html", "utf-8", null)
        val builder: AlertDialog.Builder
        builder = if (mThemeResourceId != 0) {
            AlertDialog.Builder(ContextThemeWrapper(mContext, mThemeResourceId))
        } else {
            AlertDialog.Builder(mContext)
        }
        builder.setTitle(mTitleText)
                .setView(webView)
                .setPositiveButton(mCloseText) { dialogInterface, i -> dialogInterface.dismiss() }
        val dialog = builder.create()
        dialog.setOnDismissListener { dialog ->
            if (mOnDismissListener != null) {
                mOnDismissListener!!.onDismiss(dialog)
            }
        }
        dialog.setOnShowListener {
            if (mDividerColor != 0) {
                // Set title divider color
                val titleDividerId = mContext.resources.getIdentifier("titleDivider", "id", "android")
                val titleDivider = dialog.findViewById<View>(titleDividerId)
                titleDivider?.setBackgroundColor(mDividerColor)
            }
        }
        return dialog
    }

    fun createAppCompat(): Dialog {
        //Get resources
        val webView = WebView(mContext)
        webView.loadDataWithBaseURL(null, mLicensesText, "text/html", "utf-8", null)
        val builder: AlertDialog.Builder
        builder = if (mThemeResourceId != 0) {
            AlertDialog.Builder(ContextThemeWrapper(mContext, mThemeResourceId))
        } else {
            AlertDialog.Builder(mContext)
        }
        builder.setTitle(mTitleText)
                .setView(webView)
                .setPositiveButton(mCloseText) { dialogInterface, i -> dialogInterface.dismiss() }
        val dialog = builder.create()
        dialog.setOnDismissListener { dialog ->
            if (mOnDismissListener != null) {
                mOnDismissListener!!.onDismiss(dialog)
            }
        }
        dialog.setOnShowListener {
            if (mDividerColor != 0) {
                // Set title divider color
                val titleDividerId = mContext.resources.getIdentifier("titleDivider", "id", "android")
                val titleDivider = dialog.findViewById<View>(titleDividerId)
                titleDivider?.setBackgroundColor(mDividerColor)
            }
        }
        return dialog
    }

    fun show(): Dialog {
        val dialog = create()
        dialog.show()
        return dialog
    }

    fun showAppCompat(): Dialog {
        val dialog = createAppCompat()
        dialog.show()
        return dialog
    }

    // ==========================================================================================================================
    // Inner classes
    // ==========================================================================================================================
    class Builder(private val mContext: Context) {
        // Default values
        private var mTitleText: String
        private var mCloseText: String
        private var mRawNoticesId: Int? = null
        private var mNotices: Notices? = null
        private var mNoticesText: String? = null
        private var mNoticesStyle: String
        private var mShowFullLicenseText: Boolean
        private var mIncludeOwnLicense: Boolean
        private var mThemeResourceId: Int
        private var mDividerColor: Int
        fun setTitle(titleId: Int): LicensesDialog.Builder {
            mTitleText = mContext.getString(titleId)
            return this
        }

        fun setTitle(title: String): LicensesDialog.Builder {
            mTitleText = title
            return this
        }

        fun setCloseText(closeId: Int): LicensesDialog.Builder {
            mCloseText = mContext.getString(closeId)
            return this
        }

        fun setCloseText(closeText: String): LicensesDialog.Builder {
            mCloseText = closeText
            return this
        }

        fun setNotices(rawNoticesId: Int): LicensesDialog.Builder {
            mRawNoticesId = rawNoticesId
            mNotices = null
            return this
        }

        fun setNotices(notices: Notices?): LicensesDialog.Builder {
            mNotices = notices
            mRawNoticesId = null
            return this
        }

        fun setNotices(notice: Notice?): LicensesDialog.Builder {
            return setNotices(LicensesDialog.Companion.getSingleNoticeNotices(notice))
        }

        fun setNotices(notices: String?): LicensesDialog.Builder {
            mNotices = null
            mRawNoticesId = null
            mNoticesText = notices
            return this
        }

        fun setNoticesCssStyle(cssStyleTextId: Int): LicensesDialog.Builder {
            mNoticesStyle = mContext.getString(cssStyleTextId)
            return this
        }

        fun setNoticesCssStyle(cssStyleText: String): LicensesDialog.Builder {
            mNoticesStyle = cssStyleText
            return this
        }

        fun setShowFullLicenseText(showFullLicenseText: Boolean): LicensesDialog.Builder {
            mShowFullLicenseText = showFullLicenseText
            return this
        }

        fun setIncludeOwnLicense(includeOwnLicense: Boolean): LicensesDialog.Builder {
            mIncludeOwnLicense = includeOwnLicense
            return this
        }

        fun setThemeResourceId(themeResourceId: Int): LicensesDialog.Builder {
            mThemeResourceId = themeResourceId
            return this
        }

        fun setDividerColor(dividerColor: Int): LicensesDialog.Builder {
            mDividerColor = dividerColor
            return this
        }

        fun setDividerColorId(dividerColorId: Int): LicensesDialog.Builder {
            mDividerColor = mContext.resources.getColor(dividerColorId)
            return this
        }

        fun build(): LicensesDialog {
            val licensesText: String
            licensesText = if (mNotices != null) {
                LicensesDialog.Companion.getLicensesText(mContext, mNotices, mShowFullLicenseText, mIncludeOwnLicense, mNoticesStyle)
            } else if (mRawNoticesId != null) {
                LicensesDialog.Companion.getLicensesText(mContext, LicensesDialog.Companion.getNotices(mContext, mRawNoticesId), mShowFullLicenseText, mIncludeOwnLicense,
                        mNoticesStyle)
            } else if (mNoticesText != null) {
                mNoticesText
            } else {
                throw IllegalStateException("Notices have to be provided, see setNotices")
            }
            return LicensesDialog(mContext, licensesText, mTitleText, mCloseText, mThemeResourceId, mDividerColor)
        }

        init {
            mTitleText = mContext.getString(R.string.notices_title)
            mCloseText = mContext.getString(R.string.notices_close)
            mNoticesStyle = mContext.getString(R.string.notices_default_style)
            mShowFullLicenseText = false
            mIncludeOwnLicense = false
            mThemeResourceId = 0
            mDividerColor = 0
        }
    }

    companion object {
        val LICENSES_DIALOG_NOTICE = Notice("LicensesDialog", "http://psdev.de/LicensesDialog",
                "Copyright 2013-2016 Philip Schiffer",
                ApacheSoftwareLicense20())

        // ==========================================================================================================================
        // Public API
        // ==========================================================================================================================
        private fun createWebView(context: Context): WebView {
            val webView = WebView(context)
            webView.settings.setSupportMultipleWindows(true)
            webView.webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
                    val result = view.hitTestResult
                    val data = result.extra
                    if (data != null) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                        context.startActivity(browserIntent)
                    }
                    return false
                }
            }
            return webView
        }

        private fun getNotices(context: Context, rawNoticesResourceId: Int): Notices {
            return try {
                val resources = context.resources
                if ("raw" == resources.getResourceTypeName(rawNoticesResourceId)) {
                    NoticesXmlParser.parse(resources.openRawResource(rawNoticesResourceId))
                } else {
                    throw IllegalStateException("not a raw resource")
                }
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }

        private fun getLicensesText(context: Context, notices: Notices, showFullLicenseText: Boolean,
                                    includeOwnLicense: Boolean, style: String): String {
            return try {
                if (includeOwnLicense) {
                    val noticeList = notices.notices
                    noticeList.add(LicensesDialog.Companion.LICENSES_DIALOG_NOTICE)
                }
                NoticesHtmlBuilder.Companion.create(context).setShowFullLicenseText(showFullLicenseText).setNotices(notices).setStyle(style).build()
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }

        private fun getSingleNoticeNotices(notice: Notice): Notices {
            val notices = Notices()
            notices.addNotice(notice)
            return notices
        }
    }
}