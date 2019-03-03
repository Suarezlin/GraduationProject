package com.suarezlin.pojo.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;

@ApiModel(value = "用户对象", description = "这是用户对象")
public class UsersVO {
    @Id
    @ApiModelProperty(hidden = true)
    private String id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", name = "username", example = "suarezlin", required = true)
    private String username;

    @ApiModelProperty(hidden = true)
    private String userToken;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", name = "password", example = "password", required = true)
    @JsonIgnore
    private String password;

    /**
     * 头像
     */
    @ApiModelProperty(hidden = true)
    private String faceImage;

    /**
     * 昵称
     */
    @ApiModelProperty(hidden = true)
    private String nickname;

    /**
     * 粉丝数
     */
    @ApiModelProperty(hidden = true)
    private Integer fansCounts;

    /**
     * 关注人数
     */
    @ApiModelProperty(hidden = true)
    private Integer followCounts;

    /**
     * 接收到的收藏/赞美数量
     */
    @ApiModelProperty(hidden = true)
    private Integer receiveLikeCounts;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return username - 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取头像
     *
     * @return face_image - 头像
     */
    public String getFaceImage() {
        return faceImage;
    }

    /**
     * 设置头像
     *
     * @param faceImage 头像
     */
    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    /**
     * 获取昵称
     *
     * @return nickname - 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称
     *
     * @param nickname 昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取粉丝数
     *
     * @return fans_counts - 粉丝数
     */
    public Integer getFansCounts() {
        return fansCounts;
    }

    /**
     * 设置粉丝数
     *
     * @param fansCounts 粉丝数
     */
    public void setFansCounts(Integer fansCounts) {
        this.fansCounts = fansCounts;
    }

    /**
     * 获取关注人数
     *
     * @return follow_counts - 关注人数
     */
    public Integer getFollowCounts() {
        return followCounts;
    }

    /**
     * 设置关注人数
     *
     * @param followCounts 关注人数
     */
    public void setFollowCounts(Integer followCounts) {
        this.followCounts = followCounts;
    }

    /**
     * 获取接收到的收藏/赞美数量
     *
     * @return receive_like_counts - 接收到的收藏/赞美数量
     */
    public Integer getReceiveLikeCounts() {
        return receiveLikeCounts;
    }

    /**
     * 设置接收到的收藏/赞美数量
     *
     * @param receiveLikeCounts 接收到的收藏/赞美数量
     */
    public void setReceiveLikeCounts(Integer receiveLikeCounts) {
        this.receiveLikeCounts = receiveLikeCounts;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}