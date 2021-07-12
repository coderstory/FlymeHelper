package com.coderstory.flyme.tools.hostshelper

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.*
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.util.zip.ZipInputStream

class FileHelper {
    /**
     * 从Assets中读取文本
     *
     * @param FileName 文件名
     * @param mContext context
     * @return 读取到的文本
     */
    fun getFromAssets(FileName: String?, mContext: Context?): String {
        return try {
            val inputReader = InputStreamReader(mContext!!.assets.open(FileName!!), StandardCharsets.UTF_8)
            val bufReader = BufferedReader(inputReader)
            var line: String?
            val Result = StringBuilder()
            while (bufReader.readLine().also { line = it } != null) Result.append(line).append("\n")
            Result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    companion object {
        private const val TAG = "FileHelper"
        fun getReadableFileSize(size: Long): String {
            val units = arrayOf("K", "M", "G", "T", "P")
            var nSize = (size * 1L * 1.0f).toDouble()
            val mod = 1024.0
            var i = 0
            while (nSize >= mod) {
                nSize /= mod
                i++
            }
            val df = DecimalFormat("#.##")
            return String.format("%s %s", df.format(nSize), units[i])
        }

        fun UnZipAssetsFolder(context: Context?, zipFileString: String?, outPathString: String) {
            try {
                val inZip = ZipInputStream(context!!.assets.open(zipFileString!!))
                while (true) {
                    val zipEntry = inZip.nextEntry
                    if (zipEntry != null) {
                        val szName = zipEntry.name
                        if (zipEntry.isDirectory) {
                            File(outPathString + File.separator + szName.substring(0, szName.length - 1)).mkdirs()
                        } else {
                            Log.e(TAG, outPathString + File.separator + szName)
                            val file = File(outPathString + File.separator + szName)
                            if (!file.exists()) {
                                Log.e(TAG, "Create the file:" + outPathString + File.separator + szName)
                                file.parentFile.mkdirs()
                                file.createNewFile()
                            }
                            val out = FileOutputStream(file)
                            val buffer = ByteArray(1024)
                            while (true) {
                                val len = inZip.read(buffer)
                                if (len == -1) {
                                    break
                                }
                                out.write(buffer, 0, len)
                                out.flush()
                            }
                            out.close()
                        }
                    } else {
                        inZip.close()
                        return
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "框架资源释放失败", 1).show()
            }
        }

        fun saveAssets(context: Context, zipFileString: String, outPathString: String) {
            try {
                val stream = context.assets.open(zipFileString)
                val fos = FileOutputStream("$outPathString/$zipFileString")
                val b = ByteArray(1024)
                while (stream.read(b) != -1) {
                    fos.write(b) // 写入数据
                }
                stream.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}