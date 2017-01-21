package com.jikexueyuan.remindernotebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类为工具类，负责数据库SQLiteDatabase相关操作
 */
public class DBOperationHelper extends SQLiteOpenHelper {

    private Context context;

    DBOperationHelper(Context context) {
        super(context,context.getString(R.string.table_name),null,context.getResources().getInteger(R.integer.dbVersion));
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       sqLiteDatabase.execSQL("CREATE TABLE " + context.getString(R.string.table_name) + "(" +
               "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                context.getString(R.string.event_time) + " INTEGER," +
               context.getString(R.string.event_content) +" Text," +
               context.getString(R.string.alarm_id)+" INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //插入数据
    public void insertEvent(RemindEventInfo event){
        SQLiteDatabase dbWriter = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(context.getString(R.string.event_time),event.getEventTime());
        cv.put(context.getString(R.string.event_content),event.getEventContent());
        cv.put(context.getString(R.string.alarm_id),event.getAlarmID());
        dbWriter.insert(context.getString(R.string.table_name),null,cv);
        dbWriter.close();
    }

    //返回所有数据
    public List<RemindEventInfo> getEvents(){
        List<RemindEventInfo> lists = new ArrayList<RemindEventInfo>();

        SQLiteDatabase dbReader = this.getReadableDatabase();
        Cursor cursor = dbReader.query(context.getString(R.string.table_name),null,null,null,null,null,null);

        while (cursor.moveToNext()){
            int time = cursor.getInt(cursor.getColumnIndex(context.getString(R.string.event_time)));
            String content = cursor.getString(cursor.getColumnIndex(context.getString(R.string.event_content)));
            int alarmId = cursor.getInt(cursor.getColumnIndex(context.getString(R.string.alarm_id)));

            RemindEventInfo event = new RemindEventInfo(time,content,alarmId);
            lists.add(event);
        }
        dbReader.close();
        return lists;
    }

    //删除数据
    public int deleteEvent(int position){
        SQLiteDatabase dbReader = this.getReadableDatabase();
        Cursor cursor = dbReader.query(context.getString(R.string.table_name),null,null,null,null,null,null);
        cursor.moveToPosition(position);
        int itemId = cursor.getInt(cursor.getColumnIndex(context.getString(R.string.primary_id)));
        int alarmID = cursor.getInt(cursor.getColumnIndex(context.getString(R.string.alarm_id)));
        dbReader.close();

        SQLiteDatabase dbWriter = this.getWritableDatabase();
        dbWriter.delete(context.getString(R.string.table_name),context.getString(R.string.delete_clause),new String[]{itemId+""});
        dbWriter.close();

        return alarmID;
    }
}