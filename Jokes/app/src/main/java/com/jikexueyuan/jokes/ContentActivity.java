package com.jikexueyuan.jokes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 当用户在主Activity上点击一个笑话时，该Activity用于显示笑话内容
 */
public class ContentActivity extends AppCompatActivity {
    //笑话标题
    private TextView tv_title;
    //笑话发布时间
    private TextView tv_time;
    //笑话内容
    private TextView tv_content;
    //数据库助手类
    private DBHelper dbHelper;
    //数据库读取类
    private SQLiteDatabase dbReader;
    //游标
    private Cursor cursor;
    //数据库名称
    private final String DBName = "PostsStorage";
    //数据库版本号
    private final int DBVersion = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        init();
        showContent();
    }

    /*初始化*/
    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_content = (TextView) findViewById(R.id.tv_content);

        dbHelper = new DBHelper(this,DBName,null,DBVersion);
        dbReader = dbHelper.getReadableDatabase();
    }

    /*显示数据库中缓存的笑话具体内容*/
    private void showContent() {
        Intent intent = getIntent();
        int postId = intent.getIntExtra(getString(R.string.id_tag),-1);
        if(postId != -1){
            cursor = dbReader.query(getString(R.string.table_name),new String[]{getString(R.string.date_col),getString(R.string.title_col),getString(R.string.content_col)},"_id=?",
                    new String[]{postId + ""},null,null,null);

            if(cursor.moveToFirst()){
                String title = cursor.getString(cursor.getColumnIndex(getString(R.string.title_col)));
                String time = cursor.getString(cursor.getColumnIndex(getString(R.string.date_col)));
                String content = cursor.getString(cursor.getColumnIndex(getString(R.string.content_col)));

                tv_title.setText(title);
                tv_time.setText(getString(R.string.publish_time) + time);
                tv_content.setText(content);
            }
        }
        releaseResource();
    }

    /*释放资源*/
    private void releaseResource(){
        if( cursor != null){
            cursor.close();
            cursor = null;
        }

        if(dbReader != null){
            dbReader.close();
            dbReader = null;
        }

        if(dbHelper != null){
            dbHelper.close();
            dbHelper = null;
        }
    }

    /*用户回退时销毁窗口*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
