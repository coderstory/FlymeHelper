package cn.coderstory.fuckbugme;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by coderstory on 17-2-2.
 */

public class Common {


    /**
     * 判断是否已经被授权root
     * @return boolean
     */
    public static boolean canRunRootCommands() {
        boolean Result ;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            // DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
            BufferedReader din=new BufferedReader(new InputStreamReader(suProcess.getInputStream()));

            // Getting the id of the current user to check if this is root
            os.writeBytes("id\n");
            os.flush();

            String currUid = din.readLine();
            boolean exitSu ;
            if (null == currUid) {
                Result = false;
                exitSu = false;
                Log.d("ROOT", "Can't get root access or denied by user");
            } else if (currUid.contains("uid=0")) {
                Result = true;
                exitSu = true;
                Log.d("ROOT", "Root access granted");
            } else {
                Result = false;
                exitSu = true;
                Log.d("ROOT", "Root access rejected: " + currUid);
            }

            if (exitSu) {
                os.writeBytes("exit\n");
                os.flush();
            }

        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output
            // stream after su failed, meaning that the device is not rooted
            Result = false;
            Log.d("ROOT",
                    "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return Result;
    }


    /**
     * 执行所提交的命令组
     * @return 执行结果
     */
    public final static  boolean execute(ArrayList<String>  commands) {
        boolean retval = false;
        try {
            if (null != commands && commands.size() > 0) {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                for (String currCommand : commands) {
                    os.writeBytes(currCommand + "\n");
                    os.flush();
                }
                os.writeBytes("exit\n");
                os.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuilder output = new StringBuilder();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();
                try {
                    int suProcessRetval = process.waitFor();
                    retval = 255 != suProcessRetval;
                    Log.d("gg", "execute: "+output.toString());
                } catch (Exception ex) {
                    //Log.e("Error executing root action", ex);
                }
            }
        } catch (IOException ex) {
            Log.w("ROOT", "Can't get root access", ex);
        }  catch (Exception ex) {
            Log.w("ROOT", "Error executing internal operation", ex);
        }
        return retval;
    }
}
