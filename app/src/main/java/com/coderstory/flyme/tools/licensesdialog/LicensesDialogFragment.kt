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

import android.app.Dialog
import android.content.*
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import com.coderstory.flyme.R
import com.coderstory.flyme.tools.licensesdialog.LicensesDialog
import com.coderstory.flyme.tools.licensesdialog.LicensesDialogFragment
import com.coderstory.flyme.tools.licensesdialog.NoticesHtmlBuilder
import com.coderstory.flyme.tools.licensesdialog.model.Notice
import com.coderstory.flyme.tools.licensesdialog.model.Notices

class LicensesDialogFragment  // ==========================================================================================================================
// Factory
// ==========================================================================================================================
    : DialogFragment() {
    //
    private var mTitleText: String? = null
    private var mCloseButtonText: String? = null
    private var mLicensesText: String? = null
    private var mThemeResourceId = 0
    private var mDividerColor = 0

    // ==========================================================================================================================
    // Public API
    // ==========================================================================================================================
    var onDismissListener: DialogInterface.OnDismissListener? = null

    // ==========================================================================================================================
    // Android Lifecycle
    // ==========================================================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val resources = resources
        if (savedInstanceState != null) {
            mTitleText = savedInstanceState.getString(LicensesDialogFragment.Companion.STATE_TITLE_TEXT)
            mLicensesText = savedInstanceState.getString(LicensesDialogFragment.Companion.STATE_LICENSES_TEXT)
            mCloseButtonText = savedInstanceState.getString(LicensesDialogFragment.Companion.STATE_CLOSE_TEXT)
            if (savedInstanceState.containsKey(LicensesDialogFragment.Companion.STATE_THEME_XML_ID)) {
                mThemeResourceId = savedInstanceState.getInt(LicensesDialogFragment.Companion.STATE_THEME_XML_ID)
            }
            if (savedInstanceState.containsKey(LicensesDialogFragment.Companion.STATE_DIVIDER_COLOR)) {
                mDividerColor = savedInstanceState.getInt(LicensesDialogFragment.Companion.STATE_DIVIDER_COLOR)
            }
        } else {
            mTitleText = resources.getString(R.string.notices_title)
            mCloseButtonText = resources.getString(R.string.notices_close)
            try {
                val notices: Notices?
                val arguments = arguments
                if (arguments != null) {
                    notices = if (arguments.containsKey(LicensesDialogFragment.Companion.ARGUMENT_NOTICES_XML_ID)) {
                        NoticesXmlParser.parse(resources.openRawResource(noticesXmlResourceId))
                    } else if (arguments.containsKey(LicensesDialogFragment.Companion.ARGUMENT_NOTICES)) {
                        arguments.getParcelable(LicensesDialogFragment.Companion.ARGUMENT_NOTICES)
                    } else {
                        throw IllegalStateException("Missing ARGUMENT_NOTICES_XML_ID / ARGUMENT_NOTICES")
                    }
                    if (arguments.getBoolean(LicensesDialogFragment.Companion.ARGUMENT_INCLUDE_OWN_LICENSE, false)) {
                        notices!!.notices.add(LicensesDialog.Companion.LICENSES_DIALOG_NOTICE)
                    }
                    val showFullLicenseText = arguments.getBoolean(LicensesDialogFragment.Companion.ARGUMENT_FULL_LICENSE_TEXT, false)
                    if (arguments.containsKey(LicensesDialogFragment.Companion.ARGUMENT_THEME_XML_ID)) {
                        mThemeResourceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            arguments.getInt(LicensesDialogFragment.Companion.ARGUMENT_THEME_XML_ID, android.R.style.Theme_DeviceDefault_Light_Dialog)
                        } else {
                            arguments.getInt(LicensesDialogFragment.Companion.ARGUMENT_THEME_XML_ID)
                        }
                    }
                    if (arguments.containsKey(LicensesDialogFragment.Companion.ARGUMENT_DIVIDER_COLOR)) {
                        mDividerColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            arguments.getInt(LicensesDialogFragment.Companion.ARGUMENT_DIVIDER_COLOR, android.R.color.holo_blue_light)
                        } else {
                            arguments.getInt(LicensesDialogFragment.Companion.ARGUMENT_DIVIDER_COLOR)
                        }
                    }
                    mLicensesText = NoticesHtmlBuilder.Companion.create(activity).setNotices(notices).setShowFullLicenseText(showFullLicenseText).build()
                } else {
                    throw IllegalStateException("Missing arguments")
                }
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LicensesDialogFragment.Companion.STATE_TITLE_TEXT, mTitleText)
        outState.putString(LicensesDialogFragment.Companion.STATE_LICENSES_TEXT, mLicensesText)
        outState.putString(LicensesDialogFragment.Companion.STATE_CLOSE_TEXT, mCloseButtonText)
        if (mThemeResourceId != 0) {
            outState.putInt(LicensesDialogFragment.Companion.STATE_THEME_XML_ID, mThemeResourceId)
        }
        if (mDividerColor != 0) {
            outState.putInt(LicensesDialogFragment.Companion.STATE_DIVIDER_COLOR, mDividerColor)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = LicensesDialog.Builder(activity)
                .setNotices(mLicensesText)
                .setTitle(mTitleText).setCloseText(mCloseButtonText)
                .setThemeResourceId(mThemeResourceId).setDividerColor(mDividerColor)
        val licensesDialog = builder.build()
        return if (arguments!!.getBoolean(LicensesDialogFragment.Companion.ARGUMENT_USE_APPCOMPAT, false)) {
            licensesDialog.createAppCompat()
        } else {
            licensesDialog.create()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (onDismissListener != null) {
            onDismissListener!!.onDismiss(dialog)
        }
    }

    // ==========================================================================================================================
    // Private API
    // ==========================================================================================================================
    private val noticesXmlResourceId: Int
        private get() {
            var resourceId = R.raw.notices
            val arguments = arguments
            if (arguments != null && arguments.containsKey(LicensesDialogFragment.Companion.ARGUMENT_NOTICES_XML_ID)) {
                resourceId = arguments.getInt(LicensesDialogFragment.Companion.ARGUMENT_NOTICES_XML_ID)
                check("raw".equals(resources.getResourceTypeName(resourceId), ignoreCase = true)) { "not a raw resource" }
            }
            return resourceId
        }

    // ==========================================================================================================================
    // Inner classes
    // ==========================================================================================================================
    class Builder// Set default values     // ==========================================================================================================================
    // Constructor
    // ==========================================================================================================================
    (private val mContext: Context) {
        private var mNotices: Notices? = null
        private var mRawNoticesResourceId: Int? = null
        private var mShowFullLicenseText = false
        private var mIncludeOwnLicense = true
        private var mThemeResourceId = 0
        private var mDividerColor = 0
        private var mUseAppCompat = false

        // ==========================================================================================================================
        // Public API
        // ==========================================================================================================================
        fun setNotice(notice: Notice?): LicensesDialogFragment.Builder {
            mNotices = Notices()
            mNotices!!.addNotice(notice)
            return this
        }

        fun setNotices(notices: Notices?): LicensesDialogFragment.Builder {
            mNotices = notices
            return this
        }

        fun setNotices(@RawRes rawNoticesResourceId: Int): LicensesDialogFragment.Builder {
            mRawNoticesResourceId = rawNoticesResourceId
            return this
        }

        fun setShowFullLicenseText(showFullLicenseText: Boolean): LicensesDialogFragment.Builder {
            mShowFullLicenseText = showFullLicenseText
            return this
        }

        fun setIncludeOwnLicense(includeOwnLicense: Boolean): LicensesDialogFragment.Builder {
            mIncludeOwnLicense = includeOwnLicense
            return this
        }

        fun setThemeResourceId(@StyleRes themeResourceId: Int): LicensesDialogFragment.Builder {
            mThemeResourceId = themeResourceId
            return this
        }

        fun setDividerColorRes(@ColorRes dividerColor: Int): LicensesDialogFragment.Builder {
            mDividerColor = mContext.resources.getColor(dividerColor)
            return this
        }

        fun setDividerColor(@ColorInt dividerColor: Int): LicensesDialogFragment.Builder {
            mDividerColor = dividerColor
            return this
        }

        fun setUseAppCompat(useAppCompat: Boolean): LicensesDialogFragment.Builder {
            mUseAppCompat = useAppCompat
            return this
        }

        fun build(): LicensesDialogFragment {
            return if (mNotices != null) {
                LicensesDialogFragment.Companion.newInstance(mNotices, mShowFullLicenseText, mIncludeOwnLicense, mThemeResourceId, mDividerColor, mUseAppCompat)
            } else if (mRawNoticesResourceId != null) {
                LicensesDialogFragment.Companion.newInstance(mRawNoticesResourceId, mShowFullLicenseText, mIncludeOwnLicense, mThemeResourceId, mDividerColor, mUseAppCompat)
            } else {
                throw IllegalStateException("Required parameter not set. You need to call setNotices.")
            }
        }
    }

    companion object {
        private const val ARGUMENT_NOTICES = "ARGUMENT_NOTICES"
        private const val ARGUMENT_NOTICES_XML_ID = "ARGUMENT_NOTICES_XML_ID"
        private const val ARGUMENT_INCLUDE_OWN_LICENSE = "ARGUMENT_INCLUDE_OWN_LICENSE"
        private const val ARGUMENT_FULL_LICENSE_TEXT = "ARGUMENT_FULL_LICENSE_TEXT"
        private const val ARGUMENT_THEME_XML_ID = "ARGUMENT_THEME_XML_ID"
        private const val ARGUMENT_DIVIDER_COLOR = "ARGUMENT_DIVIDER_COLOR"
        private const val ARGUMENT_USE_APPCOMPAT = "ARGUMENT_USE_APPCOMPAT"
        private const val STATE_TITLE_TEXT = "title_text"
        private const val STATE_LICENSES_TEXT = "licenses_text"
        private const val STATE_CLOSE_TEXT = "close_text"
        private const val STATE_THEME_XML_ID = "theme_xml_id"
        private const val STATE_DIVIDER_COLOR = "divider_color"
        private fun newInstance(notices: Notices,
                                showFullLicenseText: Boolean,
                                includeOwnLicense: Boolean,
                                themeResourceId: Int,
                                dividerColor: Int,
                                useAppCompat: Boolean): LicensesDialogFragment {
            val licensesDialogFragment = LicensesDialogFragment()
            val args = Bundle()
            args.putParcelable(LicensesDialogFragment.Companion.ARGUMENT_NOTICES, notices)
            args.putBoolean(LicensesDialogFragment.Companion.ARGUMENT_FULL_LICENSE_TEXT, showFullLicenseText)
            args.putBoolean(LicensesDialogFragment.Companion.ARGUMENT_INCLUDE_OWN_LICENSE, includeOwnLicense)
            args.putInt(LicensesDialogFragment.Companion.ARGUMENT_THEME_XML_ID, themeResourceId)
            args.putInt(LicensesDialogFragment.Companion.ARGUMENT_DIVIDER_COLOR, dividerColor)
            args.putBoolean(LicensesDialogFragment.Companion.ARGUMENT_USE_APPCOMPAT, useAppCompat)
            licensesDialogFragment.arguments = args
            return licensesDialogFragment
        }

        // ==========================================================================================================================
        // Constructor
        // ==========================================================================================================================
        private fun newInstance(rawNoticesResourceId: Int,
                                showFullLicenseText: Boolean,
                                includeOwnLicense: Boolean,
                                themeResourceId: Int,
                                dividerColor: Int,
                                useAppCompat: Boolean): LicensesDialogFragment {
            val licensesDialogFragment = LicensesDialogFragment()
            val args = Bundle()
            args.putInt(LicensesDialogFragment.Companion.ARGUMENT_NOTICES_XML_ID, rawNoticesResourceId)
            args.putBoolean(LicensesDialogFragment.Companion.ARGUMENT_FULL_LICENSE_TEXT, showFullLicenseText)
            args.putBoolean(LicensesDialogFragment.Companion.ARGUMENT_INCLUDE_OWN_LICENSE, includeOwnLicense)
            args.putInt(LicensesDialogFragment.Companion.ARGUMENT_THEME_XML_ID, themeResourceId)
            args.putInt(LicensesDialogFragment.Companion.ARGUMENT_DIVIDER_COLOR, dividerColor)
            args.putBoolean(LicensesDialogFragment.Companion.ARGUMENT_USE_APPCOMPAT, useAppCompat)
            licensesDialogFragment.arguments = args
            return licensesDialogFragment
        }
    }
}