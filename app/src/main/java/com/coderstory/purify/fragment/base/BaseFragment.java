package com.coderstory.purify.fragment.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coderstory.purify.utils.SharedHelper;


/**
 * Created by _SOLID
 * Date:2016/3/30
 * Time:11:30
 */
public abstract class BaseFragment extends Fragment {
    private View mContentView;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(setLayoutResourceID(), container, false);//setContentView(inflater, container);
        mContext = getContext();
        ProgressDialog mProgressDialog = new ProgressDialog(getMContext());
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

    protected SharedHelper getPrefs() {
        return new SharedHelper(getMContext());
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
