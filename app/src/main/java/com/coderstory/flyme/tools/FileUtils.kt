package com.coderstory.flyme.tools

import android.text.TextUtils
import android.util.Log
import java.io.*

/**
 * Created by _SOLID
 * Date:2016/4/20
 * Time:15:01
 */
object FileUtils {
    @Throws(Exception::class)
    fun readFile(_sFileName: String?, _sEncoding: String?): String {
        var _sEncoding = _sEncoding
        var buffContent: StringBuffer? = null
        var sLine: String?
        var fis: FileInputStream? = null
        var buffReader: BufferedReader? = null
        if (_sEncoding == null || "" == _sEncoding) {
            _sEncoding = "UTF-8"
        }
        return try {
            fis = FileInputStream(_sFileName)
            buffReader = BufferedReader(InputStreamReader(fis,
                    _sEncoding))
            var zFirstLine = "UTF-8".equals(_sEncoding, ignoreCase = true)
            while (buffReader.readLine().also { sLine = it } != null) {
                buffContent?.append("\n") ?: (buffContent = StringBuffer())
                if (zFirstLine) {
                    sLine = removeBomHeaderIfExists(sLine)
                    zFirstLine = false
                }
                buffContent.append(sLine)
            } // end while
            buffContent?.toString() ?: ""
        } catch (ex: FileNotFoundException) {
            throw Exception("要读取的文件没有找到!", ex)
        } catch (ex: IOException) {
            throw Exception("读取文件时错误!", ex)
        } finally {
            // 增加异常时资源的释放
            try {
                buffReader?.close()
                fis?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @Throws(Exception::class)
    fun writeFile(path: String, content: String, encoding: String?, isOverride: Boolean): File {
        var encoding = encoding
        if (TextUtils.isEmpty(encoding)) {
            encoding = "UTF-8"
        }
        val `is`: InputStream = ByteArrayInputStream(content.toByteArray(charset(encoding!!)))
        return writeFile(`is`, path, isOverride)
    }

    @Throws(Exception::class)
    fun writeFile(`is`: InputStream?, path: String, isOverride: Boolean): File {
        var path = path
        val sPath = extractFilePath(path)
        if (!pathExists(sPath)) {
            makeDir(sPath, true)
        }
        if (!isOverride && fileExists(path)) {
            path = if (path.contains(".")) {
                val suffix = path.substring(path.lastIndexOf("."))
                val pre = path.substring(0, path.lastIndexOf("."))
                pre + "_" + System.currentTimeMillis() + suffix
            } else {
                path + "_" + System.currentTimeMillis()
            }
        }
        var os: FileOutputStream? = null
        val file: File
        return try {
            file = File(path)
            os = FileOutputStream(file)
            var byteCount = 0
            val bytes = ByteArray(1024)
            while (`is`!!.read(bytes).also { byteCount = it } != -1) {
                os.write(bytes, 0, byteCount)
            }
            os.flush()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("写文件错误", e)
        } finally {
            try {
                os?.close()
                `is`?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 读取指定文件
     *
     * @param fileName SD下的文件路径+文件名，如:a/b.txt
     */
    fun readFile(fileName: String?): String {
        val stringBuffer = StringBuilder()
        try {
            var line: String?
            val file = File(fileName)
            val bufferedReader = BufferedReader(FileReader(file))
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            bufferedReader.close()
        } catch (e: IOException) {
            Log.e("Xposed", Log.getStackTraceString(e))
        }
        return stringBuffer.toString()
    }

    /**
     * 移除字符串中的BOM前缀
     *
     * @param _sLine 需要处理的字符串
     * @return 移除BOM后的字符串.
     */
    private fun removeBomHeaderIfExists(_sLine: String?): String? {
        if (_sLine == null) {
            return null
        }
        var line: String = _sLine
        if (line.length > 0) {
            var ch = line[0]
            // 使用while是因为用一些工具看到过某些文件前几个字节都是0xfffe.
            // 0xfeff,0xfffe是字节序的不同处理.JVM中,一般是0xfeff
            while (ch.code == 0xfeff || ch.code == 0xfffe) {
                line = line.substring(1)
                if (line.length == 0) {
                    break
                }
                ch = line[0]
            }
        }
        return line
    }

    /**
     * 从文件的完整路径名（路径+文件名）中提取 路径（包括：Drive+Directroy )
     *
     * @param _sFilePathName
     * @return
     */
    fun extractFilePath(_sFilePathName: String): String {
        var nPos = _sFilePathName.lastIndexOf('/')
        if (nPos < 0) {
            nPos = _sFilePathName.lastIndexOf('\\')
        }
        return if (nPos >= 0) _sFilePathName.substring(0, nPos + 1) else ""
    }

    /**
     * 检查指定文件的路径是否存在
     *
     * @param _sPathFileName 文件名称(含路径）
     * @return 若存在，则返回true；否则，返回false
     */
    fun pathExists(_sPathFileName: String): Boolean {
        val sPath = extractFilePath(_sPathFileName)
        return fileExists(sPath)
    }

    fun fileExists(_sPathFileName: String?): Boolean {
        val file = File(_sPathFileName)
        return file.exists()
    }

    /**
     * 创建目录
     *
     * @param _sDir             目录名称
     * @param _bCreateParentDir 如果父目录不存在，是否创建父目录
     */
    fun makeDir(_sDir: String?, _bCreateParentDir: Boolean) {
        val file = File(_sDir)
        if (_bCreateParentDir) file.mkdirs() // 如果父目录不存在，则创建所有必需的父目录
        else file.mkdir() // 如果父目录不存在，不做处理
    }

    fun getFileName(path: String): String {
        val index = path.lastIndexOf('/')
        return path.substring(index + 1)
    }
}