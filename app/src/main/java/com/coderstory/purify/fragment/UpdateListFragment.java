package com.coderstory.purify.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import com.coderstory.purify.R;
import com.coderstory.purify.adapter.AppInfo;
import com.coderstory.purify.adapter.AppInfoAdapter;
import com.coderstory.purify.fragment.base.BaseFragment;
import com.coderstory.purify.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.coderstory.purify.utils.ConfigPreferences.getInstance;


public class UpdateListFragment extends BaseFragment {
    private List<PackageInfo> packages = new ArrayList<>();
    private AppInfoAdapter adapter = null;
    private PullToRefreshView mPullToRefreshView;
    private List<AppInfo> appInfos = new ArrayList<>();
    private Dialog dialog;



    private void initData() {
        packages = new ArrayList<>();
        if (getContext() != null) {
            packages = getContext().getPackageManager().getInstalledPackages(0);
            initFruit();
        }
    }

    private void initFruit() {
        appInfos.clear();
        String str = getInstance().getString("updateList", "");
        if ("".equals(str)) {
            Toast.makeText(getContext(), "未找到任何更新包记录，请打开系统更新检测到更新后再试", Toast.LENGTH_LONG).show();
        } else {
            try {
                for (String log : str.split(";")) {
                    String[] info = log.split("@");
                    appInfos.add(new AppInfo("     " + info[0], info[1], "  " + info[2], "  " +info[3]));
                }
            } catch (Exception e) {
                getInstance().saveConfig("updateList", "");
                Toast.makeText(getContext(), "检测到数据异常，已重置", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showData() {
        adapter = new AppInfoAdapter(getContext(), R.layout.app_info_item, appInfos);
        ListView listView = getContentView().findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo appInfo = appInfos.get(position);
            Toast.makeText(getContext(), "已复制下载地址到剪贴板", Toast.LENGTH_LONG).show();
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip;
            String text = appInfo.getVersion();
            myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);

            DownloadManager dManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(text);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
            request.setDestinationInExternalPublicDir("/SystemPackage/", appInfo.getName() + "-" + appInfo.getFileSize() + ".zip".replace(" ", ""));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);
            long refernece = dManager.enqueue(request);

        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init() {
        super.init();
        Toast.makeText(getActivity(), "系统更新检测到的更新包地址", Toast.LENGTH_LONG).show();

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

