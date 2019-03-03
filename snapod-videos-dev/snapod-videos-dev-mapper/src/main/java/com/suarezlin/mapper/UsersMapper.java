package com.suarezlin.mapper;

import com.suarezlin.pojo.Users;
import com.suarezlin.utils.MyMapper;
import org.springframework.stereotype.Repository;


@Repository
public interface UsersMapper extends MyMapper<Users> {

    public Users selectUserByName(String username);

    public void addReceiveLikeCount(String userId);

    public void reduceReceiveLikeCount(String userId);

    public void addFanCount(String userId);

    public void reduceFanCount(String userId);

    public void addFollowCount(String userId);

    public void reduceFollowCount(String userId);

}