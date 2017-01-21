package com.jikexueyuan.cloudnote.db.entity;

import cn.bmob.v3.BmobObject;

/**
 * Bmob的笔记对象
 */
public class Note extends BmobObject {

    //笔记标题
    private String title;
    //笔记概要
    private String summary;
    //笔记内容
    private String content;
    //笔记图片
    private String image;
    //笔记视频
    private String video;
    //笔记的bmob id
    private String userObjId;
    //笔记的db id
    private long dbId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUserObjId() {
        return userObjId;
    }

    public void setUserObjId(String userObjId) {
        this.userObjId = userObjId;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    //bmob笔记对象转化为dao笔记对象
    public NoteEntity convert2NoteEntity(){
        NoteEntity entity = new NoteEntity();
        entity.setTitle(getTitle());
        entity.setContent(getContent());
        entity.setSummary(getSummary());
        entity.setImage(getImage());
        entity.setVideo(getVideo());
        entity.setId(getDbId());
        entity.setDate(getUpdatedAt());
        return entity;
    }
}
