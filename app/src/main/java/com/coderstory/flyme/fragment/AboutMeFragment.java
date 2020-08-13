package com.coderstory.flyme.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.alibaba.fastjson.JSON;
import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.Utils;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;

public class AboutMeFragment extends BaseFragment {

    private SharedHelper helper;
    private ProgressDialog dialog;
    @SuppressLint("HandlerLeak")
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 0:
                    final androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("请先授权应用ROOT权限");
                    normalDialog.setPositiveButton("确定",
                            (dialog, which) -> System.exit(0));
                    normalDialog.show();
                    super.handleMessage(msg);
                    break;
                case 1:
                    dialog = ProgressDialog.show(getMContext(), "检测ROOT权限", "请在ROOT授权弹窗中给与ROOT权限,\n如果长时间无反应则请检查ROOT程序是否被\"省电程序\"干掉");
                    dialog.show();
                    break;
                case 2:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.cancel();
                        helper.put("isRooted", true);
                    }
                    break;
                case 3:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getMContext());
                    dialog.setTitle("提示");
                    dialog.setMessage("本应用尚未再Xposed中启用,请启用后再试...");
                    dialog.setPositiveButton("退出", (dialog12, which) -> {
                        System.exit(0);
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                    break;

                case 4:
                    if (msg.getData().get("value").equals("{\"error\":\"0\"}")) {
                        getEditor().putString("qq", msg.getData().get("qq").toString()).apply();
                        getEditor().putString("sn", msg.getData().get("sn").toString()).apply();
                        Toast.makeText(getMContext(), "绑定成功,重启应用生效", Toast.LENGTH_SHORT).show();
                        refresh();
                    } else {
                        Toast.makeText(getMContext(), Utils.decode("5Lya5ZGY5qCh6aqM5aSx6LSl") + ":\r\n" + JSON.parseObject(msg.getData().get("value").toString()).getOrDefault("error", msg.getData().get("value").toString()), Toast.LENGTH_LONG).show();
                    }
                    // 校验返回
                    break;
                case 5:
                    // 接口调用失败
                    Toast.makeText(getMContext(), "服务器连接失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public String getSerialNumber() {

        List<String> result = Shell.SU.run(Utils.decode("Z2V0cHJvcCUyMHJvLnNlcmlhbG5v").replace("%20", " "));
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_about_me;
    }

    @Override
    protected void setUpView() {
        helper = new SharedHelper(getMContext());

        $(R.id.join_free_group).setOnClickListener(v -> {
            if (!joinQQGroup("k8v9MsMgZjsyUBhmL76_tnid2opGauic")) {
                Toast.makeText(getMContext(), "拉起手Q失败", Toast.LENGTH_LONG).show();
            }
        });

        refresh();

        if (!Utils.check(helper)) {
            $(R.id.activation).setOnClickListener(v -> test());
            // $(R.id.join_vip_group).setOnClickListener(v -> Toast.makeText(getMContext(), Utils.decode("5bCa5pyq5r+A5rS75Lya5ZGYLOS4jeWPr+eUs+ivtw=="), Toast.LENGTH_LONG).show());
        } else {
            $(R.id.activation).setVisibility(View.GONE);
            $(R.id.join_vip_group).setVisibility(View.GONE);
        }

        $(R.id.join_vip_group).setOnClickListener(v -> {
            if (!joinQQGroup("dNIW3xRJ8YKTdsFcJBak3_cZ0AwTBdEn")) {
                Toast.makeText(getMContext(), "拉起手Q失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refresh() {
        ((TextView) $(R.id.vip_version)).setText(Utils.decode("5b2T5YmN54mI5pys57G75Z6L") + ": " + (!Utils.check(helper) ? Utils.decode("5YWN6LS554mI") : Utils.decode("5LuY6LS554mI")));
        ((TextView) $(R.id.bound_qq)).setText(Utils.decode("57uR5a6aUVE=") + ": " + helper.getString("qq", "无"));
    }

    /****************
     *
     * 发起添加群流程。群号：Flyme助手和Xposed交流(717515891) 的 key 为： Dj5VgtTIdGo8nuk8wyMnYaHydxMxD6Dl
     * 调用 joinQQGroup(Dj5VgtTIdGo8nuk8wyMnYaHydxMxD6Dl) 即可发起手Q客户端申请加群 Flyme助手和Xposed交流(717515891)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    private void openInputDialog() {
        final EditText inputServer = new EditText(getMContext());
        inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
        inputServer.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        AlertDialog.Builder builder = new AlertDialog.Builder(getMContext());
        builder.setTitle(Utils.decode("5LuY6LS55LiU57uR5a6a5L2g55qEUVHlkI4NCuWcqOatpOi+k+WFpeS9oOeahFFR5bm254K55Ye76Kej6ZSBISE=")).setView(inputServer);
        builder.setPositiveButton(Utils.decode("5r+A5rS7"), (dialog, which) -> {
            String _sign = inputServer.getText().toString();
            if (!_sign.isEmpty()) {
                String sn = getSerialNumber();
                if (sn == null) {
                    androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("请先授权应用ROOT权限");
                    normalDialog.setPositiveButton("确定",
                            (dialog1, which1) -> System.exit(0));
                    normalDialog.show();
                } else {
                    new Thread(new Utils().new Check(_sign, myHandler)).start();
                }
            } else {
                Toast.makeText(getMContext(), Utils.decode("UVHlj7fkuI3og73kuLrnqbo="), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    private void test() {
        Layer anyLayer = AnyLayer.dialog(getContext())
                .contentView(R.layout.dialog_input_qq)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick((layer, v) -> {
                    TextView textView = layer.getView(R.id.input_qq);
                    String _sign = textView.getText().toString();
                    if (!_sign.isEmpty()) {
                        String sn = getSerialNumber();
                        if (sn == null) {
                            androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(getMContext());
                            normalDialog.setTitle("提示");
                            normalDialog.setMessage("请先授权应用ROOT权限");
                            normalDialog.setPositiveButton("确定",
                                    (dialog1, which1) -> System.exit(0));
                            normalDialog.show();
                        } else {
                            new Thread(new Utils().new Check(_sign, myHandler)).start();
                        }
                    } else {
                        Toast.makeText(getMContext(), Utils.decode("UVHlj7fkuI3og73kuLrnqbo="), Toast.LENGTH_SHORT).show();
                    }
                    layer.dismiss();
                }, R.id.dialog_ok);

        anyLayer.show();

        CardView cardView = (CardView) ((DialogLayer) anyLayer).getContentView();
        LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
        TextView textView = (TextView) linearLayout.getChildAt(1);


        textView.setText(Utils.decode("5LuY6LS55LiU57uR5a6a5L2g55qEUVHlkI4NCuWcqOatpOi+k+WFpeS9oOeahFFR5bm254K55Ye76Kej6ZSBISE="));
    }
}
