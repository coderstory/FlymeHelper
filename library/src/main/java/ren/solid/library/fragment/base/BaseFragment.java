package ren.solid.library.fragment.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by _SOLID
 * Date:2016/3/30
 * Time:11:30
 */
public abstract class BaseFragment extends Fragment {

    private View mContentView;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;


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

    protected SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = prefs.edit();
        }
        return editor;

    }

    protected SharedPreferences getPrefs() {

//        String  a="3149a6fe037f6ccd515fd229df6bebc4";

//        if (prefs == null) {
//         String ab=   getSign(getMContext());
//            if (a.equals(ab)) {
                prefs = getContext().getSharedPreferences("UserSettings", Context.MODE_WORLD_READABLE);
//            }else {
//                prefs = getContext().getSharedPreferences("UserSettings1", Context.MODE_WORLD_READABLE);
//            }
//        }
        return prefs;
    }






        protected void init() {

    }

    protected void setUpView() {
    }

    protected <T extends View> T $(int id) {
        return (T) mContentView.findViewById(id);
    }

    // protected abstract View setContentView(LayoutInflater inflater, ViewGroup container);

    protected View getContentView() {
        return mContentView;
    }

    public Context getMContext() {
        return mContext;
    }

    protected ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }
}
