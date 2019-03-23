package com.suarezlin.service;


import com.suarezlin.pojo.Comments;
import com.suarezlin.pojo.VO.CommentsVO;
import com.suarezlin.pojo.Videos;
import com.suarezlin.utils.PagedResult;

import java.util.List;

public interface VideoService {

    public String saveVideo(Videos video);

    public void updateVideo(Videos video);

    public Videos getVideoById(String id);

    /**
     * 分页查询全部视频
     * @param page 查询页面编号
     * @param pageSize 每页记录个数
     * @return 当前页面
     */
    public PagedResult getAllVideos(Videos video, Integer isSaveRecords, Integer page, Integer pageSize);

    public List<String> getHotWords();

    public Videos getVideo(String id);

    public void userLikeVideo(String userId, String videoId, String videoCreaterId);

    public void userNotLikeVideo(String userId, String videoId, String videoCreaterId);

    public PagedResult getUserVideos(String publisherId, Integer page, Integer pageSize);

    public PagedResult getUserLikeVideos(String userId, Integer page, Integer pageSize);

    public PagedResult getUserFollowVideos(String userId, Integer page, Integer pageSize);

    public PagedResult getVideoComments(String videoId, Integer page, Integer pageSize);

    public void deleteVideo(String videoId);

    public void addComment(Comments comment);

    public void deleteComment(String commentId);

    //public List<CommentsVO> getComments(String videoId);


}
