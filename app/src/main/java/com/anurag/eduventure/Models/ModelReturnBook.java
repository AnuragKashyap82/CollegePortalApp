package com.anurag.eduventure.Models;

public class ModelReturnBook {

    String uid, timestamp, returnDate;

    public ModelReturnBook() {
    }

    public ModelReturnBook(String uid, String timestamp, String returnDate) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.returnDate = returnDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
