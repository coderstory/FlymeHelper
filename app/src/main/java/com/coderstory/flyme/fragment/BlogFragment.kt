package com.coderstory.flyme.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.coderstory.flyme.R
import com.coderstory.flyme.tools.Misc
import com.coderstory.flyme.tools.SnackBarUtils

class BlogFragment : WebViewFragment() {
    override val loadUrl: String
        get() = Misc.MyBlogUrl

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_webview_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_copy) {
            val myClipboard: ClipboardManager
            if (activity != null) {
                myClipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val myClip: ClipData
                val text = mWebView?.url ?: ""
                myClip = ClipData.newPlainText("text", text)
                myClipboard.setPrimaryClip(myClip)
                SnackBarUtils.Companion.makeLong(view, getString(R.string.cp_url_success)).show()
            }
        } else if (item.itemId == R.id.action_share) {
            shareMsg(getString(R.string.share_url), mWebView!!.title, mWebView!!.url)
        }
        return false
    }

    fun shareMsg(activityTitle: String?, msgTitle: String?, msgText: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain" // 纯文本
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle)
        intent.putExtra(Intent.EXTRA_TEXT, msgText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(Intent.createChooser(intent, activityTitle))
    }
}