package com.suarezlin.pojo.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;

public class PublisherVO {
    private UsersVO publisher;
    private boolean isLikeVideo;

    public UsersVO getPublisher() {
        return publisher;
    }

    public void setPublisher(UsersVO publisher) {
        this.publisher = publisher;
    }

    public boolean isLikeVideo() {
        return isLikeVideo;
    }

    public void setLikeVideo(boolean likeVideo) {
        isLikeVideo = likeVideo;
    }
}