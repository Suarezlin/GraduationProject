package com.suarezlin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.suarezlin.mapper.*;
import com.suarezlin.pojo.*;
import com.suarezlin.pojo.VO.CommentsVO;
import com.suarezlin.pojo.VO.VideosVO;
import com.suarezlin.service.UserService;
import com.suarezlin.service.VideoService;
import com.suarezlin.utils.PagedResult;
import com.suarezlin.utils.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
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
    private CommentsMapper commentsMapper;

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

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult getUserVideos(String publisherId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> videos = videosMapperCustom.getUserVideos(publisherId);

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
    public PagedResult getUserLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> videos = videosMapperCustom.getUserLikeVideos(userId);

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
    public PagedResult getUserFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> videos = videosMapperCustom.getUserFollowVideos(userId);

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
    public PagedResult getVideoComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> comments = commentsMapper.getComments(videoId);

        PageInfo<CommentsVO> pageInfo = new PageInfo<>(comments);

        PagedResult result = new PagedResult();
        result.setPage(page);
        result.setRows(comments);
        result.setTotal(pageInfo.getPages());
        result.setRecords(pageInfo.getTotal());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteVideo(String videoId) {
        List<Users> users = videosMapperCustom.getVideoLiker(videoId);
        Videos video = videosMapperCustom.selectByPrimaryKey(videoId);
        // 删除前取消所有的赞
        for (Users u : users) {
            userNotLikeVideo(u.getId(), videoId, video.getUserId());
        }

        // TODO: 删除视频所有评论
        videosMapperCustom.deleteVideo(videoId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addComment(Comments comment) {
        String id = UUID.generateUUID();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteComment(String commentId) {
        Example example = new Example(Comments.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", commentId);
        commentsMapper.deleteByExample(criteria);
    }

//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public List<CommentsVO> getComments(String videoId) {
//        List<CommentsVO> comments = commentsMapper.getCommentsByVideoId(videoId);
//        return comments;
//    }
}
