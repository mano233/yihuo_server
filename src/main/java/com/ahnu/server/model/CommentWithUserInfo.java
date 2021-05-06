package com.ahnu.server.model;

public class CommentWithUserInfo {
    Comment comment;
    Member userInfo;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Member getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Member userInfo) {
        this.userInfo = userInfo;
    }
}
