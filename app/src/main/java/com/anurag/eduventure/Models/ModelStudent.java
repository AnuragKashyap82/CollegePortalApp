package com.anurag.eduventure.Models;

public class ModelStudent {

    private String name, Semester, branch, uniqueId;

    public ModelStudent() {

    }

    public ModelStudent(String name, String semester, String branch, String uniqueId) {
        this.name = name;
        Semester = semester;
        this.branch = branch;
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return Semester;
    }

    public void setSemester(String semester) {
        Semester = semester;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
