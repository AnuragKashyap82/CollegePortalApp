package com.anurag.eduventure.Models;

public class ModelFav {

    String materialId, subjectName, topicName, branch, semester, url;
    long viewsCount, downloadsCount, timestamp;
    boolean favorite;

    public ModelFav() {
    }

    public ModelFav(String materialId, String subjectName, String topicName, String branch, String semester, String url, long viewsCount, long downloadsCount, long timestamp, boolean favorite) {
        this.materialId = materialId;
        this.subjectName = subjectName;
        this.topicName = topicName;
        this.branch = branch;
        this.semester = semester;
        this.url = url;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.timestamp = timestamp;
        this.favorite = favorite;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}