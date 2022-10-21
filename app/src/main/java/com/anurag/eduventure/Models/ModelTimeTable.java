package com.anurag.eduventure.Models;

public class ModelTimeTable {

    private String subName, teacherName, startTime, endTime, day, ongoingTopic, percentSylComp;

    public ModelTimeTable() {
    }

    public ModelTimeTable(String subName, String teacherName, String startTime, String endTime, String day, String ongoingTopic, String percentSylComp) {
        this.subName = subName;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.ongoingTopic = ongoingTopic;
        this.percentSylComp = percentSylComp;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOngoingTopic() {
        return ongoingTopic;
    }

    public void setOngoingTopic(String ongoingTopic) {
        this.ongoingTopic = ongoingTopic;
    }

    public String getPercentSylComp() {
        return percentSylComp;
    }

    public void setPercentSylComp(String percentSylComp) {
        this.percentSylComp = percentSylComp;
    }
}
