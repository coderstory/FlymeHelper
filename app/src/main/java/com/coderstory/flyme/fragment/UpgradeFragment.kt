package com.coderstory.flyme.fragment


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
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
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.Layer

class UpgradeFragment : BaseFragment() {
    private val appInfos: MutableList<AppInfo?> = ArrayList()
    private var packages: List<PackageInfo> = ArrayList()
    private var adapter: AppInfoAdapter? = null
    private var mPullToRefreshView: PullToRefreshView? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_upgrade_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val anyLayer = AnyLayer.dialog(mContext)
                .contentView(R.layout.dialog_disable_app)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick({ AnyLayer: Layer, _: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
                .onClick({ AnyLayer: Layer, _: View? ->
                    editor.putString("updateList", "")
                    fix()
                    initData()
                    adapter!!.notifyDataSetChanged()
                    AnyLayer.dismiss()
                }, R.id.fl_dialog_yes)
        anyLayer.show()
        val cardView = (anyLayer as DialogLayer).contentView as CardView
        val linearLayout = cardView.getChildAt(0) as LinearLayout
        val textView = linearLayout.getChildAt(1) as TextView
        textView.text = "你确定要清空历史记录吗？"
        return false
    }

    private fun initData() {
        packages = ArrayList()
        packages = mContext.packageManager.getInstalledPackages(0)
        initFruit()
    }

    private fun initFruit() {
        appInfos.clear()
        val str = prefs.getString("updateList", "")
        if ("" == str) {
            Toast.makeText(mContext, "未找到任何更新包记录，请打开系统更新检测到更新后再试", Toast.LENGTH_LONG).show()
        } else {
            try {
                for (log in str!!.split(";").toTypedArray()) {
                    if (log != "") {
                        val info = log.split("@").toTypedArray()
                        appInfos.add(0, AppInfo("     " + info[0], info[1], "  " + info[2], "  " + info[3]))
                    }
                }
            } catch (e: Exception) {
                editor.putString("updateList", "")
                Toast.makeText(mContext, "检测到数据异常，已重置", Toast.LENGTH_LONG).show()
                fix()
            }
        }
    }

    private fun showData() {
        adapter = AppInfoAdapter(mContext, R.layout.app_upgrade_item, appInfos)
        val listView = contentView!!.findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
        listView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val appInfo = appInfos[position]
            val anyLayer = AnyLayer.dialog(mContext)
                    .contentView(R.layout.dialog_xposed_copyurl)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick({ AnyLayer: Layer, _: View? ->
                        val myClipboard: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val myClip: ClipData
                        val text = appInfo!!.version
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip)
                        AnyLayer.dismiss()
                    }, R.id.tv_dialog_yes2)
            anyLayer.show()
        }
    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_app_upgrade
    }

    override fun setUpData() {
        super.init()
        // Toast.makeText(getActivity(), "系统更新检测到的更新包地址", Toast.LENGTH_LONG).show();
        mPullToRefreshView = contentView!!.findViewById(R.id.pull_to_refresh)
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
        initData()
        showData()
        adapter!!.notifyDataSetChanged()
    }


    override fun onResume() {
        super.onResume()
        if (adapter != null) {
            initData()
            adapter!!.notifyDataSetChanged()
        }
    }

}