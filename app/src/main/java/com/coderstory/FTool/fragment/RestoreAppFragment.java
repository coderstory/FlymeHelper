package com.coderstory.FTool.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.Adapter.Application.AppInfo;
import com.coderstory.FTool.utils.Adapter.Application.AppInfoAdapter;
import com.coderstory.FTool.utils.DirManager;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import ren.solid.library.fragment.base.BaseFragment;


public class RestoreAppFragment extends BaseFragment {

    final String path_backup = Environment.getExternalStorageDirectory().getPath() + "/FTool/backupAPP/";
    List<PackageInfo> packages = new ArrayList<>();
    AppInfoAdapter adapter = null;
    ListView listView = null;
    AppInfo appInfo = null;
    int mPosition = 0;
    View mView = null;
    PullToRefreshView mPullToRefreshView;
    private View view;
    private List<AppInfo> appInfoList = new ArrayList<>();
    private Context context;
    private Dialog dialog;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_restoreapp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_restoreapp, container, false);
        context = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        new MyTask().execute();
        mPullToRefreshView = getActivity().findViewById(R.id.pull_to_refresh1);
        mPullToRefreshView.setOnRefreshListener(() -> mPullToRefreshView.postDelayed(() -> {
            initData();
            showData();
            adapter.notifyDataSetChanged();
            mPullToRefreshView.setRefreshing(false);
        }, 2000));
    }

    private void initData() {
        appInfoList = new ArrayList<>();
        PackageManager pm = getActivity().getPackageManager();
        DirManager.apkAll = DirManager.GetApkFileName(path_backup);
        packages = new ArrayList<>();

        for (String item : DirManager.apkAll
                ) {
            PackageInfo packageInfo = DirManager.loadAppInfo(item, getActivity());
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                //必须设置apk的路径 否则无法读取app的图标和名称
                appInfo.sourceDir = path_backup + item;
                appInfo.publicSourceDir = path_backup + item;
                AppInfo appInfos = new AppInfo(pm.getApplicationLabel(appInfo).toString(), pm.getApplicationIcon(appInfo), packageInfo.packageName, false, packageInfo.applicationInfo.sourceDir, packageInfo.versionName, packageInfo.versionCode);
                appInfoList.add(appInfos);
            }
        }
    }

    private void showData() {
        adapter = new AppInfoAdapter(getActivity(), R.layout.app_info_item, R.color.disableApp, appInfoList);
        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            mPosition = position;
            mView = view;
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.Tips_Title);
            String tipsText;
            String BtnText = getString(R.string.Btn_Sure);
            appInfo = appInfoList.get(mPosition);
            tipsText = "你确定要安装" + appInfo.getName() + "吗？";
            dialog.setMessage(tipsText);
            dialog.setPositiveButton(BtnText, (dialog1, which) -> {
                String commandText = "pm install  " + path_backup + appInfo.getPackageName() + ".apk";
                Log.e("cc", commandText);
                Process process = null;
                DataOutputStream os = null;
                try {
                    process = Runtime.getRuntime().exec("su"); //切换到root帐号
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes(commandText + "&\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    process.waitFor();
                } catch (Exception ignored) {

                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                        assert process != null;
                        process.destroy();
                    } catch (Exception ignored) {
                    }
                }
                closeProgress();
                Toast.makeText(context, "正在后台安装！", Toast.LENGTH_SHORT).show();
            });
            dialog.setCancelable(true);
            dialog.setNegativeButton(R.string.Btn_Cancel, (dialog12, which) -> dialog12.cancel());
            dialog.show();
        });

    }

    protected void showProgress() {
        if (dialog == null) {
            dialog = ProgressDialog.show(getActivity(), getString(R.string.Tips_Title), getString(R.string.loadappinfo));
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

    public class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            ;
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
