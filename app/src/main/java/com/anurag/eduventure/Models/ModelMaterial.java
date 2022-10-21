package com.anurag.eduventure.Models;

public class ModelMaterial {

    private String materialId, subjectName, topicName, branch, semester, timestamp, url;

    public ModelMaterial() {
    }

    public ModelMaterial(String materialId, String subjectName, String topicName, String branch, String semester, String timestamp, String url) {
        this.materialId = materialId;
        this.subjectName = subjectName;
        this.topicName = topicName;
        this.branch = branch;
        this.semester = semester;
        this.timestamp = timestamp;
        this.url = url;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
