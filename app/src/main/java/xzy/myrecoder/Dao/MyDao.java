package xzy.myrecoder.Dao;

import java.util.List;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.Model.RecoderItem;
import xzy.myrecoder.Tool.FileTool;
import xzy.myrecoder.Tool.TimeTool;
import xzy.myrecoder.View.Activity.MainActivity;

public class MyDao {
    private static RecoderItemDao recoderItemDao = GreenDaoHelper.getDaoSession().getRecoderItemDao();

    /*
           获取音频名称，添加至数据库
     */
    public static void defaultInsert(String itemname, boolean flag) {
        String date = TimeTool.getDate() + " " + TimeTool.getTime();
        String size = FileTool.getFileSize(itemname);
        String length = FileTool.getFileLength(itemname);

        RecoderItem recoderItem = new RecoderItem(null, itemname, size, length, date, flag);
        recoderItemDao.insert(recoderItem);
    }

    public static void initDB()
    {
        List<RecoderItem> recoderItems = recoderItemDao.loadAll();
        String[] amrFile = FileTool.existAmr();
        if (recoderItems.size()==0&&amrFile.length>0)
        {
            for(String itemname:amrFile)
            defaultInsert(itemname,false);
        }
    }

}
