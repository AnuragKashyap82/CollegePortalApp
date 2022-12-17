package com.anurag.eduventure.Models;

public class ModelAtteStud {

    String  uid, monthYear, presentCount, absentCount, classCode;

    public ModelAtteStud() {
    }

    public ModelAtteStud(String uid, String monthYear, String presentCount, String absentCount, String classCode) {
        this.uid = uid;
        this.monthYear = monthYear;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.classCode = classCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(String presentCount) {
        this.presentCount = presentCount;
    }

    public String getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(String absentCount) {
        this.absentCount = absentCount;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
}
