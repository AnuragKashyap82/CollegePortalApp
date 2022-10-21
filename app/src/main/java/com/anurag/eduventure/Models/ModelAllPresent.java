package com.anurag.eduventure.Models;

public class ModelAllPresent {

    String  name, branch, semester, uniqueId, date;

    public ModelAllPresent() {
    }

    public ModelAllPresent(String name, String branch, String semester, String uniqueId, String date) {
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.uniqueId = uniqueId;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
