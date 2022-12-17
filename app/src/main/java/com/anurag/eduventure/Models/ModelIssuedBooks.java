package com.anurag.eduventure.Models;

public class ModelIssuedBooks {

    String timestamp, issueDate, uid;

    public ModelIssuedBooks() {
    }

    public ModelIssuedBooks(String timestamp, String issueDate, String uid) {
        this.timestamp = timestamp;
        this.issueDate = issueDate;
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
