package xzy.myrecoder.Tool;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;

public class RecoderTool
{
    private final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recoreder_res";
    private MediaRecorder mRecorder;// 录音器
    private boolean isPause = false;//暂停状态
    private ArrayList<String> mList = new ArrayList<String>();// 待合成的录音片段

    private String fileName = null;
    private RecoderItemDao recoderItemDao= GreenDaoHelper.getDaoSession().getRecoderItemDao();


    /*
    开始录音
     */
    public void startRecord() {
        mRecorder = new MediaRecorder();
        // 当进入新录音，清空缓存列表
        if (!isPause) {
            mList.clear();
        }
        isPause = false;

        fileName = path + "/" + TimeTool.getDate()+"_"+TimeTool.getTime() + ".amr";
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (Exception e) {
        }
        if (mRecorder != null) {
            mRecorder.start();
        }

    }
    /*
        暂停录音
        把已经录音的文件的文件名保存到mList
    */
    public void pauseRecord(){
        mRecorder.stop();
        mRecorder.release();

        // 将录音片段加入列表
        mList.add(fileName);
        isPause = true;
    }

/*
       结束录音，对已录的内容进行处理
 */
    public void stopRecord() {
        //当在播放的时候，按下停止键
        if(isPause==false)
        mList.add(fileName);

        mRecorder.release();
        mRecorder = null;
        isPause = false;

        //默认生成的音频名字
        fileName = path+"/"+FileTool.getDefaultName();

        /*
        将存在mlist里面的文件输出到新文件
        若有多个音频文件，除第一个，其余的文件前6个字节需要删除，从而将文件拼接
         */
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        try {
            for (int i = 0; i < mList.size(); i++) {
                File file = new File(mList.get(i));
                // 把因为暂停所录出的多段录音进行读取
                fileInputStream = new FileInputStream(file);
                byte[] mByte = new byte[fileInputStream.available()];
                int length = mByte.length;
                // 第一个录音文件的前六位是不需要删除的
                if (i == 0) {
                    while (fileInputStream.read(mByte) != -1) {
                        fileOutputStream.write(mByte, 0, length);
                    }
                }
                // 之后的文件，去掉前六位
                else {
                    while (fileInputStream.read(mByte) != -1) {

                        fileOutputStream.write(mByte, 6, length - 6);
                    }
                }
            }

        } catch (Exception e) {
            // 这里捕获流的IO异常，万一系统错误需要提示用户
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.flush();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 删除已录的录音片段
        for (int i = 0; i < mList.size(); i++) {
            File file = new File(mList.get(i));
            if (file.exists()) {
                file.delete();
            }
        }

    }

}
