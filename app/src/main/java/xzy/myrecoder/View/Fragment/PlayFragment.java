package xzy.myrecoder.View.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;

import android.util.DisplayMetrics;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.R;
import xzy.myrecoder.Tool.FileTool;
import xzy.myrecoder.Tool.MediaPlayerTool;
import xzy.myrecoder.Tool.ShareTool;
import xzy.myrecoder.View.Activity.ListViewActivity;


public class PlayFragment extends DialogFragment {
    private Button mediaplayer_moreBtn;
    private Button mediaplayer_playBtn;
    private Button mediaplayer_stopInterimBtn;
    private SeekBar seekBar;
    private TextView fileLengthTextView;
    private TextView currentTimeTextView;
    private TextView fileNameTextView;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private RecoderItemDao recoderItemDao = GreenDaoHelper.getDaoSession().getRecoderItemDao();
    private MediaPlayerTool mediaPlayerTool = new MediaPlayerTool();

    private String itemname;
    private boolean dismiss = false;//退出fragment的标志位


    private Handler handler;
    private Thread thread;
    //记录播放位置
    private int time;
    //记录是否暂停
    private boolean flage = false;
    //记录进度条是否被拖拽
    private boolean isChanging = false;
    private boolean exitFragment = true;
    //stores minutes and seconds of the length of the file.
    long minutes = 0;
    long seconds = 0;
    SeekBarThread seekBarThread = new SeekBarThread();
    //获取MediaPlayer对象进行管理

    public PlayFragment() {
        // Required empty public constructor
    }


    public void getMediaDuration() {
        minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
        seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration())
                - TimeUnit.MINUTES.toSeconds(minutes);
        fileLengthTextView.setText(String.format("%02d:%02d", minutes, Math.abs(seconds - 1)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);


        Bundle bundle = getArguments();
        final Long id = bundle.getLong("itemnum");
        itemname = recoderItemDao.load(id).getItemName();

        fileNameTextView = (TextView) view.findViewById(R.id.fileNameTextView);
        currentTimeTextView = (TextView) view.findViewById(R.id.currentTimeTextView);
        fileLengthTextView = (TextView) view.findViewById(R.id.fileLengthTextView);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mediaplayer_playBtn = (Button) view.findViewById(R.id.mediaplayer_playBtn);
        mediaplayer_stopInterimBtn = (Button) view.findViewById(R.id.mediaplayer_stopInterimBtn);
        mediaplayer_moreBtn = (Button) view.findViewById(R.id.mediaplayer_more);

        //设置文件名
        fileNameTextView.setText(itemname);

        /*
        setListener
         */
        //分享和删除菜单的监听
        mediaplayer_moreBtn.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onClick(View v) {

                        PopupMenu pMenu = new PopupMenu(getActivity(), v);
                        //设置PopupMenu对象的布局
                        pMenu.getMenuInflater().inflate(R.menu.mediaplayer_menu_item, pMenu.getMenu());
                        //设置pMenu菜单的单击监听事件
                        pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //当点击时弹出被点击象标题
                                switch (item.getItemId()) {
                                    case R.id.share:
                                        ShareTool.shareAudio(getActivity(), itemname);
                                        break;
                                    case R.id.delete:
                                        FileTool.deleteFile(itemname);
                                        Toast.makeText(getActivity(), "成功删除 " + recoderItemDao.load(id).getItemName() , Toast.LENGTH_SHORT).show();
                                        recoderItemDao.deleteByKey(id);
                                        recoderItemDao.load(id).getItemName();
                                        dismiss=true;
                                        break;
                                    case R.id.saveToDrive:
                                        ShareTool.saveToDrive(getActivity(), 0, itemname);
                                        break;

                                    default:
                                        break;
                                }
                                //MeidaPlayer_fragment.this.dismiss();
                                return true;
                            }
                        });
                        pMenu.show();
                        if(dismiss)
                        {
                            PlayFragment.this.dismiss();
                        }
                    }
                }
        );

        //播放按钮的监听
        mediaplayer_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mediaPlayerTool.initMediaPlayer(mediaPlayer, recoderItemDao.load(id).getItemName(), seekBar);
                getMediaDuration();
                mediaplayer_playBtn.setVisibility(View.INVISIBLE);
                mediaplayer_stopInterimBtn.setVisibility(View.VISIBLE);
                if (!mediaPlayer.isPlaying()) {
                    //如果暂停
                    flage = mediaPlayerTool.listenToStopPlaying(mediaPlayer, flage, time);
                    thread = new Thread(seekBarThread);
                    // 启动线程
                    thread.start();
                }
                //暂停
                mediaplayer_stopInterimBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        mediaPlayerTool.stopPlaying(mediaPlayer);
                        mediaplayer_playBtn.setVisibility(View.VISIBLE);
                        mediaplayer_stopInterimBtn.setVisibility(View.INVISIBLE);
                        time = mediaPlayer.getCurrentPosition();
                        flage = true;
                    }
                });
            }
        });

        //拖拽seekbar检测
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                isChanging = false;
            }
        });

        //播放结束
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayerTool.finishPlaying(mediaPlayer);
                mediaplayer_playBtn.setVisibility(View.VISIBLE);
                mediaplayer_stopInterimBtn.setVisibility(View.INVISIBLE);
                seekBar.setProgress(0);
                currentTimeTextView.setText(String.format("%02d:%02d", 0, 0));
            }
        });


        //设置进度时间文字
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case 10:
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())
                                - TimeUnit.MINUTES.toSeconds(minutes);
                        currentTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
                        break;
                }
            }
        };

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            exitFragment = false;
            mediaPlayer.release();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //改变dialogfragment 宽度占比
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.setCanceledOnTouchOutside(false);
    }


    // 自定义的线程
    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (!isChanging && exitFragment && mediaPlayer.isPlaying()) {
                // 将SeekBar位置设置到当前播放位置
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                Message message = new Message();
                message.arg1 = 10;
                handler.sendMessage(message);
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(1000);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ;
}
