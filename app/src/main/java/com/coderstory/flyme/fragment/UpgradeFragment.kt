package com.coderstory.flyme.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.coderstory.flyme.R;
import com.coderstory.flyme.adapter.AppInfo;
import com.coderstory.flyme.adapter.AppInfoAdapter;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;

import static android.content.Context.CLIPBOARD_SERVICE;


public class UpgradeFragment extends BaseFragment {
    private final List<AppInfo> appInfos = new ArrayList<>();
    private List<PackageInfo> packages = new ArrayList<>();
    private AppInfoAdapter adapter = null;
    private PullToRefreshView mPullToRefreshView;
    private Dialog dialog;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_upgrade_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Layer anyLayer = AnyLayer.dialog(getMContext())
                .contentView(R.layout.dialog_tdisable_app)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick((AnyLayer, v) -> AnyLayer.dismiss(), R.id.fl_dialog_no)
                .onClick((AnyLayer, v) -> {
                    getEditor().putString("updateList", "");
                    fix();
                    initData();
                    adapter.notifyDataSetChanged();
                    AnyLayer.dismiss();
                }, R.id.fl_dialog_yes);
        anyLayer.show();
        CardView cardView = (CardView) ((DialogLayer) anyLayer).getContentView();
        LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
        TextView textView = (TextView) linearLayout.getChildAt(1);
        textView.setText("你确定要清空历史记录吗？");


        return false;
    }

    private void initData() {
        packages = new ArrayList<>();
        if (getContext() != null) {
            packages = getContext().getPackageManager().getInstalledPackages(0);
            initFruit();
        }
    }

    private void initFruit() {
        appInfos.clear();
        String str = getPrefs().getString("updateList", "");
        if ("".equals(str)) {
            Toast.makeText(getContext(), "未找到任何更新包记录，请打开系统更新检测到更新后再试", Toast.LENGTH_LONG).show();
        } else {
            try {
                for (String log : str.split(";")) {
                    String[] info = log.split("@");
                    appInfos.add(0, new AppInfo("     " + info[0], info[1], "  " + info[2], "  " + info[3]));
                }
            } catch (Exception e) {
                getEditor().putString("updateList", "");
                Toast.makeText(getContext(), "检测到数据异常，已重置", Toast.LENGTH_LONG).show();
                fix();
            }
        }
    }

    private void showData() {
        adapter = new AppInfoAdapter(getContext(), R.layout.app_upgrade_item, appInfos);
        ListView listView = getContentView().findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo appInfo = appInfos.get(position);

            Layer anyLayer = AnyLayer.dialog(getContext())
                    .contentView(R.layout.dialog_xposed_copyurl)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick((AnyLayer, v) -> {
                        ClipboardManager myClipboard;
                        myClipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                        ClipData myClip;
                        String text = appInfo.getVersion();
                        myClip = ClipData.newPlainText("text", text);
                        myClipboard.setPrimaryClip(myClip);
                        AnyLayer.dismiss();
                    }, R.id.tv_dialog_yes2);

            anyLayer.show();

        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_app_upgrade;
    }

    @Override
    protected void init() {
        super.init();
        // Toast.makeText(getActivity(), "系统更新检测到的更新包地址", Toast.LENGTH_LONG).show();

        new MyTask().execute();

        mPullToRefreshView = getContentView().findViewById(R.id.pull_to_refresh);

        mPullToRefreshView.setOnRefreshListener(() -> mPullToRefreshView.postDelayed(() -> {
            initData();
            showData();
            adapter.notifyDataSetChanged();
            mPullToRefreshView.setRefreshing(false);
        }, 2000));
    }

    protected void showProgress() {
        if (dialog == null) {
            dialog = ProgressDialog.show(getContext(), getString(R.string.Tips_Title), "正在读取。。。");
            dialog.show();
        }
    }

    //
    protected void closeProgress() {

        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            initData();
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

            showProgress();
        }

        @Override
        protected void onPostExecute(String param) {
            showData();

            adapter.notifyDataSetChanged();
            closeProgress();
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            initData();
            return null;
        }
    }
}

