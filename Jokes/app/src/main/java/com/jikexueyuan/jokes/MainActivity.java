package com.jikexueyuan.jokes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务十三作业一：开发一个笑话连连看应用
 * 服务器连接地址：http://myjikexueyuan.applinzi.com/get-jokes.php
 *
 * 代码说明：
 * 1，ContentActivity:用于显示笑话具体内容
 * 2，DBHelper:数据库助手类，用于笑话的本地缓存和读取
 * 3，MainActivity:主Activity，用于显示笑话刷新列表
 * 4，NetworkUtils:网络助手类，用于检查网络状态
 * 5，PullRefreshView:定制的刷新列表控件
 */
public class MainActivity extends AppCompatActivity {

    //刷新列表控件（包括头部和底部）
    private PullRefreshView pullRefreshView;
    //中间的笑话列表
    private ListView jokesList;
    //刷新列表底部
    private View refreshViewFooter;
    //笑话列表适配器
    private SimpleAdapter adapter;
    //存储笑话列表的List
    private List<Map<String,Object>> mapList;
    //数据库助手类
    private DBHelper dbHelper;
    //数据库读写类
    private SQLiteDatabase dbReader, dbWriter;
    //游标
    private Cursor cursor;
    //笑话个数
    private long dataCount;
    //最大页数
    private int maxpage;
    //是否准备好加载的标志
    private boolean readyToLoad = true;
    //每次显示笑话的个数
    private static  final int number = 10;
    //给Handler传递message时的tag
    private static final int SHOW_FOOTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /*初始化*/
    private void init(){
        //初始化各个控件
        pullRefreshView = (PullRefreshView) findViewById(R.id.pull_refresh_view);
        jokesList = (ListView) findViewById(R.id.jokes_list);
        mapList = new ArrayList<>();
        refreshViewFooter = getLayoutInflater().inflate(R.layout.refresh_view_footer, null);
        jokesList.setOnScrollListener(new ScrollListener());

        //数据库初始化
        dbHelper = new DBHelper(this,"PostsStorage",null,1);
        dbReader = dbHelper.getReadableDatabase();
        dbWriter = dbHelper.getWritableDatabase();
        dataCount = getRecordsCount();

        //计算最大页数
        maxpage = (int) (dataCount % number == 0 ? dataCount / number : dataCount / number + 1);

        //显示第一页
        refreshListView(1);

        //绑定listview和adapter
        adapter = new SimpleAdapter(this, mapList, R.layout.list_item, new String[]{"title", "date"}, new int[]{R.id.tvTitle, R.id.tvTime});
        jokesList.addFooterView(refreshViewFooter);
        jokesList.setAdapter(adapter);
        if (dataCount == 0) {
            jokesList.removeFooterView(refreshViewFooter);
        }

        //设置刷新回调函数
        pullRefreshView.setOnRefreshListener(new PullRefreshView.RefreshListener() {
            @Override
            public void onRefresh() {
                //判断是否联网
                if (NetworkUtils.isNetworkConnected(getApplicationContext()) || NetworkUtils.isWifi(getApplicationContext())) {

                    //启用异步线程，来获取网络数据
                    new GetDataTask().execute("http://myjikexueyuan.applinzi.com/get-jokes.php");
                    try {

                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                pullRefreshView.finishRefresh();
            }
        });

        //绑定笑话列表点击函数
        jokesList.setOnItemClickListener(listClickListener);
    }

    /*异步获取笑话数据*/
    class GetDataTask extends AsyncTask<String, Void, StringBuilder> {
        @Override
        protected StringBuilder doInBackground(String... params) {
            StringBuilder strBuilder = new StringBuilder();
            try {
                //URL中数据的读取
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    strBuilder.append(line);
                }

                br.close();
                isr.close();
                is.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strBuilder;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(StringBuilder builder) {
            try {
                //清空数据表
                dbWriter.delete(getString(R.string.table_name), null, null);
                //解析JASON对象，存入数据库
                JSONArray jsonArray = new JSONArray(builder.toString());
                dataCount = jsonArray.length();
                int page = 1;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    //将之按10个每页来分页
                    page = i / number + 1;
                    //将之按10个每页来分页
                    int ID = jo.getInt(getString(R.string.post_id));
                    //发布时间
                    String post_date = jo.getString(getString(R.string.post_date));
                    //修改时间
                    String post_modified = jo.getString(getString(R.string.post_modified));
                    //标题
                    String post_title = jo.getString(getString(R.string.post_title));
                    //正文
                    String post_content = jo.getString(getString(R.string.post_content));

                    //写入数据库
                    DBInsert(page, ID, post_date, post_modified, post_title, post_content);
                }

                maxpage = page;
                mapList.clear();

                refreshListView(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(builder);
        }
    }

    /*将笑话写入数据库*/
    private void DBInsert(Integer page, Integer ID, String date, String modified, String title, String content) {
        ContentValues cv = new ContentValues();
        cv.put(getString(R.string.pages), page);
        cv.put(getString(R.string.db_id), ID);
        cv.put(getString(R.string.db_date), date);
        cv.put(getString(R.string.db_modified), modified);
        cv.put(getString(R.string.db_title), title);
        cv.put(getString(R.string.db_content), content);
        dbWriter.insert(getString(R.string.table_name), null, cv);
    }

    /*获取笑话总个数*/
    public long getRecordsCount() {
        Cursor cursor = dbReader.rawQuery("select count(*)from posts", null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    /*列表滚动监听*/
    private final class ScrollListener implements AbsListView.OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            //加载完毕
            if (i2 == dataCount + 1 && dataCount != 0) {
                jokesList.removeFooterView(refreshViewFooter);
                Toast.makeText(MainActivity.this, R.string.loading_complete, Toast.LENGTH_SHORT).show();
            }

            //加载数据
            int lastItemId = i + i1 - 1;
            if (lastItemId + 1 == i2 && dataCount != 0) {
                if (lastItemId > 0) {
                    int currentPage = lastItemId % number == 0 ? lastItemId / number
                            : lastItemId / number + 1;
                    final int nextPage = currentPage + 1;

                    if (nextPage <= maxpage && readyToLoad) {
                        readyToLoad = false;
                        jokesList.addFooterView(refreshViewFooter);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                handler.sendMessage(handler.obtainMessage(SHOW_FOOTER, nextPage));
                            }
                        }).start();
                    }
                }
            }
        }
    }

    /*Handler操作UI*/
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_FOOTER:
                    refreshListView((Integer) msg.obj);
                    adapter.notifyDataSetChanged();

                    if (jokesList.getFooterViewsCount() > 0) {
                        jokesList.removeFooterView(refreshViewFooter);
                    }
                    readyToLoad = true;
                    break;
                default:
                    break;
            }
        }
    };

    /*显示某页笑话*/
    private void refreshListView(int showPage) {
        cursor = dbReader.rawQuery("SELECT * FROM posts WHERE pages = ?", new String[]{String.valueOf(showPage)});
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getCount(); ++i) {
                cursor.moveToPosition(i);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(getString(R.string.title_col), cursor.getString(cursor.getColumnIndex(getString(R.string.title_col))));
                map.put(getString(R.string.date_col), cursor.getString(cursor.getColumnIndex(getString(R.string.date_col))));
                map.put("sql_id", cursor.getInt(cursor.getColumnIndex(getString(R.string.id_col))));
                mapList.add(map);
            }
        }
    }

    /*点击某个笑话，显示笑话具体内容*/
    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                ListView listView = (ListView) parent;
                HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
                int itemId = (int) data.get("sql_id");
                if (itemId > 0) {
                    Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                    intent.putExtra((getString(R.string.id_tag)), itemId);
                    startActivity(intent);
                }
            } catch (Exception e) {
                //捕获异常，为防止点击最后footerView的情况
                e.printStackTrace();
            }
        }
    };

    /*关闭数据库与光标*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null){
            cursor.close();
            cursor = null;
        }

        if(dbReader != null){
            dbReader.close();
            dbReader = null;
        }

        if(dbWriter != null){
            dbWriter.close();
            dbWriter = null;
        }

        if (dbHelper != null){
            dbHelper.close();
            dbHelper = null;
        }
    }
}
