package com.suarezlin.controller;
import com.suarezlin.VO.UsersVO;
import com.suarezlin.pojo.Users;
import com.suarezlin.service.UserService;
import com.suarezlin.utils.CommonReturnType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@Api(value = "用户注册/登录接口", tags = {"注册/登录 controller"})
public class RegisterLoginController extends BasicController {

    @Autowired
    UserService userService = null;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册接口", notes = "用户注册接口")
    public CommonReturnType register(@RequestBody Users user) {

        // 判断用户名密码不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return CommonReturnType.errorMsg("用户名或密码不能为空");
        }

        // 判断用户是否存在
        boolean isUserExist = userService.isUsernameExist(user.getUsername());
        if (isUserExist) {
            return CommonReturnType.errorMsg("用户名已存在");
        }

        // 保存用户
        user.setNickname(user.getUsername());
        try {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        } catch (Exception e) {
            return CommonReturnType.errorException(e.getMessage());
        }
        user.setFansCounts(0);
        user.setFollowCounts(0);
        user.setReceiveLikeCounts(0);

        userService.saveUser(user);
        user.setPassword("");

        UsersVO usersVO = setUserRedisSessionToken(user);
        return CommonReturnType.ok(usersVO);
    }

    public UsersVO setUserRedisSessionToken(Users user) {
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 60 * 60 * 24 * 2);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口", notes = "用户登录接口")
    public CommonReturnType login(@RequestBody Users user) {
//        try {
//            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
//        } catch (Exception e) {
//            return CommonReturnType.errorException(e.getMessage());
//        }
        user = userService.matchUser(user);
        if (user == null) {
            return CommonReturnType.errorMsg("用户名或密码错误");
        } else {
            user.setPassword("");
            UsersVO usersVO = setUserRedisSessionToken(user);
            return CommonReturnType.ok(usersVO);
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "用户注销接口", notes = "用户注销接口")
    @ApiImplicitParam(name="userId", value = "用户 ID", required = true, dataType = "String", paramType = "query")
    public CommonReturnType logout(@RequestParam String userId) throws InterruptedException {
        redisOperator.del(USER_REDIS_SESSION + ":" + userId);
        return CommonReturnType.ok("注销成功");
    }
}
