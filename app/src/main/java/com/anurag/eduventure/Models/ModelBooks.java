package com.anurag.eduventure.Models;

public class ModelBooks {

    String  subjectName, bookName, authorName, bookId, timestamp;

    public ModelBooks() {
    }

    public ModelBooks(String subjectName, String bookName, String authorName, String bookId, String timestamp) {
        this.subjectName = subjectName;
        this.bookName = bookName;
        this.authorName = authorName;
        this.bookId = bookId;
        this.timestamp = timestamp;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
