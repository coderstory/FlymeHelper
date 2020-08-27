package com.coderstory.flyme.utils.hostshelper;


import android.content.Context;

import com.topjohnwu.superuser.Shell;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.coderstory.flyme.utils.Misc.HostFileTmpName;


/**
 * 和hosts相关的操作
 * Created by cc on 2016/6/7.
 */
public class HostsHelper {
    private final String mcontent;
    private Context mcontext = null;

    public HostsHelper(String mcontent, Context m) {
        this.mcontent = mcontent;
        this.mcontext = m;
    }

    /**
     * 构造需要root下执行的命令组
     *
     * @return 构造好的命令组
     */
    protected ArrayList<String> getCommandsToExecute() throws UnsupportedEncodingException {
        ArrayList<String> list = new ArrayList<>();
        list.add("mount -o rw,remount /system");

        String path = mcontext.getFilesDir().getPath() + HostFileTmpName;

        FileOutputStream out = null;
        BufferedWriter writer;
        try {
            out = mcontext.openFileOutput("hosts", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (out != null) {
            writer = new BufferedWriter(new OutputStreamWriter(out));
            try {
                writer.write(mcontent);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        list.add(String.format("mv %s %s", path, "/etc/hosts"));
        list.add(String.format("chmod 755 %s", "/etc/hosts"));
        return list;
    }

    /**
     * 执行所提交的命令组
     */
    public final void execute() throws UnsupportedEncodingException {
        ArrayList<String> commands = getCommandsToExecute();
        if (null != commands && commands.size() > 0) {
            for (String command : commands) {
                Shell.su(command).exec();
            }
        }

    }

}
