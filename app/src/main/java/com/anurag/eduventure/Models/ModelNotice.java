package com.anurag.eduventure.Models;

public class ModelNotice {

    private String uid, NoticeId, title, number, url, timestamp;

    public ModelNotice() {
    }

    public ModelNotice(String uid, String noticeId, String title, String number, String url, String timestamp) {
        this.uid = uid;
        NoticeId = noticeId;
        this.title = title;
        this.number = number;
        this.url = url;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNoticeId() {
        return NoticeId;
    }

    public void setNoticeId(String noticeId) {
        NoticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}