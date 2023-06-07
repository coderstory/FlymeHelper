package com.coderstory.flyme.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.cardview.widget.CardView
import com.coderstory.flyme.R
import com.coderstory.flyme.adapter.AppInfo
import com.coderstory.flyme.adapter.AppInfoAdapter
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.FileUtils
import com.coderstory.flyme.tools.Misc
import com.coderstory.flyme.tools.SnackBarUtils
import com.coderstory.flyme.view.PullToRefreshView
import com.topjohnwu.superuser.Shell
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.Layer
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DisbaleAppFragment : BaseFragment() {
    private val appInfoList: MutableList<AppInfo?> = ArrayList()
    private val appInfoList2: MutableList<AppInfo?> = ArrayList()
    private var packages: List<PackageInfo> = ArrayList()
    var adapter: AppInfoAdapter? = null
    private var listView: ListView? = null
    private var appInfo: AppInfo? = null
    private var mposition = 0
    private var mview: View? = null
    var mPullToRefreshView: PullToRefreshView? = null
    private var dialog: Dialog? = null

    @SuppressLint("HandlerLeak")
    var myHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            (dialog as ProgressDialog?)!!.setMessage(getString(R.string.refreshing_list))
            initData()
            adapter!!.notifyDataSetChanged()
            dialog?.cancel()
            super.handleMessage(msg)
        }
    }

    private fun initData() {
        packages = ArrayList()
        if (context != null) {
            packages = requireContext().packageManager.getInstalledPackages(0)
            initFruit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_disableapp_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initFruit() {
        appInfoList.clear()
        appInfoList2.clear()
        if (context != null) {
            for (i in packages.indices) {
                val packageInfo = packages[i]
                if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0) {
                    if (packageInfo.applicationInfo.enabled) {
                        val appInfo = AppInfo(
                            packageInfo.applicationInfo.loadLabel(requireContext().packageManager)
                                .toString(),
                            packageInfo.applicationInfo.loadIcon(requireContext().packageManager),
                            packageInfo.packageName,
                            false,
                            packageInfo.versionName.toString()
                        )
                        appInfoList.add(appInfo)
                    } else {
                        val appInfo = AppInfo(
                            packageInfo.applicationInfo.loadLabel(requireContext().packageManager)
                                .toString(),
                            packageInfo.applicationInfo.loadIcon(requireContext().packageManager),
                            packageInfo.packageName,
                            true,
                            packageInfo.versionName.toString()
                        )
                        appInfoList2.add(appInfo)
                    }
                }
            }
            appInfoList.addAll(appInfoList2)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showData() {
        adapter = AppInfoAdapter(context, R.layout.app_info_item, appInfoList)
        listView = contentView?.findViewById(R.id.listView)
        assert(listView != null)
        listView!!.adapter = adapter
        listView!!.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                mposition = position
                mview = view
                val anyLayer = AnyLayer.dialog(context)
                    .contentView(R.layout.dialog_disable_app)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick({ AnyLayer: Layer, v: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
                    .onClick({ AnyLayer: Layer, v: View? ->
                        val commandText =
                            (if (!appInfo!!.disable) "pm disable " else "pm enable ") + appInfo!!.packageName
                        Log.e("cc", commandText)
                        var process: Process? = null
                        var os: DataOutputStream? = null
                        try {
                            process = Runtime.getRuntime().exec("su") //切换到root帐号
                            os = DataOutputStream(process.outputStream)
                            os.writeBytes(
                                """
    $commandText
    
    """.trimIndent()
                            )
                            os.writeBytes("exit\n")
                            os.flush()
                            process.waitFor()
                            if (appInfo!!.disable) {
                                appInfo!!.disable = false
                                appInfoList[mposition] = appInfo
                                mview!!.setBackgroundColor(resources.getColor(R.color.colorPrimary)) //正常的颜色
                            } else {
                                appInfo!!.disable = true
                                appInfoList[mposition] = appInfo
                                mview!!.setBackgroundColor(Color.parseColor("#d0d7d7d7")) //冻结的颜色
                            }
                        } catch (ignored: Exception) {
                        } finally {
                            try {
                                os?.close()
                                assert(process != null)
                                process!!.destroy()
                            } catch (ignored: Exception) {
                            }
                        }
                        AnyLayer.dismiss()
                    }, R.id.fl_dialog_yes)
                anyLayer.show()
                val cardView = (anyLayer as DialogLayer).contentView as CardView
                val linearLayout = cardView.getChildAt(0) as LinearLayout
                val textView = linearLayout.getChildAt(1) as TextView
                appInfo = appInfoList[mposition]
                if (appInfo!!.disable) {
                    textView.text =
                        getString(R.string.sureAntiDisable) + appInfo!!.name + getString(R.string.sureAntiDisableAfter)
                } else {
                    textView.text =
                        getString(R.string.sureDisable) + appInfo!!.name + getString(R.string.sureDisableAfter)
                }
            }
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_app_list
    }

    override fun setUpView() {
        Toast.makeText(activity, R.string.disableapptips, Toast.LENGTH_LONG).show()
        mPullToRefreshView = contentView?.findViewById(R.id.pull_to_refresh)
        mPullToRefreshView!!.setOnRefreshListener(object : PullToRefreshView.OnRefreshListener {
            override fun onRefresh() {
                mPullToRefreshView!!.postDelayed({
                    initData()
                    showData()
                    adapter!!.notifyDataSetChanged()
                    mPullToRefreshView!!.setRefreshing(false)
                }, 2000)
            }

        })

        dialog = ProgressDialog.show(
            mContext,
            getString(R.string.Tips_Title),
            getString(R.string.loadappinfo)
        )
        Thread {
            initData()
            (mContext as Activity).runOnUiThread {
                showData()
                adapter!!.notifyDataSetChanged()
                dialog?.dismiss()
            }
        }.start()

    }

    override fun onDestroyView() {
        dialog?.dismiss()
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_backupList) {
            val anyLayer = AnyLayer.dialog(context)
                .contentView(R.layout.dialog_disable_app)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick({ AnyLayer: Layer, _: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
                .onClick({ AnyLayer: Layer, _: View? ->
                    satrtBackuop()
                    AnyLayer.dismiss()
                }, R.id.fl_dialog_yes)
            anyLayer.show()
            val cardView = (anyLayer as DialogLayer).contentView as CardView
            val linearLayout = cardView.getChildAt(0) as LinearLayout
            val textView = linearLayout.getChildAt(1) as TextView
            textView.text = getString(R.string.tips_sure_backuplist)
        } else if (item.itemId == R.id.action_restoreList) {
            val anyLayer = AnyLayer.dialog(context)
                .contentView(R.layout.dialog_disable_app)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick({ AnyLayer: Layer, _: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
                .onClick({ AnyLayer: Layer, _: View? ->
                    restoreList()
                    AnyLayer.dismiss()
                }, R.id.fl_dialog_yes)
            anyLayer.show()
            val cardView = (anyLayer as DialogLayer).contentView as CardView
            val linearLayout = cardView.getChildAt(0) as LinearLayout
            val textView = linearLayout.getChildAt(1) as TextView
            textView.text = getString(R.string.restore_set)
        }
        return false
    }

    private fun restoreList() {
        val dir = File(Misc.BackPath)
        val fileName = "userList"
        var content: String? = ""
        if (!dir.exists()) {
            SnackBarUtils.makeShort(
                `$`<View>(R.id.listView),
                getString(R.string.not_fond_backup_list_file)
            ).danger()
            return
        }
        try {
            content = FileUtils.readFile(Misc.BackPath + fileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (content!!.isEmpty()) {
            SnackBarUtils.makeShort(
                `$`<View>(R.id.listView),
                getString(R.string.not_fond_backup_list)
            ).danger()
            return
        }
        val list = content.split("\n").toTypedArray()
        dialog =
            ProgressDialog.show(context, getString(R.string.tips), getString(R.string.restoreing))
        dialog!!.show()
        Thread {
            Shell.su(*list)
            myHandler.sendMessage(Message())
        }.start()
    }

    private fun satrtBackuop() {
        val SB = StringBuilder("#已备份的系统APP冻结列表#\n")

        //遍历数据源
        for (info in appInfoList) {
            if (info!!.disable) { //判断是否被冻结
                SB.append(info.packageName).append("\n")
            }
        }
        val dir = File(Misc.BackPath)
        val fileName = "userList"
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                SnackBarUtils.makeShort(
                    `$`<View>(R.id.listView),
                    getString(R.string.tips_backup_error)
                ).show()
                return
            }
        }
        val fos: FileOutputStream
        var result = ""
        try {
            fos = FileOutputStream(Misc.BackPath + fileName)
            fos.write(SB.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            result = e.message ?: ""
        }
        if (result == "") {
            SnackBarUtils.makeShort(
                `$`<View>(R.id.listView),
                getString(R.string.tips_backup_success)
            ).show()
        } else {
            SnackBarUtils.makeShort(
                `$`<View>(R.id.listView),
                getString(R.string.tips_backup_error) + result
            ).show()
        }
    }
}