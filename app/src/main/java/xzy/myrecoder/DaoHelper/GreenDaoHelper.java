package xzy.myrecoder.DaoHelper;


import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import xzy.GreebDao.DaoMaster;
import xzy.GreebDao.DaoSession;

public class GreenDaoHelper extends Application {
    public static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase db;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    public static GreenDaoHelper instances;

    public void onCreate()
    {
        super.onCreate();
        instances = this;
        initDatabase();
    }

    public static GreenDaoHelper getInstances()
    {return instances;}

    /**
     * 初始化greenDao
     */
    public static void initDatabase() {

        mHelper = new DaoMaster.DevOpenHelper(instances, "Recoder-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
    public static SQLiteDatabase getDb() {
        return db;
    }


}
