package com.jikexueyuan.playsoundandlrc;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 任务十二作业一：同步播放歌词
 *
 * 代码说明：
 * 1，实体类LrcLineEntity对应Lrc文件中的一行歌词。
 * 2，歌词列表ArrayList<LrcLineEntity> 对应所有歌词的集合。
 */
public class MainActivity extends AppCompatActivity {

    //显示歌词的文本框
    private TextView lrcShow;
    //歌词列表
    private ArrayList<LrcLineEntity> lrcLineList;
    //当前应该显示的歌词序号
    private int currentLrcIndex = 0;
    //播放器
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化
        init();

        //开始播放歌曲
        mPlayer.start();

        //新开一个线程处理歌词同步
        new Thread(new runable()).start();
    }

    /*初始化*/
    private void init(){
        lrcShow = (TextView) findViewById(R.id.showLRC);
        mPlayer = MediaPlayer.create(this,R.raw.nianlun);
        lrcLineList = new ArrayList<LrcLineEntity>();

        //解析Lrc文件，存储在lrcLineList歌词列表中
        parseLrc();
    }

    /*解析Lrc文件，存储在lrcLineList歌词列表中*/
    private void parseLrc(){
        InputStreamReader inReader = new InputStreamReader(getResources().openRawResource(R.raw.nianlun_lrc));
        BufferedReader bufReader = new BufferedReader(inReader);

        String lrcLine = "";
        try {
            while (null != (lrcLine = bufReader.readLine())){
                //去除掉时间中括号的左半部分
                lrcLine = lrcLine.replace("[","");
                //通过时间中括号的右半部分将时间和歌词分开
                String[] splitedLine = lrcLine.split("\\]");
                if(splitedLine.length>1){
                    LrcLineEntity entity = new LrcLineEntity();
                    //存储该行时间
                    entity.setLrcLineTime(calculateLrcTime(splitedLine[0]));
                    //存储该行歌词
                    entity.setLrcLineStr(splitedLine[1]);
                    //存储歌词实体
                    lrcLineList.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*计算每行歌词对应时间的毫秒数*/
    private int calculateLrcTime(String strTime){
        //先将文件中的“.”替换成“：”，然后通过“：”将各段时间分开
        strTime = strTime.replace(".",":");
        String[] splitedTimeStr = strTime.split("\\:");

        int minute = Integer.parseInt(splitedTimeStr[0]);
        int second = Integer.parseInt(splitedTimeStr[1]);
        int mSecond = Integer.parseInt(splitedTimeStr[2]);

        //计算毫秒数
        int currentTime = minute*60*1000 + second*1000 + mSecond*10;
        return currentTime;
    }

    /*运行在单独的线程中，同步歌词*/
    private class runable implements Runnable{
        @Override
        public void run() {
            while (true){
                if(mPlayer.isPlaying()){
                    //计算当前的歌词行序号
                    calculateLrcIndex();
                    //获得当前歌词
                    String currentLrcLine = lrcLineList.get(currentLrcIndex).getLrcLineStr();

                    //通过handler修改UI
                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putString("lrcLine",currentLrcLine);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        }
    }

    /*计算当前的歌词行序号*/
    private void calculateLrcIndex(){
        int currentTime = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();

        if(currentTime < duration){
            for( int i = currentLrcIndex;i<lrcLineList.size();i++){
                if(i<lrcLineList.size()-1){
                    //第一行的情况
                    if((i==0)&&(currentTime<lrcLineList.get(i).getLrcLineTime())){
                        currentLrcIndex = i;
                    }

                    //不是第一行和最后一行的情况
                    if((currentTime>lrcLineList.get(i).getLrcLineTime())&&(currentTime<lrcLineList.get(i+1).getLrcLineTime())){
                        currentLrcIndex = i;
                    }
                }

                //最后一行的情况
                if((i==lrcLineList.size()-1)&&(currentTime>lrcLineList.get(i).getLrcLineTime())){
                    currentLrcIndex = i;
                }
            }
        }
    }

    /*通过handler修改UI*/
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            lrcShow.setText(msg.getData().getString("lrcLine"));
        }
    };
}
