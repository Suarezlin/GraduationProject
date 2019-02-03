package com.suarezlin.service;

import com.suarezlin.mapper.UsersMapper;
import com.suarezlin.pojo.Users;
import com.suarezlin.utils.MD5Utils;
import org.mindrot.jbcrypt.BCrypt;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper = null;

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

}
