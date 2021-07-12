package com.coderstory.flyme.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.pm.PackageInfo
import android.os.AsyncTask
import android.os.Build
import android.os.Looper
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
import com.coderstory.flyme.view.PullToRefreshView
import com.topjohnwu.superuser.Shell
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.Layer
import java.util.*

class HideAppFragment : BaseFragment() {
    private val appInfoList: MutableList<AppInfo?> = ArrayList()
    private val appInfoList2: MutableList<AppInfo?> = ArrayList()
    private var packages: List<PackageInfo> = ArrayList()
    private var adapter: AppInfoAdapter? = null
    private var appInfo: AppInfo? = null
    private var mPosition = 0
    private var mView: View? = null
    private var mPullToRefreshView: PullToRefreshView? = null
    private var hideAppList: MutableList<String>? = null
    private var dialog: Dialog? = null
    private fun initData() {
        val list = prefs.getString("Hide_App_List", "")
        hideAppList = ArrayList()
        hideAppList.addAll(Arrays.asList(*list!!.split(":").toTypedArray()))
        packages = ArrayList()
        if (context != null) {
            packages = context!!.packageManager.getInstalledPackages(0)
            initFruit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_hideapp_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initFruit() {
        appInfoList.clear()
        appInfoList2.clear()
        for (i in packages.indices) {
            val packageInfo = packages[i]
            if (context != null) {
                val intent = context!!.packageManager.getLaunchIntentForPackage(packageInfo.packageName)
                // 过来掉没启动器图标的app
                if (intent != null && "com.coderstory.flyme" != packageInfo.packageName) {
                    if (!hideAppList!!.contains(packageInfo.applicationInfo.packageName)) {
                        val appInfo = AppInfo(packageInfo.applicationInfo.loadLabel(context!!.packageManager).toString(), packageInfo.applicationInfo.loadIcon(context!!.packageManager), packageInfo.packageName, false, packageInfo.versionName.toString())
                        appInfoList.add(appInfo)
                    } else {
                        val appInfo = AppInfo(packageInfo.applicationInfo.loadLabel(context!!.packageManager).toString(), packageInfo.applicationInfo.loadIcon(context!!.packageManager), packageInfo.packageName, true, packageInfo.versionName.toString())
                        appInfoList2.add(appInfo)
                    }
                }
            }
        }
        appInfoList.addAll(appInfoList2)
    }

    private fun showData() {
        adapter = AppInfoAdapter(context, R.layout.app_info_item, appInfoList)
        val listView = contentView.findViewById<ListView>(R.id.listView)!!
        listView.adapter = adapter
        listView.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            mPosition = position
            mView = view
            appInfo = appInfoList[mPosition]
            val anyLayer = AnyLayer.dialog(context)
                    .contentView(R.layout.dialog_tdisable_app)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick({ AnyLayer: Layer, v: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
                    .onClick({ AnyLayer: Layer, v: View? ->
                        if (appInfo!!.disable) {
                            // 解除隐藏
                            var tmp = ""
                            for (s in hideAppList!!) {
                                if (s == appInfo!!.packageName) {
                                    tmp = s
                                }
                            }
                            hideAppList!!.remove(tmp)
                        } else {
                            // 隐藏
                            hideAppList!!.add(appInfo!!.packageName)
                        }
                        var value = StringBuilder()
                        for (s in hideAppList!!) {
                            value.append(s).append(":")
                        }
                        value = StringBuilder(value.substring(0, value.length - 1))
                        editor.putString("Hide_App_List", value.toString())
                        fix()
                        if (appInfo!!.disable) {
                            appInfo!!.disable = false
                            appInfoList[mPosition] = appInfo
                            mView!!.setBackgroundColor(resources.getColor(R.color.colorPrimary, null)) //正常的颜色
                        } else {
                            appInfo!!.disable = true
                            appInfoList[mPosition] = appInfo
                            mView!!.setBackgroundColor(resources.getColor(R.color.disableApp, null)) //冻结的颜色
                        }
                        AnyLayer.dismiss()
                    }, R.id.fl_dialog_yes)
            anyLayer.show()
            val cardView = (anyLayer as DialogLayer).contentView as CardView
            val linearLayout = cardView.getChildAt(0) as LinearLayout
            val textView = linearLayout.getChildAt(1) as TextView
            if (appInfo!!.disable) {
                textView.text = getString(R.string.sureAntiDisable) + appInfo!!.name + "的隐藏状态吗"
            } else {
                textView.text = "你确定要隐藏" + appInfo!!.name + getString(R.string.sureDisableAfter)
            }
        }
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_app_list
    }

    override fun init() {
        super.init()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Toast.makeText(activity, "本功能在Android 10及以上系统上暂时无效", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(activity, "点击应用切换 隐藏/显示 状态 【重启桌面生效】", Toast.LENGTH_LONG).show()
        }
        MyTask().execute()
        mPullToRefreshView = contentView.findViewById(R.id.pull_to_refresh)
        mPullToRefreshView!!.setOnRefreshListener {
            mPullToRefreshView!!.postDelayed({
                initData()
                showData()
                adapter!!.notifyDataSetChanged()
                mPullToRefreshView!!.setRefreshing(false)
            }, 2000)
        }
    }

    protected fun showProgress() {
        if (dialog == null) {
            dialog = ProgressDialog.show(context, getString(R.string.Tips_Title), getString(R.string.loadappinfo))
            dialog.show()
        }
    }

    //
    protected fun closeProgress() {
        if (dialog != null) {
            dialog!!.cancel()
            dialog = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_restrathome) {
            val anyLayer = AnyLayer.dialog(context)
                    .contentView(R.layout.dialog_tdisable_app)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick({ AnyLayer: Layer, v: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
                    .onClick({ AnyLayer: Layer?, v: View? ->
                        Shell.su("killall com.android.systemui").exec()
                        Shell.su("am force-stop com.meizu.flyme.launcher").exec()
                        System.exit(0)
                    }, R.id.fl_dialog_yes)
            anyLayer.show()
            val cardView = (anyLayer as DialogLayer).contentView as CardView
            val linearLayout = cardView.getChildAt(0) as LinearLayout
            val textView = linearLayout.getChildAt(1) as TextView
            textView.text = "是否重启Flyme桌面应用当前设置?"
        }
        return false
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class MyTask : AsyncTask<String?, Int?, String?>() {
        override fun onPreExecute() {
            showProgress()
        }

        override fun onPostExecute(param: String?) {
            showData()
            adapter!!.notifyDataSetChanged()
            closeProgress()
        }

        protected override fun doInBackground(vararg params: String): String? {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            initData()
            return null
        }
    }
}