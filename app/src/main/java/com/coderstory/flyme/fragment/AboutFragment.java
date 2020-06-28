package com.coderstory.flyme.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coderstory.flyme.BuildConfig;
import com.coderstory.flyme.R;
import com.coderstory.flyme.config.Misc;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.utils.SharedHelper;
import com.coderstory.flyme.utils.licensesdialog.LicensesDialog;
import com.coderstory.flyme.utils.licensesdialog.licenses.ApacheSoftwareLicense20;
import com.coderstory.flyme.utils.licensesdialog.licenses.GnuGeneralPublicLicense20;
import com.coderstory.flyme.utils.licensesdialog.model.Notice;
import com.coderstory.flyme.utils.licensesdialog.model.Notices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class AboutFragment extends BaseFragment {

    private SharedHelper helper;
    private ProgressDialog dialog;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_about;
    }

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
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getMContext());
                    dialog.setTitle("提示");
                    dialog.setMessage("本应用尚未再Xposed中启用,请启用后再试...");
                    dialog.setPositiveButton("退出", (dialog12, which) -> {
                        System.exit(0);
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                    break;

                case 4:
                    Toast.makeText(getMContext(), "绑定成功", Toast.LENGTH_SHORT).show();
                    if (msg.getData().get("value").equals("{\"error\":\"0\"}")) {
                        getEditor().putString("qq", msg.getData().get("qq").toString()).apply();
                        getEditor().putString("uuid", msg.getData().get("uuid").toString()).apply();
                    } else {
                        Toast.makeText(getMContext(), "绑定失败\r\n" + msg.getData().get("value"), Toast.LENGTH_LONG).show();
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

    @Override
    protected void setUpView() {
        helper = new SharedHelper(getMContext());
        TextView tv_content = $(R.id.tv_content);
        tv_content.setAutoLinkMask(Linkify.ALL);
        tv_content.setMovementMethod(LinkMovementMethod
                .getInstance());

        $(R.id.os).setOnClickListener(v -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("ApacheSoftwareLicense", "", "", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("GnuGeneralPublicLicense", "", "", new GnuGeneralPublicLicense20()));

            new LicensesDialog.Builder(getMContext())
                    .setNotices(notices)
                    .build()
                    .show();
        });

        ((TextView) $(R.id.version)).setText(BuildConfig.VERSION_NAME);

        ((TextView) $(R.id.mark)).setText("当前版本类型:" + (helper.getString("qq", "").equals("") || helper.getString("uuid", "").equals("") ? "体验版" : "完整版"));
        ((TextView) $(R.id.qq)).setText("绑定QQ:" + helper.getString("qq", "无"));


        if (helper.getString("qq", "").equals("") || helper.getString("uuid", "").equals("")) {
            final EditText inputServer = new EditText(getMContext());
            inputServer.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
            AlertDialog.Builder builder = new AlertDialog.Builder(getMContext());
            builder.setTitle("请加群906552736后再此输入您加群使用的QQ号").setView(inputServer);
            builder.setPositiveButton("确定", (dialog, which) -> {
                String _sign = inputServer.getText().toString();
                if (!_sign.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
                    new Thread(new Check(_sign, uuid)).start();
                } else {
                    Toast.makeText(getMContext(), "QQ号不能为空", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    class Check implements Runnable {

        String qq;
        String uuid;

        public Check(String qq, String uuid) {
            this.qq = qq;
            this.uuid = uuid;
        }

        @Override
        public void run() {
            String path = Misc.searchApi;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                //数据准备
                String data = "{\n" +
                        "    \"QQ\": \"" + qq + "\",\n" +
                        "    \"uuid\": \"" + uuid + "\",\n" +
                        "    \"isLogin\": 1\n" +
                        "}";
                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", data.length() + "");

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());

                //获得结果码
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    //请求成功
                    InputStream is = connection.getInputStream();

                    Message msg = new Message();
                    msg.arg1 = 4;
                    Bundle data2 = new Bundle();
                    data2.putString("value", dealResponseResult(is));
                    data2.putString("qq", qq);
                    data2.putString("uuid", uuid);
                    msg.setData(data2);
                    myHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.arg1 = 5;
                    myHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String dealResponseResult(InputStream inputStream) {
            String resultData;      //存储处理结果
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len = 0;
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultData = new String(byteArrayOutputStream.toByteArray());
            return resultData;
        }
    }

}
