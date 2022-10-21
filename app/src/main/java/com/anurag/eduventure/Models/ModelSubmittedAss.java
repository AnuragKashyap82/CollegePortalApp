package com.anurag.eduventure.Models;

public class ModelSubmittedAss {

    private String assignmentId, assignmentName, classCode, dueDate, fullMarks, timestamp, uid, url, marksObtained;
    private boolean isSubmitted;

    public ModelSubmittedAss() {
    }

    public ModelSubmittedAss(String assignmentId, String assignmentName, String classCode, String dueDate, String fullMarks, String timestamp, String uid, String url, String marksObtained, boolean isSubmitted) {
        this.assignmentId = assignmentId;
        this.assignmentName = assignmentName;
        this.classCode = classCode;
        this.dueDate = dueDate;
        this.fullMarks = fullMarks;
        this.timestamp = timestamp;
        this.uid = uid;
        this.url = url;
        this.marksObtained = marksObtained;
        this.isSubmitted = isSubmitted;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getFullMarks() {
        return fullMarks;
    }

    public void setFullMarks(String fullMarks) {
        this.fullMarks = fullMarks;
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

    public String getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(String marksObtained) {
        this.marksObtained = marksObtained;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }
}