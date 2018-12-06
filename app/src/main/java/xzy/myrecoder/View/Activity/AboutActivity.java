package xzy.myrecoder.View.Activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import xzy.myrecoder.R;

public class AboutActivity extends AppCompatActivity {


    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        relativeLayout= (RelativeLayout) findViewById(R.id.relativeLayout);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //.setImage(R.drawable.recorder)//图片
                .setDescription("非常感谢您使用我们的App\n版权归CLX团队所有\n\n" )//介绍
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("联系方式")
                .addEmail(" 123@qq.com")//邮箱

                .addPlayStore("com.example.abouttest")
                .addGitHub("www.github.com")//github
                .create();
        relativeLayout.addView(aboutPage);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

}
