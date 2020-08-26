package com.coderstory.flyme.utils.hostshelper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileHelper {

    private static final String TAG = "FileHelper";

    public static String getReadableFileSize(long size) {
        String[] units = new String[]{"K", "M", "G", "T", "P"};
        double nSize = size * 1L * 1.0f;
        double mod = 1024.0f;
        int i = 0;
        while (nSize >= mod) {
            nSize /= mod;
            i++;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return String.format("%s %s", df.format(nSize), units[i]);
    }

    /**
     * 从Assets中读取文本
     *
     * @param FileName 文件名
     * @param mContext context
     * @return 读取到的文本
     */
    public String getFromAssets(String FileName, Context mContext) {
        try {
            InputStreamReader inputReader = new InputStreamReader(mContext.getAssets().open(FileName), StandardCharsets.UTF_8);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder Result = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                Result.append(line).append("\n");
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 解压assets目录下的zip到指定的路径
     *
     * @param zipFileString ZIP的名称，压缩包的名称：xxx.zip
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static void UnZipAssetsFolder(Context context, String zipFileString, String
            outPathString) {
        try {
            ZipInputStream inZip = new ZipInputStream(context.getAssets().open(zipFileString));
            ZipEntry zipEntry;
            String szName;
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    //获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    Log.e(TAG, outPathString + File.separator + szName);
                    File file = new File(outPathString + File.separator + szName);
                    if (!file.exists()) {
                        Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    // 获取文件的输出流
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // 读取（字节）字节到缓冲区
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }

            inZip.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "框架资源释放失败", Toast.LENGTH_LONG).show();
        }
    }

}
