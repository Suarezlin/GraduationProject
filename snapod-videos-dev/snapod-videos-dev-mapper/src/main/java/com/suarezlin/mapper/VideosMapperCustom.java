package com.suarezlin.mapper;

import com.suarezlin.pojo.Users;
import com.suarezlin.pojo.VO.VideosVO;
import com.suarezlin.pojo.Videos;
import com.suarezlin.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVO> getAllVideos(@Param("videoDesc") String videoDesc);

    /**
     * 对视频喜欢的数量进行累加
     * @param videoId 视频 ID
     */
    public void addVideoLikeCount(String videoId);
    /**
     * 对视频喜欢的数量进行累减
     * @param videoId 视频 ID
     */
    public void reduceVideoLikeCount(String videoId);

    public List<VideosVO> getUserVideos(String publisherId);

    public List<VideosVO> getUserLikeVideos(String userId);

    public List<VideosVO> getUserFollowVideos(String userId);

    public void deleteVideo(String videoId);

    public List<Users> getVideoLiker(String videoId);

}