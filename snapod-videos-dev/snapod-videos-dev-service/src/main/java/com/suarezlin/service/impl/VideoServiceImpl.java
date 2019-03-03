package com.suarezlin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.suarezlin.mapper.*;
import com.suarezlin.pojo.SearchRecords;
import com.suarezlin.pojo.UserLikeVideos;
import com.suarezlin.pojo.VO.VideosVO;
import com.suarezlin.pojo.Videos;
import com.suarezlin.service.UserService;
import com.suarezlin.service.VideoService;
import com.suarezlin.utils.PagedResult;
import com.suarezlin.utils.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UserLikeVideosMapper userLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UserService userService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String saveVideo(Videos video) {
        String id = UUID.generateUUID();
        video.setId(id);
        videosMapper.insert(video);
        return id;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVideo(Videos video) {
        videosMapper.updateByPrimaryKey(video);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Videos getVideoById(String id) {
        return videosMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PagedResult getAllVideos(Videos video, Integer isSaveRecords, Integer page, Integer pageSize) {
        // 保存热搜词
        String desc = null;
        if (isSaveRecords != null && isSaveRecords == 1) {
            desc = video.getVideoDesc();
            SearchRecords record = new SearchRecords();
            record.setId(UUID.generateUUID());
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page, pageSize);
        List<VideosVO> videos = videosMapperCustom.getAllVideos(desc);

        PageInfo<VideosVO> pageInfo = new PageInfo<>(videos);

        PagedResult result = new PagedResult();
        result.setPage(page);
        result.setRows(videos);
        result.setTotal(pageInfo.getPages());
        result.setRecords(pageInfo.getTotal());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getHotWords() {
        List<String> hotWords = searchRecordsMapper.getHotWords();
        return hotWords;
    }

    @Override
    public Videos getVideo(String id) {
        Videos video = videosMapper.selectByPrimaryKey(id);
        return video;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        // 保存喜欢关联表
        String likeId = UUID.generateUUID();
        UserLikeVideos ulv = new UserLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        userLikeVideosMapper.insert(ulv);

        // 视频喜欢数量加一
        videosMapperCustom.addVideoLikeCount(videoId);
        // 用户受喜欢数量加一
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userNotLikeVideo(String userId, String videoId, String videoCreaterId) {
        // 删除喜欢关联表
        UserLikeVideos ulv = new UserLikeVideos();
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        userLikeVideosMapper.delete(ulv);

        // 视频喜欢数量减一
        videosMapperCustom.reduceVideoLikeCount(videoId);
        // 用户受喜欢数量减一
        usersMapper.reduceReceiveLikeCount(videoCreaterId);
    }
}
