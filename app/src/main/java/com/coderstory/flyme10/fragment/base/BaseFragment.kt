package com.coderstory.flyme10.fragment.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coderstory.flyme10.tools.Misc
import com.coderstory.flyme10.tools.Utils
import com.topjohnwu.superuser.Shell
import java.io.File

/**
 * Created by _SOLID
 * Date:2016/3/30
 * Time:11:30
 */
abstract class BaseFragment : Fragment() {
    protected var contentView: View? = null
        private set
    lateinit var mContext: Context
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(
            setLayoutResourceID(),
            container,
            false
        ) //setContentView(inflater, container);
        mContext = requireContext()
        val mProgressDialog = ProgressDialog(mContext)
        mProgressDialog.setCanceledOnTouchOutside(false)
        setHasOptionsMenu(true)
        init()
        setUpView()
        setUpData()
        prefs
        return contentView
    }

    protected abstract fun setLayoutResourceID(): Int
    protected open fun setUpData() {}
    protected val editor: SharedPreferences.Editor
        get() {
            if (Companion.editor == null) {
                Companion.editor = prefs.edit()
            }
            return Companion.editor!!
        }
    protected val prefs: SharedPreferences
        get() {
            Companion.prefs = Utils.getMySharedPreferences(
                mContext.applicationContext,
                Misc.SharedPreferencesName
            )
            return Companion.prefs
        }

    fun fix() {
        editor.commit()
        sudoFixPermissions()
    }

    @SuppressLint("SetWorldReadable")
    protected fun sudoFixPermissions() {
        if (Build.VERSION.SDK_INT < 30) {
            Thread {
                val pkgFolder = File("/data/user_de/0/" + Misc.ApplicationName)
                if (pkgFolder.exists()) {
                    pkgFolder.setExecutable(true, false)
                    pkgFolder.setReadable(true, false)
                }
                Shell.su("chmod  755 $PREFS_FOLDER").exec()
                // Set preferences file permissions to be world readable
                Shell.su("chmod  644 $PREFS_FILE").exec()
            }.start()
        }
    }

    protected open fun init() {}
    protected open fun setUpView() {}
    protected fun <T : View?> `$`(id: Int): T {
        return contentView!!.findViewById(id)
    }

    companion object {
        const val PREFS_FOLDER = " /data/user_de/0/" + Misc.ApplicationName + "/shared_prefs\n"
        const val PREFS_FILE =
            " /data/user_de/0/" + Misc.ApplicationName + "/shared_prefs/" + Misc.SharedPreferencesName + ".xml\n"
        private lateinit var prefs: SharedPreferences
        private var editor: SharedPreferences.Editor? = null
    }
}