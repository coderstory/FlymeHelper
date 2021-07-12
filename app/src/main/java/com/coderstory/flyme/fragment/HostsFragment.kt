package com.coderstory.flyme.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.Misc
import com.coderstory.flyme.tools.hostshelper.FileHelper
import com.topjohnwu.superuser.Shell
import java.io.*

class HostsFragment : BaseFragment() {
    private var dialog: Dialog? = null
    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_hosts
    }

    override fun setUpView() {
        `$`<View>(R.id.enableHosts).setOnClickListener { v: View ->
            editor.putBoolean("enableHosts", (v as SwitchCompat).isChecked)
            editor.commit()
            sudoFixPermissions()
            setCheck(v.isChecked)
            MyTask().execute()
        }
        `$`<View>(R.id.enableMIUIHosts).setOnClickListener { v: View ->
            editor.putBoolean("enableMIUIHosts", (v as SwitchCompat).isChecked)
            editor.commit()
            sudoFixPermissions()
            MyTask().execute()
        }
        `$`<View>(R.id.enableBlockAdsHosts).setOnClickListener { v: View ->
            editor.putBoolean("enableBlockAdsHosts", (v as SwitchCompat).isChecked)
            editor.commit()
            sudoFixPermissions()
            MyTask().execute()
        }
        `$`<View>(R.id.enableGoogleHosts).setOnClickListener { v: View ->
            editor.putBoolean("enableGoogleHosts", (v as SwitchCompat).isChecked)
            editor.commit()
            sudoFixPermissions()
            MyTask().execute()
        }
        `$`<View>(R.id.enableStore).setOnClickListener { v: View ->
            editor.putBoolean("enableStore", (v as SwitchCompat).isChecked)
            editor.commit()
            sudoFixPermissions()
            MyTask().execute()
        }
        `$`<View>(R.id.enableupdater).setOnClickListener { v: View ->
            editor.putBoolean("enableUpdater", (v as SwitchCompat).isChecked)
            editor.commit()
            sudoFixPermissions()
            MyTask().execute()
        }
    }

    override fun setUpData() {
        (`$`<View>(R.id.enableHosts) as SwitchCompat).isChecked = prefs.getBoolean("enableHosts", false)
        (`$`<View>(R.id.enableMIUIHosts) as SwitchCompat).isChecked = prefs.getBoolean("enableMIUIHosts", false)
        (`$`<View>(R.id.enableBlockAdsHosts) as SwitchCompat).isChecked = prefs.getBoolean("enableBlockAdsHosts", false)
        (`$`<View>(R.id.enableGoogleHosts) as SwitchCompat).isChecked = prefs.getBoolean("enableGoogleHosts", false)
        (`$`<View>(R.id.enableStore) as SwitchCompat).isChecked = prefs.getBoolean("enableStore", false)
        (`$`<View>(R.id.enableupdater) as SwitchCompat).isChecked = prefs.getBoolean("enableUpdater", false)
        setCheck(prefs.getBoolean("enableHosts", false))
    }

    //因为hosts修改比较慢 所以改成异步的
    //更新hosts操作
    @Throws(UnsupportedEncodingException::class)
    private fun UpdateHosts() {
        val enableHostsSet = prefs.getBoolean("enableHosts", false) //1
        val enableMIUIHostsSet = prefs.getBoolean("enableMIUIHosts", false) //4
        val enableBlockAdsHostsSet = prefs.getBoolean("enableBlockAdsHosts", false) //4
        val enableGoogleHostsSet = prefs.getBoolean("enableGoogleHosts", false) //4
        val enableStoreSet = prefs.getBoolean("enableStore", false) //4
        val enableupdaterSet = prefs.getBoolean("enableUpdater", false) //4
        if (enableHostsSet) {
            val fh = FileHelper()
            var HostsContext = fh.getFromAssets("hosts_default", mContext)
            if (prefs.getBoolean("enableHosts", false)) { //如果未启用hosts

                // 国内广告
                if (enableBlockAdsHostsSet) {
                    HostsContext += fh.getFromAssets("hosts_noad", mContext)
                }
                //  国外一些网站 不含google
                if (enableGoogleHostsSet) {
                    HostsContext += fh.getFromAssets("hosts_foreign", mContext)
                }
                // 屏蔽在线更新
                if (enableupdaterSet) {
                    HostsContext += "\n127.0.0.1 update.miui.com\n"
                }
                // 屏蔽应用商店 游戏 等
                if (enableStoreSet) {
                    HostsContext += fh.getFromAssets("hosts_nostore", mContext)
                }
                // 屏蔽miui广告
                if (enableMIUIHostsSet) {
                    HostsContext += fh.getFromAssets("hosts_miui", mContext)
                }
            }
            Shell.su(*getCommandsToExecute(HostsContext)).exec()
        }
    }

    @Throws(UnsupportedEncodingException::class)
    protected fun getCommandsToExecute(context: String?): Array<String?> {
        val list = arrayOfNulls<String>(3)
        list[0] = "mount -o rw,remount /system"
        val path = mContext.filesDir.path + Misc.HostFileTmpName
        var out: FileOutputStream? = null
        val writer: BufferedWriter
        try {
            out = mContext.openFileOutput("hosts", Context.MODE_PRIVATE)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (out != null) {
            writer = BufferedWriter(OutputStreamWriter(out))
            try {
                writer.write(context)
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        list[1] = String.format("mv %s %s", path, "/etc/hosts")
        list[2] = String.format("chmod 755 %s", "/system/etc/hosts")
        return list
    }

    private fun showProgress() {
        if (dialog == null || !dialog!!.isShowing) { //dialog未实例化 或者实例化了但没显示
            dialog = ProgressDialog.show(activity, getString(R.string.Working), getString(R.string.Waiting))
            dialog.show()
        }
    }

    private fun closeProgress() {
        if (activity != null && !activity!!.isFinishing) {
            dialog!!.cancel()
        }
    }

    private fun setCheck(type: Boolean) {
        if (type) {
            `$`<View>(R.id.enableMIUIHosts).isEnabled = true
            `$`<View>(R.id.enableBlockAdsHosts).isEnabled = true
            `$`<View>(R.id.enableGoogleHosts).isEnabled = true
            `$`<View>(R.id.enableStore).isEnabled = true
            `$`<View>(R.id.enableupdater).isEnabled = true
        } else {
            `$`<View>(R.id.enableMIUIHosts).isEnabled = false
            `$`<View>(R.id.enableBlockAdsHosts).isEnabled = false
            `$`<View>(R.id.enableGoogleHosts).isEnabled = false
            `$`<View>(R.id.enableStore).isEnabled = false
            `$`<View>(R.id.enableupdater).isEnabled = false
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class MyTask : AsyncTask<String?, Int?, String?>() {
        override fun onPreExecute() {
            showProgress()
        }

        override fun onPostExecute(param: String?) {
            closeProgress()
        }

        protected override fun onProgressUpdate(vararg values: Int) {
            super.onProgressUpdate(*values)
        }

        protected override fun doInBackground(vararg params: String): String? {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            try {
                UpdateHosts()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }
    }
}