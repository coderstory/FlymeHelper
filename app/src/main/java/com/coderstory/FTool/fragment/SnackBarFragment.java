package com.coderstory.FTool.fragment;

import com.coderstory.FTool.R;

import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.utils.SnackBarUtils;

/**
 * Created by _SOLID
 * Date:2016/5/9
 * Time:13:52
 */
public class SnackBarFragment extends BaseFragment {
    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_snackbar;
    }

    @Override
    protected void setUpView() {
        $(R.id.btn_default).setOnClickListener(v -> SnackBarUtils.makeShort(v, "TEXT").show());
        $(R.id.btn_danger).setOnClickListener(v -> SnackBarUtils.makeShort(v, "TEXT").danger());
        $(R.id.btn_confirm).setOnClickListener(v -> SnackBarUtils.makeShort(v, "TEXT").success());
        $(R.id.btn_info).setOnClickListener(v -> SnackBarUtils.makeShort(v, "TEXT").info("action", v1 -> SnackBarUtils.makeShort(v1, "TEXT").show()));
        $(R.id.btn_warning).setOnClickListener(v -> SnackBarUtils.makeShort(v, "TEXT").warning("action", v12 -> SnackBarUtils.makeShort(v12, "TEXT").show()));
    }
}
