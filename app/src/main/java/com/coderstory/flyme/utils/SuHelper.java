package com.coderstory.flyme.utils;

import com.topjohnwu.superuser.Shell;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public abstract class SuHelper {

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

    protected abstract ArrayList<String> getCommandsToExecute() throws UnsupportedEncodingException;
}