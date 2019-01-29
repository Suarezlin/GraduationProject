package com.suarezlin.service;

import com.suarezlin.pojo.Users;

public interface UserService {
    /**
     * 判断用户是否存在
     * @param username 用户名
     * @return true 存在 false 不存在
     */
    public boolean isUsernameExist(String username);

    /**
     * 保存用户
     * @param user 注册用户
     */
    public void saveUser(Users user);

    /**
     * 判断用户名与密码是否匹配
     * @param users 待验证用户
     * @return 成功: 用户对象 失败: null
     */
    public Users matchUser(Users users);


}
