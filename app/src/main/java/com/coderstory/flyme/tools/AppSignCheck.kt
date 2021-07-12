package com.coderstory.flyme.tools

import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

class AppSignCheck {
    private val context: Context?
    private var cer: String? = null

    /**
     * 设置正确的签名
     *
     * @param realCer
     */
    var realCer: String? = null

    constructor(context: Context?) {
        this.context = context
        try {
            cer = certificateSHA1Fingerprint
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
    }

    constructor(context: Context?, realCer: String?) {
        this.context = context
        this.realCer = realCer
        try {
            cer = certificateSHA1Fingerprint
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }//获取包管理器

    //获取当前要获取 SHA1 值的包名，也可以用其他的包名，但需要注意，
    //在用其他包名的前提是，此方法传递的参数 Context 应该是对应包的上下文。

    //返回包括在包中的签名信息


    //获得包的所有内容信息类


    //签名信息

    //将签名转换为字节数组流

    //证书工厂类，这个类实现了出厂合格证算法的功能


    //X509 证书，X.509 是一种非常通用的证书格式


    //加密算法的类，这里的参数可以使 MD4,MD5 等加密算法

    //获得公钥

    //字节到十六进制的格式转换
    /**
     * 获取应用的签名
     *
     * @return
     */
    @get:Throws(PackageManager.NameNotFoundException::class, CertificateException::class, NoSuchAlgorithmException::class)
    val certificateSHA1Fingerprint: String?
        get() {
            //获取包管理器
            val pm = context!!.packageManager

            //获取当前要获取 SHA1 值的包名，也可以用其他的包名，但需要注意，
            //在用其他包名的前提是，此方法传递的参数 Context 应该是对应包的上下文。
            val packageName = context.packageName

            //返回包括在包中的签名信息
            val flags = PackageManager.GET_SIGNATURES
            var packageInfo: PackageInfo? = null


            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags)


            //签名信息
            val signatures = packageInfo.signatures
            val cert = signatures[0].toByteArray()

            //将签名转换为字节数组流
            val input: InputStream = ByteArrayInputStream(cert)

            //证书工厂类，这个类实现了出厂合格证算法的功能
            var cf: CertificateFactory? = null
            cf = CertificateFactory.getInstance(Utils.Companion.decode("WDUwOQ=="))


            //X509 证书，X.509 是一种非常通用的证书格式
            var c: X509Certificate? = null
            c = cf.generateCertificate(input) as X509Certificate
            var hexString: String? = null


            //加密算法的类，这里的参数可以使 MD4,MD5 等加密算法
            val md = MessageDigest.getInstance(Utils.Companion.decode("U0hBMQ=="))

            //获得公钥
            val publicKey = md.digest(c.encoded)

            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey)
            return hexString
        }

    //这里是将获取到得编码进行16 进制转换
    private fun byte2HexFormatted(arr: ByteArray): String {
        val str = StringBuilder(arr.size * 2)
        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1) h = "0$h"
            if (l > 2) h = h.substring(l - 2, l)
            str.append(h.uppercase(Locale.getDefault()))
            if (i < arr.size - 1) str.append(':')
        }
        return str.toString()
    }

    /**
     * 检测签名是否正确
     *
     * @return true 签名正常 false 签名不正常
     */
    fun check(): Boolean {
        if (realCer != null) {
            cer = cer!!.trim { it <= ' ' }
            cer = encrypt(cer!!)
            realCer = realCer!!.trim { it <= ' ' }
            return cer == realCer
        }
        return false
    }

    companion object {
        private const val slat = "&%5123***JKO&%%$$#@"
        fun encrypt(dataStr: String): String {
            var dataStr = dataStr
            try {
                dataStr = dataStr + slat
                val m = MessageDigest.getInstance("MD5")
                m.update(dataStr.toByteArray(StandardCharsets.UTF_8))
                val s = m.digest()
                val result = StringBuilder()
                for (i in s.indices) {
                    result.append(Integer.toHexString(0x000000FF and s[i].toInt() or -0x100).substring(6))
                }
                return result.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    }
}