package com.coderstory.flyme.tools

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 收集手机全局崩溃时的exception,并log到本地
 *
 *
 * https://github.com/amimo/dcc
 *
 * @author Jackland_zgl
 */
class CrashHandler
/**
 * 保证只有一个CrashHandler实例
 */
private constructor() : Thread.UncaughtExceptionHandler {
    //用来存储设备信息和异常信息
    private val infos: MutableMap<String, String> = HashMap()

    //用于格式化日期,作为日志文件名的一部分
    private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINESE)
    private val newfileFinder = Comparator<File> { x, y -> // TODO Auto-generated method stub
        if (x.lastModified() > y.lastModified()) return@Comparator 1
        if (x.lastModified() < y.lastModified()) -1 else 0
    }

    //系统默认的UncaughtException处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    //程序的Context对象
    private var mContext: Context? = null

    /**
     * 初始化
     *
     * @param context context
     */
    fun init(context: Context?) {
        mContext = context
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                Log.e(TAG, "error : ", e)
            }
        }
        //退出程序
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0)
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private fun collectDeviceInfo(ctx: Context?) {
        try {
            val pm = ctx!!.packageManager
            val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                infos["versionName"] = versionName
                infos["versionCode"] = versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "an error occured when collect package info", e)
        }
        val fields = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                infos[field.name] = field[null].toString()
            } catch (e: Exception) {
                Log.e(TAG, "an error occured when collect crash info", e)
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex Throwable
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(ex: Throwable): Int {

        //将设备信息变成string
        val sb = StringBuilder()
        for ((key, value) in infos) {
            sb.append(key).append("=").append(value).append("\n")
        }

        //递归获取全部的exception信息
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result) //将写入的结果

        //构造文件名
        val timestamp = System.currentTimeMillis()
        val time = formatter.format(Date())
        val fileName = "crash-$time-$timestamp.log"
        val dir = File(Misc.CrashFilePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(Misc.CrashFilePath + fileName)
            fos.write(sb.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        cleanLogFileToN(Misc.CrashFilePath)
        Log.d(TAG, "saveCrashInfo2File: $sb")
        return 1
    }

    private fun cleanLogFileToN(dirname: String): Int {
        val dir = File(dirname)
        if (dir.isDirectory) {
            val logFiles = dir.listFiles()
            if (logFiles.size > LogFileLimit) {
                Arrays.sort(logFiles, newfileFinder) //从小到大排
                //删掉N个以前的
                for (i in 0 until logFiles.size - LogFileLimit) {
                    logFiles[i].delete()
                }
            }
        }
        return 1
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        //使用Toast来显示异常信息
        Thread {
            Looper.prepare()
            Toast.makeText(mContext, "很抱歉,程序出现异常", Toast.LENGTH_LONG).show()
            Looper.loop()
        }.start()

        //收集设备参数信息
        collectDeviceInfo(mContext)
        //保存日志文件
        saveCrashInfo2File(ex)
        return true
    }

    companion object {
        const val LogFileLimit = 10
        const val TAG = "CrashHandler"

        /**
         * 获取CrashHandler实例 ,单例模式
         */
        //CrashHandler实例
        @SuppressLint("StaticFieldLeak")
        val instance = CrashHandler()
    }
}