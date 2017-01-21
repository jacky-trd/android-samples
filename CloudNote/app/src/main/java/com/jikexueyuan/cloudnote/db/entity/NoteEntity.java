package com.jikexueyuan.cloudnote.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import cn.bmob.v3.BmobUser;

/**
 * 笔记的dao对象类，用于自动生成对应的DaoMaster,DaoSession和NoteEntityDao
 */
@Entity
public class NoteEntity implements Parcelable {

    //笔记的本地数据库Id，自增1
    @Id(autoincrement = true)
    private long id;

    //笔记标题
    @Property(nameInDb = "TITLE")
    private String title;

    //笔记概要
    @Property(nameInDb = "SUMMARY")
    private String summary;

    //笔记内容
    @Property(nameInDb = "CONTENT")
    private String content;

    //笔记图片
    @Property(nameInDb = "IMAGE")
    private String image;

    //笔记视频
    @Property(nameInDb = "VIDEO")
    private String video;

    //笔记日期
    @Property(nameInDb = "DATE")
    private String date;

    //笔记Bmob Id
    @Property(nameInDb = "OBJ_ID")
    private String objId;

    //是否和bmob同步过的flag
    @Property(nameInDb = "IS_SYNC")
    private boolean isSync;

    public boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    public String getObjId() {
        return this.objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVideo() {
        return this.video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Generated(hash = 290996040)
    public NoteEntity(long id, String title, String summary, String content,
            String image, String video, String date, String objId, boolean isSync) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.image = image;
        this.video = video;
        this.date = date;
        this.objId = objId;
        this.isSync = isSync;
    }

    @Generated(hash = 734234824)
    public NoteEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable接口的序列化方法
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.summary);
        parcel.writeString(this.content);
        parcel.writeString(this.image);
        parcel.writeString(this.video);
        parcel.writeString(this.date);
        parcel.writeString(this.objId);
        parcel.writeByte(this.isSync ? (byte) 1 : (byte) 0);
    }

    //Parcelable接口的反序列化方法
    public NoteEntity (Parcel parcel){
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.summary = parcel.readString();
        this.content = parcel.readString();
        this.image = parcel.readString();
        this.video = parcel.readString();
        this.date = parcel.readString();
        this.objId = parcel.readString();
        this.isSync = parcel.readByte() != 0;
    }

    @Transient
    public static final Parcelable.Creator<NoteEntity> CREATOR = new Parcelable.Creator<NoteEntity>(){

        @Override
        public NoteEntity createFromParcel(Parcel parcel) {
            return new NoteEntity(parcel);
        }

        @Override
        public NoteEntity[] newArray(int i) {
            return new NoteEntity[i];
        }
    };

    //dao笔记对象转化为bmob笔记对象
    public Note convert2Bmob(){
        Note bmobNote = new Note();
        bmobNote.setTitle(getTitle());
        bmobNote.setContent(getContent());
        bmobNote.setSummary(getSummary());
        bmobNote.setImage(getImage());
        bmobNote.setVideo(getVideo());
        bmobNote.setDbId(getId());
        bmobNote.setUserObjId(BmobUser.getCurrentUser().getUsername());
        return bmobNote;
    }
}
