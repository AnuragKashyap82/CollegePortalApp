package com.anurag.eduventure.Models;

public class ModelAssignment {

    private String assignmentId, classCode, assignmentName, fullMarks, dueDate, timestamp, uid, url;

    public ModelAssignment() {
    }

    public ModelAssignment(String assignmentId, String classCode, String assignmentName, String fullMarks, String dueDate, String timestamp, String uid, String url) {
        this.assignmentId = assignmentId;
        this.classCode = classCode;
        this.assignmentName = assignmentName;
        this.fullMarks = fullMarks;
        this.dueDate = dueDate;
        this.timestamp = timestamp;
        this.uid = uid;
        this.url = url;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getFullMarks() {
        return fullMarks;
    }

    public void setFullMarks(String fullMarks) {
        this.fullMarks = fullMarks;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}