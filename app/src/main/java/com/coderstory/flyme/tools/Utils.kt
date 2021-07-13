package com.coderstory.flyme.tools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.topjohnwu.superuser.Shell
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class Utils {
    fun getSerialNumber(mContext: Context?): String? {
        val result = Shell.su(decode("Z2V0cHJvcCUyMHJvLnNlcmlhbG5v").replace("%20", " ")).exec().out
        if (result.size == 0) {
            return null
        }
        if (result[0].contains("start command")) {
            val normalDialog = AlertDialog.Builder(mContext)
            normalDialog.setTitle("!!致命错误!!")
            normalDialog.setMessage("检测到您手机自带的ROOT已失效!")
            normalDialog.setPositiveButton("确定"
            ) { _: DialogInterface?, _: Int -> exitProcess(0) }
            normalDialog.setCancelable(true)
            normalDialog.show()
        }
        return result[0]
    }

    inner class Check : Runnable {
        private var mark: String
        private var sn: String?
        private var myHandler: Handler
        private var isLogin: Int
        var mContext: Context?

        constructor(helper: SharedHelper, myHandler: Handler, mContext: Context?) {
            mark = decodeStr(helper.getString(decode("bWFyaw=="), ""))
            sn = getSerialNumber(mContext)
            this.myHandler = myHandler
            isLogin = 0
            this.mContext = mContext
        }

        constructor(mark: String, myHandler: Handler, mContext: Context?) {
            this.mark = mark
            sn = getSerialNumber(mContext)
            this.myHandler = myHandler
            isLogin = 1
            this.mContext = mContext
        }

        override fun run() {
            val path = Misc.searchApi
            try {
                val url = URL(path)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.requestMethod = "POST"

                //数据准备
                val data = """{
    "${decode("UVE=")}": "$mark",
    "${decode("c24=")}": "$sn",
    "isLogin": $isLogin
}"""
                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Content-Length", data.length.toString() + "")

                //post的方式提交实际上是留的方式提交给服务器
                connection.doOutput = true
                val outputStream = connection.outputStream
                outputStream.write(data.toByteArray())

                //获得结果码
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    //请求成功
                    val `is` = connection.inputStream
                    val msg = Message()
                    msg.arg1 = 4
                    val data2 = Bundle()
                    data2.putString("value", dealResponseResult(`is`))
                    data2.putString(decode("bWFyaw=="), mark)
                    data2.putString("sn", sn)
                    msg.data = data2
                    myHandler.sendMessage(msg)
                } else {
                    val msg = Message()
                    msg.arg1 = 5
                    myHandler.sendMessage(msg)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                val msg = Message()
                msg.arg1 = 5
                myHandler.sendMessage(msg)
            }
        }

        private fun dealResponseResult(inputStream: InputStream): String {
            val resultData: String //存储处理结果
            val byteArrayOutputStream = ByteArrayOutputStream()
            val data = ByteArray(1024)
            var len: Int
            try {
                while (inputStream.read(data).also { len = it } != -1) {
                    byteArrayOutputStream.write(data, 0, len)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            resultData = String(byteArrayOutputStream.toByteArray())
            return resultData
        }
    }

    companion object {
        fun convertDpToPixel(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp.toFloat() * density).roundToInt()
        }

        private fun compareDay(day1: String, day2: String): Long {
            return try {
                val d1 = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(day1)
                val d2 = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(day2)
                (d2!!.time - d1!!.time) / 1000 / 60 / 60 / 24
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }

        fun vi(): Boolean {
            return vp() >= 0
        }

        private fun vp(): Long {
            return compareDay(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date()), Misc.endTime)
        }

        fun getMySharedPreferences(context: Context?, dir: String?, fileName: String?): SharedPreferences {
            var result: SharedPreferences
            try {
                result = context!!.getSharedPreferences(fileName, Context.MODE_WORLD_READABLE)
            } catch (e: SecurityException) {
                try {
                    // 获取 ContextWrapper对象中的mBase变量。该变量保存了 ContextImpl 对象
                    val field_mBase = ContextWrapper::class.java.getDeclaredField("mBase")
                    field_mBase.isAccessible = true
                    // 获取 mBase变量
                    val obj_mBase = field_mBase[context]
                    // 获取 ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
                    val field_mPreferencesDir = obj_mBase.javaClass.getDeclaredField("mPreferencesDir")
                    field_mPreferencesDir.isAccessible = true
                    // 创建自定义路径
                    //  String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android";
                    val file = File(dir)
                    // 修改mPreferencesDir变量的值
                    field_mPreferencesDir[obj_mBase] = file
                    // 返回修改路径以后的 SharedPreferences :%FILE_PATH%/%fileName%.xml
                    //Log.e(TAG, "getMySharedPreferences filep=" + file.getAbsolutePath() + "| fileName=" + fileName);
                    return context!!.getSharedPreferences(fileName, Activity.MODE_PRIVATE)
                } catch (f: NoSuchFieldException) {
                    f.printStackTrace()
                } catch (f: IllegalArgumentException) {
                    f.printStackTrace()
                } catch (f: IllegalAccessException) {
                    f.printStackTrace()
                }
                //Log.e(TAG, "getMySharedPreferences end filename=" + fileName);
                // 返回默认路径下的 SharedPreferences : /data/data/%package_name%/shared_prefs/%fileName%.xml
                result = context!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            }
            return result
        }

        fun check(helper: SharedHelper?): Boolean {
            return helper!!.getString(decode("bWFyaw=="), "") != ""
        }

        fun decode(base64: String?): String {
            return try {
                val clazz = Class.forName("android.util.Base64")
                val method = clazz.getDeclaredMethod("decode", String::class.java, Int::class.javaPrimitiveType)
                String((method.invoke(null, base64, 0) as ByteArray))
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                ""
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                ""
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                ""
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                ""
            }
        }

        fun encodeStr(data: String): String {
            //把字符串转为字节数组
            val b = data.toByteArray()
            //遍历
            for (i in b.indices) {
                b[i] = b[i].plus(1).toByte() //在原有的基础上+1
            }
            return String(b)
        }

        fun decodeStr(data: String): String {
            //把字符串转为字节数组
            val b = data.toByteArray()
            //遍历
            for (i in b.indices) {
                b[i] =  b[i].plus(-1).toByte()
            }
            return String(b)
        }
    }
}