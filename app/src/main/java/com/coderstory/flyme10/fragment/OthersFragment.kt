package com.coderstory.flyme10.fragment

import android.app.ProgressDialog
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.coderstory.flyme10.R
import com.coderstory.flyme10.fragment.base.BaseFragment
import com.topjohnwu.superuser.Shell
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.Layer

class OthersFragment : BaseFragment() {
    var dialog: ProgressDialog? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_upgrade_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val anyLayer = AnyLayer.dialog(mContext)
            .contentView(R.layout.dialog_disable_app)
            .cancelableOnTouchOutside(true)
            .cancelableOnClickKeyBack(true)
            .onClick({ AnyLayer: Layer, v: View? -> AnyLayer.dismiss() }, R.id.fl_dialog_no)
            .onClick({ AnyLayer: Layer?, v: View? ->
                Shell.su("killall com.android.systemui").exec()
                Shell.su("am force-stop com.meizu.flyme10.launcher").exec()
                System.exit(0)
            }, R.id.fl_dialog_yes)
        anyLayer.show()
        val cardView = (anyLayer as DialogLayer).contentView as CardView
        val linearLayout = cardView.getChildAt(0) as LinearLayout
        val textView = linearLayout.getChildAt(1) as TextView
        textView.text = "一键重启桌面状态栏包管理器等app"
        return false
    }

    override fun setUpView() {
        `$`<View>(R.id.enableBlockAD).setOnClickListener { v: View ->
            editor.putBoolean("EnableBlockAD", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.enabletheme).setOnClickListener { v: View ->
            editor.putBoolean("enabletheme", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hide_icon_label).setOnClickListener { v: View ->
            editor.putBoolean("hide_icon_label", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.enableCTS).setOnClickListener { v: View ->
            editor.putBoolean("enableCTS", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.enableCheckInstaller).setOnClickListener { v: View ->
            editor.putBoolean("enableCheckInstaller", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.hideDepWarn).setOnClickListener { v: View ->
            editor.putBoolean("hideDepWarn", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.removeStore).setOnClickListener { v: View ->
            editor.putBoolean("removeStore", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.autoInstall).setOnClickListener { v: View ->
            editor.putBoolean("autoInstall", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.disableSearch).setOnClickListener { v: View ->
            editor.putBoolean("disableSearch", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.disable_charge_animation).setOnClickListener { v: View ->
            editor.putBoolean("disable_charge_animation", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.double_clock_sleep).setOnClickListener { v: View ->
            editor.putBoolean("double_clock_sleep", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.click_to_clock).setOnClickListener { v: View ->
            editor.putBoolean("click_to_clock", (v as SwitchCompat).isChecked)
            fix()
        }
        `$`<View>(R.id.click_to_calendar).setOnClickListener { v: View ->
            editor.putBoolean("click_to_calendar", (v as SwitchCompat).isChecked)
            fix()
        }
        val carrierName = `$`<EditText>(R.id.enable_back_vibrator)
        carrierName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                editor.putString("enable_back_vibrator_value", editable.toString())
                fix()
            }
        })

    }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_others
    }

    override fun setUpData() {
        (`$`<View>(R.id.enable_back_vibrator) as EditText).setText(
            prefs.getString(
                "enable_back_vibrator_value",
                ""
            )
        )
        (`$`<View>(R.id.disable_charge_animation) as SwitchCompat).isChecked =
            prefs.getBoolean("disable_charge_animation", false)
        (`$`<View>(R.id.double_clock_sleep) as SwitchCompat).isChecked =
            prefs.getBoolean("double_clock_sleep", true)
        (`$`<View>(R.id.click_to_clock) as SwitchCompat).isChecked =
            prefs.getBoolean("click_to_clock", true)
        (`$`<View>(R.id.click_to_calendar) as SwitchCompat).isChecked =
            prefs.getBoolean("click_to_calendar", true)
        (`$`<View>(R.id.hide_icon_label) as SwitchCompat).isChecked =
            prefs.getBoolean("hide_icon_label", false)
        (`$`<View>(R.id.enableBlockAD) as SwitchCompat).isChecked =
            prefs.getBoolean("EnableBlockAD", false)
        (`$`<View>(R.id.enabletheme) as SwitchCompat).isChecked =
            prefs.getBoolean("enabletheme", false)
        (`$`<View>(R.id.enableCheckInstaller) as SwitchCompat).isChecked =
            prefs.getBoolean("enableCheckInstaller", false)
        (`$`<View>(R.id.enableCTS) as SwitchCompat).isChecked = prefs.getBoolean("enableCTS", false)
        (`$`<View>(R.id.hideDepWarn) as SwitchCompat).isChecked =
            prefs.getBoolean("hideDepWarn", false)
        (`$`<View>(R.id.removeStore) as SwitchCompat).isChecked =
            prefs.getBoolean("removeStore", false)
        (`$`<View>(R.id.autoInstall) as SwitchCompat).isChecked =
            prefs.getBoolean("autoInstall", false)
        (`$`<View>(R.id.disableSearch) as SwitchCompat).isChecked =
            prefs.getBoolean("disableSearch", false)

    }

    private fun setDatePickerDividerColor(picker: NumberPicker?, max: Int, min: Int) {
        //设置最大值
        picker!!.maxValue = max
        //设置最小值
        picker.minValue = min
        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    pf[picker] = ColorDrawable(Color.alpha(256))
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: NotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
                break
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        setUpData()
    }
}