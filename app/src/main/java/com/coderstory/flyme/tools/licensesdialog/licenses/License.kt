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
package com.coderstory.flyme.tools.licensesdialog.licenses

import android.content.Context
import com.coderstory.flyme.tools.licensesdialog.licenses.License
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Serializable

abstract class License : Serializable {
    private var mCachedSummaryText: String? = null
    private var mCachedFullText: String? = null
    abstract val name: String
    abstract fun readSummaryTextFromResources(context: Context?): String?
    abstract fun readFullTextFromResources(context: Context?): String?
    abstract val version: String
    abstract val url: String

    //
    fun getSummaryText(context: Context?): String? {
        if (mCachedSummaryText == null) {
            mCachedSummaryText = readSummaryTextFromResources(context)
        }
        return mCachedSummaryText
    }

    fun getFullText(context: Context?): String? {
        if (mCachedFullText == null) {
            mCachedFullText = readFullTextFromResources(context)
        }
        return mCachedFullText
    }

    protected fun getContent(context: Context, contentResourceId: Int): String {
        var reader: BufferedReader? = null
        try {
            val inputStream = context.resources.openRawResource(contentResourceId)
            if (inputStream != null) {
                reader = BufferedReader(InputStreamReader(inputStream))
                return toString(reader)
            }
            throw IOException("Error opening license file.")
        } catch (e: IOException) {
            throw IllegalStateException(e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    // Don't care.
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun toString(reader: BufferedReader): String {
        val builder = StringBuilder()
        var line: String? = null
        while (reader.readLine().also { line = it } != null) {
            builder.append(line).append(License.Companion.LINE_SEPARATOR)
        }
        return builder.toString()
    }

    companion object {
        private const val serialVersionUID = 3100331505738956523L
        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }
}