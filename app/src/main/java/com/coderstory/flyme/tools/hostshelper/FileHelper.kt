package com.coderstory.flyme.tools.hostshelper

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat

class FileHelper {
    /**
     * 从Assets中读取文本
     *
     * @param FileName 文件名
     * @param mContext context
     * @return 读取到的文本
     */
    fun getFromAssets(FileName: String?, mContext: Context?): String {
        return try {
            val inputReader = InputStreamReader(mContext!!.assets.open(FileName!!), StandardCharsets.UTF_8)
            val bufReader = BufferedReader(inputReader)
            var line: String?
            val Result = StringBuilder()
            while (bufReader.readLine().also { line = it } != null) Result.append(line).append("\n")
            Result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    companion object {
        fun getReadableFileSize(size: Long): String {
            val units = arrayOf("K", "M", "G", "T", "P")
            var nSize = (size * 1L * 1.0f).toDouble()
            val mod = 1024.0
            var i = 0
            while (nSize >= mod) {
                nSize /= mod
                i++
            }
            val df = DecimalFormat("#.##")
            return String.format("%s %s", df.format(nSize), units[i])
        }

    }
}