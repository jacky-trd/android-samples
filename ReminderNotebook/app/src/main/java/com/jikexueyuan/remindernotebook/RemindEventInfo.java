package com.jikexueyuan.remindernotebook;

/**
 * 该类对新建的事件进行了封装，包括事件提醒时间和事件内容，以及闹铃id
 */
public class RemindEventInfo {
    private int eventTime;
    private String eventContent;
    private int alarmID;

    public RemindEventInfo(int time,String content,int id){
        eventTime = time;
        eventContent = content;
        alarmID = id;
    }

    public int getEventTime(){
        return eventTime;
    }

    public void setEventTime(int time){
        eventTime = time;
    }

    public String getEventContent(){
        return eventContent;
    }

    public void setEventContent(String content){
        eventContent = content;
    }

    public int getAlarmID(){
        return alarmID;
    }

    public void setAlarmID( int id){
        alarmID = id;
    }
}