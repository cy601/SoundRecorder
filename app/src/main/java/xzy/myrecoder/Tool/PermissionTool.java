package xzy.myrecoder.Tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import xzy.myrecoder.View.Activity.MainActivity;

public class PermissionTool
{
    /*
   动态申请获取系统资源
    */

    public static void applypermission(Context context,Activity activity) {
        String[] allpermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        if (Build.VERSION.SDK_INT >= 23) {
            boolean needapply = false;
            for (int i = 0; i < allpermissions.length; i++) {
                int chechpermission = ContextCompat.checkSelfPermission(context, allpermissions[i]);
                if (chechpermission != PackageManager.PERMISSION_GRANTED) {
                    needapply = true;
                }
            }
            if (needapply) {
                ActivityCompat.requestPermissions(activity, allpermissions, 1);
            }
        }
    }

}
