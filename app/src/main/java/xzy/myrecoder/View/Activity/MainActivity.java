package xzy.myrecoder.View.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xzy.myrecoder.Dao.MyDao;
import xzy.myrecoder.R;
import xzy.myrecoder.Tool.FileTool;
import xzy.myrecoder.Tool.PermissionTool;
import xzy.myrecoder.Tool.RecoderTool;
import xzy.myrecoder.Tool.TimeTool;

import static android.app.Notification.VISIBILITY_PUBLIC;


public class MainActivity extends AppCompatActivity {

    private Button playBtn;
    private Button stopInterimBtn;
    private Button stopBtn;
    private Button fileBtn;
    private Button flagBtn;
    private RecoderTool recoderTool = new RecoderTool();
    private TextView timeTextView;

    private Animation animation;
    private Animation transAnimationone;
    private Animation transAnimationtwo;
    private ImageView musicPlayerImg;
    private ImageView transparentImgone;
    private ImageView transparentImgTwo;

    private boolean isStoping = true;
    private long firstTime = 0;
    private boolean isRecording = false; //判断是否处于录音状态，true时包括开始及暂停状态，false表示停止录音 用于返回键判断

    private int timer = 0;  //计时器标志位，0表示暂停 1表示开始 2 表示停止
    private boolean flag = false; //收藏标志位
    private boolean not_lessthan_1s = false;//避免录音小于1s的标志位

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    /*
     初始化
    */
    public void init() {
        stopInterimBtn = findViewById(R.id.stopInterimbtn);
        stopBtn = findViewById(R.id.stopbtn);
        playBtn = findViewById(R.id.playbtn);
        fileBtn = findViewById(R.id.filebtn);
        flagBtn = findViewById(R.id.flagbtn);
        musicPlayerImg = findViewById(R.id.musicplayer);
        transparentImgone = findViewById(R.id.transparentone);
        transparentImgTwo = findViewById(R.id.transparenttwo);
        timeTextView = findViewById(R.id.timeTextView);

        fileBtn.setEnabled(true);
        flagBtn.setAlpha(0);
        flagBtn.setEnabled(false);
        stopBtn.setEnabled(false);
        stopBtn.setAlpha((float) 0.5);


        //禁止横屏操作
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //动态申请权限
        PermissionTool.applypermission(getApplicationContext(), MainActivity.this);
        //默认在SD根目录创建资源文件夹recoreder_res
        FileTool.createFile();

        setListener();

        MyDao.initDB();
    }


    @Override
    //重写onKeyUp方法，没有处于录音状态时，双击back键退出程序
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!isRecording) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK://点击返回键
                    long secondTime = System.currentTimeMillis();//以毫秒为单位
                    if (secondTime - firstTime > 2000) {
                        Toast.makeText(this, "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
                        firstTime = secondTime;
                    } else {
                        NotificationManager NotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationManager.cancel(1); //通过ID 关闭通知

                        finish();
                        System.exit(0);
                    }
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    //正在录音时，点击back键直接返回桌面 不退出程序
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isRecording) {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /*
    给各个按钮设置监听器
     */
    public void setListener() {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                playBtn.setVisibility(View.INVISIBLE);
                stopInterimBtn.setVisibility(View.VISIBLE);
                stopBtn.setEnabled(true);
                stopBtn.setAlpha((float) 1);
                fileBtn.setEnabled(false);
                fileBtn.setAlpha(0);
                flagBtn.setEnabled(true);
                flagBtn.setAlpha((float) 0.5);
                flag = false;

                //旋转和透明的动画
                rotateAnimation();
                transparentAnimation();

                //开启录音
                recoderTool.startRecord();

                isStoping = false;
                isRecording = true;
                if (timer == 2) currentSecond = 0;
                timer = 1;
                timeRunable.run();
                sendFlagOngoingEventNotification();//向通知栏发送通知
            }
        });

        stopInterimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                playBtn.setVisibility(View.VISIBLE);
                stopInterimBtn.setVisibility(View.INVISIBLE);
                closeAnimation();
                timer = 0;
                //暂停录音
                recoderTool.pauseRecord();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                final String itemname = FileTool.getDefaultName();

                //小于1s不能停止录音
                if (not_lessthan_1s==false)
                {
                   Toast.makeText(MainActivity.this,"录音不能小于1s！",Toast.LENGTH_SHORT).show();
                   return;
                }
                else
                {
                    recoderTool.stopRecord();
                    not_lessthan_1s=false;
                }



                closeAnimation();
                isRecording = false;
                timer = 2;
                currentSecond = 0;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle("是否保存？");
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playBtn.setVisibility(View.VISIBLE);
                        stopInterimBtn.setVisibility(View.INVISIBLE);

                        //将生成的文件信息存入数据库
                        MyDao.defaultInsert(itemname, flag);
                        Toast.makeText(MainActivity.this, "录音文件已保存至/recoreder_res文件夹下", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playBtn.setVisibility(View.VISIBLE);
                        stopInterimBtn.setVisibility(View.INVISIBLE);

                        //将已录的音频的文件删除
                        FileTool.deleteFile(itemname);
                    }
                });
                builder.create().show();


                NotificationManager NotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationManager.cancel(1); //通过ID 关闭通知


                stopBtn.setAlpha((float) 0.5);
                stopBtn.setEnabled(false);
                fileBtn.setEnabled(true);
                fileBtn.setAlpha(1);
                flagBtn.setEnabled(false);
                flagBtn.setAlpha(0);

            }
        });

            /*
            fileBtn的功能实现
            跳转至ItemListView
             */
        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAnimation();
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);

            }
        });

        /*flagBtn的功能实现
        添加或取消收藏
         */
        flagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = !flag;
                if (flag==true)
                {
                    flagBtn.setAlpha(1);
                }
                else
                 {
                    flagBtn.setAlpha((float) 0.5);
                }
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        });

    }


    //生成顶端菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    /*
    位于菜单栏的About
    跳转至AboutActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        return true;
    }

    //开启app时动态获取权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, permissions[i] + "已授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, permissions[i] + "拒绝授权", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //旋转
    public void rotateAnimation() {
        //动画
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        musicPlayerImg.startAnimation(animation);
    }

    //虚化图片移动
    public void transparentAnimation() {
        isStoping = false;
        transAnimationone = AnimationUtils.loadAnimation(this, R.anim.transparent_animation);
        transAnimationtwo = AnimationUtils.loadAnimation(this, R.anim.transparenttwo_animation);
        transparentImgone.startAnimation(transAnimationone);
        transparentImgTwo.startAnimation(transAnimationtwo);
        transAnimationone.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isStoping == false)
                    transparentImgone.startAnimation(transAnimationone);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        transAnimationtwo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isStoping == false)
                    transparentImgTwo.startAnimation(transAnimationtwo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //关闭动画
    public void closeAnimation() {
        if (musicPlayerImg.getAnimation() != null && transparentImgone.getAnimation() != null && transparentImgTwo.getAnimation() != null) {
            musicPlayerImg.clearAnimation();//停止旋转
            transparentImgone.clearAnimation();
            transparentImgTwo.clearAnimation();
            isStoping = true;
        }
    }

    //向通知栏发送正在录音通知
    private void sendFlagOngoingEventNotification()
    {
        //   Intent intent = new Intent(MainActivity.this, MainActivity.class);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this, MainActivity.class));//用ComponentName得到class对象
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//如果没开启activity就开启acitvity  否则加载原来的activity


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //获取通知服务

        if (Build.VERSION.SDK_INT >= 26) {
            //当sdk版本大于26
            String id = "channel_1";
            String description = "正在录音";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, description, importance);

            //channel.enableVibration(true);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(MainActivity.this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.microphone)
                    .setContentTitle("正在录音…")
                    .setContentIntent(pendingIntent)
                    //.setFullScreenIntent(pendingIntent, false)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;  //点击后不消失
            manager.notify(1, notification);
        } else {
            //当sdk版本小于26
            Notification notification = new NotificationCompat.Builder(MainActivity.this)
                    .setContentTitle("正在录音")
                    .setContentText("")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .build();
            manager.notify(1, notification);
        }
    }


    //计时器
    private Handler mhandle = new Handler();
    private long currentSecond = 0; //当前毫秒数
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            if (timer == 2)
            {
                currentSecond = 0;
            }

            timeTextView.setText(TimeTool.getFormatHMS(currentSecond));

            //当录音时间大于1s时，才能结束录音
            if (currentSecond>1000)
            {
                not_lessthan_1s =true;
            }
            if (timer == 1) {
                //每隔一秒运行一次
                mhandle.postDelayed(this, 100);
            }
            currentSecond = currentSecond + 100;
        }
    };



}


