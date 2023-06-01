package com.coderstory.flyme.tools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.SharedPreferences
import com.topjohnwu.superuser.Shell
import java.io.File
import java.lang.reflect.InvocationTargetException
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