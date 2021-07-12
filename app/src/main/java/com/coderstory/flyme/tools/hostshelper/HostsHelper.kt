package com.coderstory.flyme.tools.hostshelper

import android.content.Context
import com.coderstory.flyme.tools.Misc
import com.topjohnwu.superuser.Shell
import java.io.*
import java.util.*

/**
 * 和hosts相关的操作
 * Created by cc on 2016/6/7.
 */
class HostsHelper(private val mcontent: String, m: Context?) {
    private var mcontext: Context? = null

    /**
     * 构造需要root下执行的命令组
     *
     * @return 构造好的命令组
     */
    @get:Throws(UnsupportedEncodingException::class)
    protected val commandsToExecute: ArrayList<String>
        protected get() {
            val list = ArrayList<String>()
            list.add("mount -o rw,remount /system")
            val path = mcontext!!.filesDir.path + Misc.HostFileTmpName
            var out: FileOutputStream? = null
            val writer: BufferedWriter
            try {
                out = mcontext.openFileOutput("hosts", Context.MODE_PRIVATE)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            if (out != null) {
                writer = BufferedWriter(OutputStreamWriter(out))
                try {
                    writer.write(mcontent)
                    writer.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            list.add(String.format("mv %s %s", path, "/etc/hosts"))
            list.add(String.format("chmod 755 %s", "/etc/hosts"))
            return list
        }

    /**
     * 执行所提交的命令组
     */
    @Throws(UnsupportedEncodingException::class)
    fun execute() {
        val commands = commandsToExecute
        if (null != commands && commands.size > 0) {
            for (command in commands) {
                Shell.su(command).exec()
            }
        }
    }

    init {
        mcontext = m
    }
}