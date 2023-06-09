package com.coderstory.flyme10.tools

import android.util.Log
import java.io.*
import java.util.*

object RuntimeUtil {
    /**
     * 通过执行命令的方式判断手机是否root, 会有申请root权限的对话框出现
     */
    fun hasRooted(): Boolean {
        return execSilent("echo test")
    }


    /**
     * 判断是否成功执行
     */
    fun execSilent(cmd: String): Boolean {
        var result = false
        var writer: BufferedWriter? = null
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su")
            writer = BufferedWriter(OutputStreamWriter(process.outputStream))
            runCmd(writer, cmd)
            process.waitFor()
            Log.d("runtime", "onCreate: process.exitValue()  " + process.exitValue())
            result = process.exitValue() == 0
        } catch (e: Exception) {
            // e.printStackTrace();
        } finally {
            closeCloseable(writer)
            process?.destroy()
        }
        return result
    }

    // 关闭流文件
    private fun closeCloseable(vararg closeable: Closeable?) {
        for (i in 0 until closeable.size) {
            if (null != closeable[i]) {
                try {
                    closeable[i]!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 执行命令
    @Throws(IOException::class)
    private fun runCmd(writer: BufferedWriter, vararg cmd: String) {
        for (element in cmd) {
            writer.write(
                """
    $element
    
    """.trimIndent()
            )
            writer.flush()
        }
        writer.write("exit \n")
        writer.flush()
    }
}