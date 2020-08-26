package com.coderstory.flyme.fragment;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.hostshelper.FileHelper;

import eu.chainfire.libsuperuser.Shell;


public class XposedFragment extends BaseFragment {

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_hosts;
    }

    @Override
    protected void setUpView() {
        String base = getMContext().getFilesDir().getAbsolutePath();
        FileHelper.UnZipAssetsFolder(getMContext(), "EDXP_4529_Y.zip", base);
        Shell.SU.run("mount -o rw,remount /system");
        Shell.SU.run("mv -f " + base + "/data /");
        Shell.SU.run("mv " + base + "/system /");
    }

    @Override
    protected void setUpData() {
    }

}

