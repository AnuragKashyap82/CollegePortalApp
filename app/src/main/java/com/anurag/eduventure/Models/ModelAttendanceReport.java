package com.anurag.eduventure.Models;

public class ModelAttendanceReport {

    String Attendance, date, uid;

    public ModelAttendanceReport() {
    }

    public ModelAttendanceReport(String attendance, String date, String uid) {
        Attendance = attendance;
        this.date = date;
        this.uid = uid;
    }

    public String getAttendance() {
        return Attendance;
    }

    public void setAttendance(String attendance) {
        Attendance = attendance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
