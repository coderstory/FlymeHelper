package cn.coderstory.fuckbugme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static cn.coderstory.fuckbugme.Common.canRunRootCommands;
import static cn.coderstory.fuckbugme.Common.execute;

public class MainActivity extends Activity {
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    TextView rootStatus_txt;
    Button getRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootStatus_txt=(TextView)findViewById(R.id.rootStatus);
        getRoot= (Button) findViewById(R.id.button);
        setRootStatus();
    }

    /**
     *
     * @param v
     */
    public void checkroot(View v){
        Toast.makeText(MainActivity.this,"正在查询。。",Toast.LENGTH_LONG).show();
        if (canRunRootCommands()){
            getEditor().putInt("rootStatus",0);

            ArrayList<String> list=new ArrayList<>();
            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.helper.BootBroadcastReceiver");
            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.font.FontTrialService");
            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.theme.ThemeTrialService");
            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.service.ThemeRestoreService");
            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.service.FontRestoreService");
            list.add("pm disable com.meizu.customizecenter/com.meizu.gslb.push.GslbDataRefreshReceiver");

            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.push.CustomizePushReceiver");
            list.add("pm disable com.meizu.customizecenter/com.meizu.customizecenter.common.helper.ShopDemoReceiver");
            list.add("pm disable com.meizu.customizecenter/com.meizu.cloud.pushsdk.SystemReceiver");
            list.add("pm disable com.meizu.customizecenter/com.meizu.advertise.api.AppDownloadAndInstallReceiver");




           if ( !execute(list)){
               getEditor().putInt("rootStatus",1);
           }

        }else{
            getEditor().putInt("rootStatus",2);
        }

        getEditor().apply();
        setRootStatus();

    }

    protected SharedPreferences.Editor getEditor() {
        return editor == null?editor = getPrefs().edit():editor;
    }

    protected SharedPreferences getPrefs() {
        return prefs==null?prefs=getBaseContext().getSharedPreferences("UserSettings", Context.MODE_PRIVATE):prefs;
    }


    protected  void setRootStatus(){
        int rootStatus=  getPrefs().getInt("rootStatus",2);
        switch (rootStatus){
            case 0:
                rootStatus_txt.setText("当前Root授权状态:已获取授权");
                getRoot.setText("重新授权");
                break;
            case 1:
                rootStatus_txt.setText("当前Root授权状态:已获取授权,但命令执行出错");
                break;
            case 2:
                rootStatus_txt.setText("当前Root授权状态:尚未获取");
                break;
        }
    }
     public  void openBlog(View v){
         Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.coderstory.cn"));
         startActivity(it);
     }
}
