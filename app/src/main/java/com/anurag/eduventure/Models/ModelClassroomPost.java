package com.anurag.eduventure.Models;

public class ModelClassroomPost {

    private String uid, classMsg, classCode, timestamp, url;
    private boolean attachmentExist;

    public ModelClassroomPost() {
    }

    public ModelClassroomPost(String uid, String classMsg, String classCode, String timestamp, String url, boolean attachmentExist) {
        this.uid = uid;
        this.classMsg = classMsg;
        this.classCode = classCode;
        this.timestamp = timestamp;
        this.url = url;
        this.attachmentExist = attachmentExist;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClassMsg() {
        return classMsg;
    }

    public void setClassMsg(String classMsg) {
        this.classMsg = classMsg;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAttachmentExist() {
        return attachmentExist;
    }

    public void setAttachmentExist(boolean attachmentExist) {
        this.attachmentExist = attachmentExist;
    }
}