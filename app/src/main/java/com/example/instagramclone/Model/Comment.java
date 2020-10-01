package com.example.instagramclone.Model;

public class Comment {

    private String comment;
    private String publisher;

    public Comment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
