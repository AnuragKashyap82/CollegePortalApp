package com.anurag.eduventure.Models;

public class ModelLecture {

    String lectureText, uid,  lectureId, lectureVideo;

    public ModelLecture() {
    }

    public ModelLecture(String lectureText, String uid, String lectureId, String lectureVideo) {
        this.lectureText = lectureText;
        this.uid = uid;
        this.lectureId = lectureId;
        this.lectureVideo = lectureVideo;
    }

    public String getLectureText() {
        return lectureText;
    }

    public void setLectureText(String lectureText) {
        this.lectureText = lectureText;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getLectureVideo() {
        return lectureVideo;
    }

    public void setLectureVideo(String lectureVideo) {
        this.lectureVideo = lectureVideo;
    }
}
