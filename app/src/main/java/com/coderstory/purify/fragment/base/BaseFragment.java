package com.coderstory.purify.fragment.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coderstory.purify.utils.ConfigPreferences;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.coderstory.purify.config.Misc.ApplicationName;
import static com.coderstory.purify.config.Misc.SharedPreferencesName;
import static com.coderstory.purify.utils.ConfigPreferences.getInstance;


/**
 * Created by _SOLID
 * Date:2016/3/30
 * Time:11:30
 */
public abstract class BaseFragment extends Fragment {

    public static final String PREFS_FOLDER = " /data/data/" + ApplicationName + "/shared_prefs\n";
    public static final String PREFS_FILE = " /data/data/" + ApplicationName + "/shared_prefs/" + SharedPreferencesName + ".xml\n";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private View mContentView;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(setLayoutResourceID(), container, false);//setContentView(inflater, container);
        mContext = getContext();
        mProgressDialog = new ProgressDialog(getMContext());
        mProgressDialog.setCanceledOnTouchOutside(false);
        setHasOptionsMenu(true);
        init();
        setUpView();
        setUpData();
        getPrefs();
        return mContentView;
    }

    protected abstract int setLayoutResourceID();

    protected void setUpData() {
    }

    protected ConfigPreferences getPrefs() {
        return getInstance();
    }

    protected void init() {
    }

    protected void setUpView() {
    }

    protected <T extends View> T $(int id) {
        return (T) mContentView.findViewById(id);
    }


    protected View getContentView() {
        return mContentView;
    }

    public Context getMContext() {
        return mContext;
    }

}
