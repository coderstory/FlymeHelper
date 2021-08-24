package com.coderstory.flyme.fragment

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.Misc
import com.coderstory.flyme.tools.SnackBarUtils
import com.coderstory.flyme.tools.hostshelper.FileHelper
import com.topjohnwu.superuser.Shell

class CleanFragment : BaseFragment() {
    @SuppressLint("HandlerLeak")
    private val hComplete: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            (`$`<View>(R.id.button) as Button).setText(R.string.starting_clean)
            `$`<View>(R.id.button).isEnabled = true
            SnackBarUtils.makeShort(`$`<View>(R.id.button), getString(R.string.clean_success)).info()
        }
    }
    var th: Thread? = null
    private var tvClean: TextView? = null

    @SuppressLint("HandlerLeak")
    private val hInfo: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            tvClean!!.append(msg.obj as String)
        }
    }

    override fun setUpView() {
        super.setUpView()
        `$`<View>(R.id.button).setOnClickListener { view: View? -> threadClean() }
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_clean
    }

    private fun sendMessageStr(str: String) {
        val msg = Message()
        msg.obj = str
        hInfo.sendMessage(msg)
    }

    private fun threadClean() {
        tvClean = `$`(R.id.tvClean)
        tvClean!!.movementMethod = ScrollingMovementMethod.getInstance()
        (`$`<View>(R.id.button) as Button).setText(R.string.cleaning)
        tvClean!!.append(getString(R.string.view_start_clean))
        `$`<View>(R.id.button).isEnabled = false
        th = Thread {
            var totalSize = 0L // K
            Misc.isProcessing = true
            val ret = Shell.su("find /data/data/ -type d -name \"cache\"").exec().out
            var cs: CacheSize
            for (s in ret) {
                cs = getSize(s)
                if (cs.size > 16) { // clean only above 16K
                    deleteCache(s)
                    sendMessageStr(getString(R.string.view_clean_cache, s, cs.sizeReadable))
                    totalSize += cs.size
                }
            }
            // clean anr log
            val anrSize = getSize("/data/anr/")
            deleteAnrLog()
            sendMessageStr(getString(R.string.view_clean_anr, anrSize.sizeReadable))
            totalSize += anrSize.size
            sendMessageStr(getString(R.string.view_clean_complete, FileHelper.getReadableFileSize(totalSize)))
            hComplete.sendEmptyMessage(0)
            Misc.isProcessing = false
        }
        th!!.start()
    }

    private fun getSize(path: String): CacheSize {
        return try {
            val result = Shell.su(String.format("du -s -k \"%s\"", path)).exec().out[0]
            val sizeStr = result.substring(0, result.indexOf('\t')).trim { it <= ' ' }
            val size: Long = sizeStr.toLong()
            CacheSize(sizeStr + "K", size)
        } catch (e: Exception) {
            CacheSize(0.toString() + "K", 0)
        }
    }

    private fun deleteCache(path: String) {
        Shell.su(String.format("rm -r \"%s\"", path)).exec()
    }

    private fun deleteAnrLog() {
        Shell.su("rm -r /data/anr/*").exec()
    }

    private inner class CacheSize(sr: String, s: Long) {
        var sizeReadable = ""
        var size = 0L

        init {
            sizeReadable = sr
            size = s
        }
    }
}