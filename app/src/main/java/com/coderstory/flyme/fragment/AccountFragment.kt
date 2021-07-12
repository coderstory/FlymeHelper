package com.coderstory.flyme.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.coderstory.flyme.R
import com.coderstory.flyme.fragment.base.BaseFragment
import com.coderstory.flyme.tools.SharedHelper
import com.coderstory.flyme.tools.Utils
import com.google.gson.Gson
import com.topjohnwu.superuser.Shell
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.DialogLayer
import per.goweii.anylayer.Layer

class AccountFragment : BaseFragment() {
    private var helper: SharedHelper? = null
    private var dialog: ProgressDialog? = null

    @SuppressLint("HandlerLeak")
    var myHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.arg1) {
                0 -> {
                    val normalDialog = AlertDialog.Builder(mContext)
                    normalDialog.setTitle("提示")
                    normalDialog.setMessage("请先授权应用ROOT权限")
                    normalDialog.setPositiveButton("确定"
                    ) { dialog: DialogInterface?, which: Int -> System.exit(0) }
                    normalDialog.show()
                    super.handleMessage(msg)
                }
                1 -> {
                    dialog = ProgressDialog.show(mContext, "检测ROOT权限", "请在ROOT授权弹窗中给与ROOT权限,\n如果长时间无反应则请检查ROOT程序是否被\"省电程序\"干掉")
                    dialog!!.show()
                }
                2 -> if (dialog != null && dialog!!.isShowing) {
                    dialog!!.cancel()
                    helper!!.put("isRooted", true)
                }
                3 -> {
                    val dialog = android.app.AlertDialog.Builder(mContext)
                    dialog.setTitle("提示")
                    dialog.setMessage("本应用尚未再Xposed中启用,请启用后再试...")
                    dialog.setPositiveButton("退出") { dialog12: DialogInterface?, which: Int -> System.exit(0) }
                    dialog.setCancelable(false)
                    dialog.show()
                }
                4 -> if (msg.data["value"] == "{\"error\":\"0\"}") {
                    editor.putString(Utils.Companion.decode("bWFyaw=="), Utils.Companion.encodeStr(msg.data["mark"].toString())).commit()
                    sudoFixPermissions()
                    //getEditor().putString("sn", msg.getData().get("sn").toString()).apply();
                    Toast.makeText(mContext, "绑定成功,重启应用生效", Toast.LENGTH_SHORT).show()
                    refresh()
                } else {
                    Toast.makeText(mContext, Utils.Companion.decode("5Lya5ZGY5qCh6aqM5aSx6LSl") + ":\r\n" +
                            Gson().fromJson<Map<String, String>>(msg.data["value"].toString(), MutableMap::class.java).getOrDefault("error", msg.data["value"].toString()), Toast.LENGTH_LONG).show()
                }
                5 ->                     // 接口调用失败
                    Toast.makeText(mContext, "服务器连接失败", Toast.LENGTH_LONG).show()
            }
        }
    }
    val serialNumber: String?
        get() {
            val result: List<String> = Shell.su(Utils.Companion.decode("Z2V0cHJvcCUyMHJvLnNlcmlhbG5v").replace("%20", " ")).exec().out
            if (result.size == 0) {
                return null
            }
            if (result[0].contains("start command")) {
                val normalDialog = android.app.AlertDialog.Builder(mContext)
                normalDialog.setTitle("!!致命错误!!")
                normalDialog.setMessage("你手机的ROOT已爆炸,请刷机后重试!")
                normalDialog.setPositiveButton("确定"
                ) { dialog: DialogInterface?, which: Int -> System.exit(0) }
                normalDialog.setCancelable(true)
                normalDialog.show()
            }
            return result[0]
        }

    override fun setLayoutResourceID(): Int {
        return R.layout.fragment_about_me
    }

    override fun setUpView() {
        helper = SharedHelper(mContext)
        `$`<View>(R.id.join_free_group).setOnClickListener { v: View? ->
            if (!joinQQGroup("k8v9MsMgZjsyUBhmL76_tnid2opGauic")) {
                Toast.makeText(mContext, "拉起手Q失败", Toast.LENGTH_LONG).show()
            }
        }
        refresh()
        `$`<View>(R.id.activation).setOnClickListener { v: View? -> ababa() }
        `$`<View>(R.id.join_vip_group).setOnClickListener { v: View? ->
            if (!joinQQGroup("dNIW3xRJ8YKTdsFcJBak3_cZ0AwTBdEn")) {
                Toast.makeText(mContext, "拉起手Q失败", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun refresh() {
        (`$`<View>(R.id.vip_version) as TextView).text = Utils.Companion.decode("5b2T5YmN54mI5pys57G75Z6L") + ": " + if (!Utils.Companion.check(helper)) Utils.Companion.decode("5YWN6LS554mI") else Utils.Companion.decode("5LuY6LS554mI")
        (`$`<View>(R.id.bound_qq) as TextView).text = Utils.Companion.decode("57uR5a6aUVE=") + ": " + Utils.Companion.decodeStr(helper!!.getString(Utils.Companion.decode("bWFyaw=="), "瘡"))
    }

    /****************
     *
     * 发起添加群流程。群号：Flyme助手和Xposed交流(717515891) 的 key 为： Dj5VgtTIdGo8nuk8wyMnYaHydxMxD6Dl
     * 调用 joinQQGroup(Dj5VgtTIdGo8nuk8wyMnYaHydxMxD6Dl) 即可发起手Q客户端申请加群 Flyme助手和Xposed交流(717515891)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     */
    fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }
    }

    private fun openInputDialog() {
        val inputServer = EditText(mContext)
        inputServer.filters = arrayOf<InputFilter>(LengthFilter(17))
        inputServer.keyListener = DigitsKeyListener.getInstance("0123456789")
        val builder = android.app.AlertDialog.Builder(mContext)
        builder.setTitle(Utils.Companion.decode("5LuY6LS55LiU57uR5a6a5L2g55qEUVHlkI4NCuWcqOatpOi+k+WFpeS9oOeahFFR5bm254K55Ye76Kej6ZSBISE=")).setView(inputServer)
        builder.setPositiveButton(Utils.Companion.decode("5r+A5rS7"), DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
            val _sign = inputServer.text.toString()
            if (!_sign.isEmpty()) {
                val sn = serialNumber
                if (sn == null) {
                    val normalDialog = AlertDialog.Builder(mContext)
                    normalDialog.setTitle("提示")
                    normalDialog.setMessage("请先授权应用ROOT权限")
                    normalDialog.setPositiveButton("确定"
                    ) { dialog1: DialogInterface?, which1: Int -> System.exit(0) }
                    normalDialog.show()
                } else {
                    Thread(Utils().Check(_sign, myHandler, mContext)).start()
                }
            } else {
                Toast.makeText(mContext, Utils.Companion.decode("UVHlj7fkuI3og73kuLrnqbo="), Toast.LENGTH_SHORT).show()
            }
        })
        builder.show()
    }

    private fun ababa() {
        val anyLayer = AnyLayer.dialog(context)
                .contentView(R.layout.dialog_input_qq)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick({ layer: Layer, v: View? ->
                    val textView = layer.getView<TextView>(R.id.input_qq)
                    val _sign = textView.text.toString()
                    if (!_sign.isEmpty()) {
                        val sn = serialNumber
                        if (sn == null) {
                            val normalDialog = AlertDialog.Builder(mContext)
                            normalDialog.setTitle("提示")
                            normalDialog.setMessage("请先授权应用ROOT权限")
                            normalDialog.setPositiveButton("确定"
                            ) { dialog1: DialogInterface?, which1: Int -> System.exit(0) }
                            normalDialog.show()
                        } else {
                            Thread(Utils().Check(_sign, myHandler, mContext)).start()
                        }
                    } else {
                        Toast.makeText(mContext, Utils.Companion.decode("UVHlj7fkuI3og73kuLrnqbo="), Toast.LENGTH_SHORT).show()
                    }
                    layer.dismiss()
                }, R.id.dialog_ok)
        anyLayer.show()
        val cardView = (anyLayer as DialogLayer).contentView as CardView
        val linearLayout = cardView.getChildAt(0) as LinearLayout
        val textView = linearLayout.getChildAt(1) as TextView
        textView.text = Utils.Companion.decode("5LuY6LS55LiU57uR5a6a5L2g55qEUVHlkI4NCuWcqOatpOi+k+WFpeS9oOeahFFR5bm254K55Ye76Kej6ZSBISE=")
    }
}