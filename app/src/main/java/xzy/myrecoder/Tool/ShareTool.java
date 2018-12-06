package xzy.myrecoder.Tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;


import java.io.File;

public class ShareTool
{
    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recoreder_res";

    /*
    通过系统自带API分享文件
     */
    public static void shareAudio(Context context, String fileName) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path + "/" + fileName)));
        shareIntent.setType("audio/mp3");
        context.startActivity(shareIntent);
    }

  /*
  将文件保存至网盘
   */
    public static void saveToDrive(Context context,int position, String fileName) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).getFilePath())));
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path + "/" + fileName)));
        try {
            shareIntent.setPackage("com.baidu.netdisk");
            shareIntent.setClassName("com.baidu.netdisk", "com.baidu.netdisk.ui.EnterShareFileActivity");
            shareIntent.setType("audio/mp3");
            context.startActivity(shareIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请先安装百度网盘APP", Toast.LENGTH_LONG).show();
            launchAppDetail( context,"com.baidu.netdisk","");
        }
    }

    //自动跳转到应用商店 百度网盘APP详情页面
    public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
