package com.coderstory.flyme.tools

import android.util.Log

/*
 * Logger
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:54
 */ /**
 * The type Logger.
 */
object Logger {
    /**
     * The constant TAG.
     */
    const val TAG = "flyme"

    /**
     * Logi.
     *
     * @param msg the msg
     */
    fun logi(msg: String) {
        Log.i(TAG, msg)
    }

    /**
     * Loge.
     *
     * @param msg the msg
     */
    fun loge(msg: String) {
        Log.e(TAG, msg)
    }

    /**
     * Logw.
     *
     * @param msg the msg
     */
    fun logw(msg: String) {
        Log.w(TAG, msg)
    }

    /**
     * Logd.
     *
     * @param msg the msg
     */
    fun logd(msg: String) {
        Log.d(TAG, msg)
    }

    /**
     * Logv.
     *
     * @param msg the msg
     */
    fun logv(msg: String) {
        Log.v(TAG, msg)
    }
}