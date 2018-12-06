package xzy.myrecoder.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTool
{
    /*
    获取日期
     */
    public static String getDate()
    {
        Calendar c = Calendar.getInstance();
       int year = c.get(Calendar.YEAR);
       int month = c.get(Calendar.MONTH);
       int day = c.get(Calendar.DAY_OF_MONTH);
        return year+" "+month+" "+day;
    }

    /*
    获取当前时间
     */
    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String time = formatter.format(curDate);
        return time;
    }

    //将传入的time（毫秒）转换为时分秒的格式
    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return String.format("%02d:%02d:%02d", h, m, s);
    }

}
