package com.jikexueyuan.jokes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库助手类，用于笑话的数据库存取操作
 */
public class DBHelper extends SQLiteOpenHelper
{
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE posts(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pages INTEGER," +
                "ID INTEGER," +
                "date TEXT," +
                "modified TEXT," +
                "title TEXT," +
                "content TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
