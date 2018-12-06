package xzy.myrecoder.Tool;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FileTool {
    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recoreder_res";

    /*
    进入MainActivity就检测是否存在资源文件夹recoreder_res
    若没有就创建文件夹
    否则，反之
     */
    public static void createFile() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

    }
    /*
        获取文件大小 单位为KB
    */
    public static   String getFileSize(String fileName)
    {
        File file = new File(path+"/"+fileName);
        if (file.exists() && file.isFile()) {
            return (file.length()/1024)+"KB";
        }
        return "null";
    }
    /*
    传入音频文件名
    删除recoreder_res的音频文件
     */
    public static void deleteFile(String fileName){
        File file = new File(path+"/"+fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /*
     更改音频文件名
     有可能新的命名已存在 避免覆盖
     */
    public static int renameFile(String oldName,String newName)
    {
        File oldfile = new File(path+"/"+oldName);
        File newfile = new File(path+"/"+newName);
        if (oldfile.exists() && oldfile.isFile()&&!newfile.exists()) {
            oldfile.renameTo(newfile);
            return 1;
        }
        else
            return 0;
    }

    //获取音频文件的播放长度
    public static String getFileLength(String fileName)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();
        long minutes = 0;
        long seconds = 0;
        try {
            mediaPlayer.setDataSource(path+"/"+fileName);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();

        }
        minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
        seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())
                - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes,Math.abs(seconds-1));
    }

    /*
    获取一个默认文件名
     */
    public static String getDefaultName()
    {
        String prefix = path+"/"+"unnamed";
        String suffix = ".amr";
        int i =1;
        File file = new File(prefix+i+suffix);
        while(file.exists())
        {
            i++;
           file = new File(prefix+i+suffix);
        }
        return "unnamed"+i+suffix;
    }

    public static String[] existAmr()
    {
        File file = new File(path);
        MyFilter filter = new MyFilter(".amr");
        String[] files = file.list(filter);
        return files;
    }

    static class MyFilter implements FilenameFilter {
        private String type;

        public MyFilter(String type) {
            this.type = type;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(type);
        }
    }
}
