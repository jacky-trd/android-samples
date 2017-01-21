package com.jikexueyuan.cloudnote;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jikexueyuan.cloudnote.db.dao.DaoMaster;
import com.jikexueyuan.cloudnote.db.dao.DaoSession;
import com.jikexueyuan.cloudnote.db.dao.NoteEntityDao;

import cn.bmob.v3.Bmob;

/**
 * App类，用于保存全局变量
 */
public class CloudNoteApp extends Application {
    private static DaoSession mDaoSession;
    private static SQLiteDatabase mDb;
    private static DaoMaster mDaoMaster;
    private static DaoMaster.DevOpenHelper mHelper;
    private static NoteEntityDao mNoteEntityDao;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Bmob.initialize(this,Constants.APPID);
        mContext = getApplicationContext();
        setupDatabase();
    }

    private void setupDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this,Constants.DB_NAME,null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
        mNoteEntityDao = mDaoSession.getNoteEntityDao();
    }

    public static Context getContext(){
        return mContext;
    }

    public static SQLiteDatabase getDb() {
        return mDb;
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static NoteEntityDao getNoteEntityDao() {
        return mNoteEntityDao;
    }
}
