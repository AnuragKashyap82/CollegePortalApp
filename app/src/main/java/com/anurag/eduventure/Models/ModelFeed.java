package com.anurag.eduventure.Models;

public class ModelFeed {

    String postText, uid,  postId, postImage;

    public ModelFeed() {
    }

    public ModelFeed(String postText, String uid, String postId, String postImage, String postVideo) {
        this.postText = postText;
        this.uid = uid;
        this.postId = postId;
        this.postImage = postImage;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}