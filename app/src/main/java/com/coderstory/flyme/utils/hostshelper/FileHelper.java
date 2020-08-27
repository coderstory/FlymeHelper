package com.coderstory.flyme.utils.hostshelper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static void UnZipAssetsFolder(Context context, String zipFileString, String outPathString) {
        try {
            ZipInputStream inZip = new ZipInputStream(context.getAssets().open(zipFileString));
            while (true) {
                ZipEntry zipEntry = inZip.getNextEntry();
                if (zipEntry != null) {
                    String szName = zipEntry.getName();
                    if (zipEntry.isDirectory()) {
                        new File(outPathString + File.separator + szName.substring(0, szName.length() - 1)).mkdirs();
                    } else {
                        Log.e(TAG, outPathString + File.separator + szName);
                        File file = new File(outPathString + File.separator + szName);
                        if (!file.exists()) {
                            Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int len = inZip.read(buffer);
                            if (len == -1) {
                                break;
                            }
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                        out.close();
                    }
                } else {
                    inZip.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "框架资源释放失败", 1).show();
        }
    }


    public static void saveAssets(Context context, String zipFileString, String outPathString) {
        try {
            InputStream stream = context.getAssets().open(zipFileString);
            FileOutputStream fos = new FileOutputStream(outPathString + "/" + zipFileString);
            byte[] b = new byte[1024];
            while ((stream.read(b)) != -1) {
                fos.write(b);// 写入数据
            }
            stream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
