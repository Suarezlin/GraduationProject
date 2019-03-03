package com.suarezlin.service.impl;

import com.suarezlin.mapper.UserLikeVideosMapper;
import com.suarezlin.mapper.UsersFansMapper;
import com.suarezlin.mapper.UsersMapper;
import com.suarezlin.pojo.UserLikeVideos;
import com.suarezlin.pojo.Users;
import com.suarezlin.pojo.UsersFans;
import com.suarezlin.service.UserService;
import com.suarezlin.utils.UUID;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    static {
        System.out.println("UserServiceImpl loading...");
    }

    @Autowired
    private UsersMapper usersMapper = null;

    @Autowired
    private UserLikeVideosMapper userLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private Sid sid = null;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUsernameExist(String username) {
        Users user = new Users();
        user.setUsername(username);

        Users result = usersMapper.selectOne(user);

        return result == null ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {
        // TODO: 将来改成 使用 id 的方式
        // String id = UUID.nameUUIDFromBytes(user.getUsername().getBytes()).toString().replace("-", "").toLowerCase();
        user.setId(sid.nextShort());
        usersMapper.insert(user);

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users matchUser(Users users) {
        String password = users.getPassword();
        users.setPassword(null);
        Users result = usersMapper.selectOne(users);
        if (result == null) return null;
        else {
            if (BCrypt.checkpw(password, result.getPassword())) {
                return result;
            } else {
                return null;
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(Users users) {
        usersMapper.updateByPrimaryKey(users);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users getUserById(String id) {
        Users user = new Users();
        user.setId(id);
        user = usersMapper.selectOne(user);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId)) {
            return false;
        }

        Example example = new Example(UserLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        List<UserLikeVideos> ulv = userLikeVideosMapper.selectByExample(example);
        if (ulv != null && ulv.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUserFanRelation(String userId, String fanId) {
        UsersFans usersFans = new UsersFans();
        usersFans.setId(UUID.generateUUID());
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);
        usersMapper.addFanCount(userId);
        usersMapper.addFollowCount(fanId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserFanRelation(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);
        usersFansMapper.deleteByExample(example);
        usersMapper.reduceFanCount(userId);
        usersMapper.reduceFollowCount(fanId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean hasUserFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> uf = usersFansMapper.selectByExample(example);
        if (uf != null && uf.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

}
