package com.coderstory.flyme10.tools

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

/**
 * Created by _SOLID
 * Date:2016/4/20
 * Time:15:01
 */
object FileUtils {

    /**
     * 读取指定文件
     *
     * @param fileName SD下的文件路径+文件名，如:a/b.txt
     */
    fun readFile(fileName: String): String {
        val stringBuffer = StringBuilder()
        try {
            var line = ""
            val file = File(fileName)
            val bufferedReader = BufferedReader(FileReader(file))
            while (bufferedReader.readLine().also {
                    if (it != null) {
                        stringBuffer.append(line)
                    }
                } != null)
                bufferedReader.close()
        } catch (e: IOException) {
            Log.e("Xposed", Log.getStackTraceString(e))
        }
        return stringBuffer.toString()
    }
}