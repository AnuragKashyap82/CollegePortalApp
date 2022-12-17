package com.anurag.eduventure.Models;

public class ModelAppliedBooks {

    String timestamp, appliedDate, uid;

    public ModelAppliedBooks() {
    }

    public ModelAppliedBooks(String timestamp, String appliedDate, String uid) {
        this.timestamp = timestamp;
        this.appliedDate = appliedDate;
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
