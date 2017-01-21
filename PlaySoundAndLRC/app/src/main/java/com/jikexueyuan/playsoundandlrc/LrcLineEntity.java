package com.jikexueyuan.playsoundandlrc;

/**
 * 实体类，对应LRC歌词中的一行歌词
 */
public class LrcLineEntity {
    private String lrcLineStr;
    private int lrcLineTime;

    public String getLrcLineStr() {
        return lrcLineStr;
    }

    public void setLrcLineStr(String lrcLineStr) {
        this.lrcLineStr = lrcLineStr;
    }

    public int getLrcLineTime() {
        return lrcLineTime;
    }

    public void setLrcLineTime(int lrcLineTime) {
        this.lrcLineTime = lrcLineTime;
    }
}