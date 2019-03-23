package com.suarezlin.mapper;

import com.suarezlin.pojo.Comments;
import com.suarezlin.pojo.VO.CommentsVO;
import com.suarezlin.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsMapper extends MyMapper<Comments> {

    public List<CommentsVO> getComments(String videoId);
}