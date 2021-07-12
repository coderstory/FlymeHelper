package com.coderstory.flyme.fragment

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment

abstract class WebViewFragment : BaseFragment() {
    protected var mWebView: WebView? = null
    protected var mProgressBar: ProgressBar? = null
    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_webview
    }

    protected abstract val loadUrl: String
    override fun setUpView() {
        mProgressBar = contentView.findViewById(R.id.progressbar)
        mWebView = contentView.findViewById(R.id.webView)
        initWebViewSettings()
        mWebView!!.webViewClient = MyWebViewClient()
        mWebView!!.webChromeClient = MyWebChromeClient()
        mProgressBar!!.max = 100
        mWebView!!.loadUrl(loadUrl)
    }

    private fun initWebViewSettings() {
        val webSettings = mWebView!!.settings //支持获取手势焦点，输入用户名、密码或其他
        mWebView!!.requestFocusFromTouch() //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN //支持内容重新布局
        webSettings.supportMultipleWindows() //多窗口
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.setNeedInitialFocus(true) //当webview调用requestFocus时为webview设置节点
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
    }

    fun canGoBack(): Boolean {
        return mWebView != null && mWebView!!.canGoBack()
    }

    fun goBack() {
        if (mWebView != null) {
            mWebView!!.goBack()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mWebView != null) mWebView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (mWebView != null) mWebView!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mWebView != null) mWebView!!.destroy()
    }

    //WebViewClient就是帮助WebView处理各种通知、请求事件的。
    internal class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    internal inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            mProgressBar!!.progress = newProgress
            if (newProgress == 100) {
                mProgressBar!!.visibility = View.GONE
            } else {
                mProgressBar!!.visibility = View.VISIBLE
            }
        }
    }
}