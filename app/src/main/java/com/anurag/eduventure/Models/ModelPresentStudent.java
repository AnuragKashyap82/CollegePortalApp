package com.anurag.eduventure.Models;

public class ModelPresentStudent {

    String  name, branch, semester, uniqueId;

    public ModelPresentStudent() {
    }

    public ModelPresentStudent(String name, String branch, String semester, String uniqueId) {
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
