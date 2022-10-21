package com.anurag.eduventure.Models;

public class ModelClassroom {

    private String subjectName, className, classCode, timestamp, uid;

    public ModelClassroom() {
    }

    public ModelClassroom(String subjectName, String className, String classCode, String timestamp, String uid) {
        this.subjectName = subjectName;
        this.className = className;
        this.classCode = classCode;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
