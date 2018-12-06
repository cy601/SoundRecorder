package xzy.myrecoder.Tool;

import android.media.MediaPlayer;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MediaPlayerTool {

    public final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recoreder_res";

    //mediaPlayer初始化
    public  void initMediaPlayer(MediaPlayer mediaPlayer,String itemname,SeekBar seekBar)
    {
        try{
            mediaPlayer.setDataSource(path+"/"+itemname);
            mediaPlayer.prepare();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        seekBar.setMax(mediaPlayer.getDuration());//设置SeekBar的总长度
    }


    //播放结束
    public void finishPlaying(MediaPlayer mediaPlayer)
    {
        mediaPlayer.reset();
    }

    //停止播放
    public void stopPlaying(MediaPlayer mediaPlayer)
    {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }


    //监听暂停
    public boolean listenToStopPlaying(MediaPlayer mediaPlayer,boolean flage,int time)
    {
        mediaPlayer.start();
        //如果暂停
        if (flage) {
            mediaPlayer.seekTo(time);
            flage=false;
        }
        return flage;
    }

}
