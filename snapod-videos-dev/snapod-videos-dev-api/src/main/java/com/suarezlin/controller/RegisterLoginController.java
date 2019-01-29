package com.suarezlin.controller;
import com.suarezlin.pojo.Users;
import com.suarezlin.service.UserService;
import com.suarezlin.utils.CommonReturnType;
import com.suarezlin.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "用户注册/登录接口", tags = {"注册/登录 controller"})
public class RegisterLoginController {

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
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        } catch (Exception e) {
            return CommonReturnType.errorException(e.getMessage());
        }
        user.setFansCounts(0);
        user.setFollowCounts(0);
        user.setReceiveLikeCounts(0);

        userService.saveUser(user);
        user.setPassword("");
        return CommonReturnType.ok(user);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口", notes = "用户登录接口")
    public CommonReturnType login(@RequestBody Users user) throws InterruptedException {
        try {
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        } catch (Exception e) {
            return CommonReturnType.errorException(e.getMessage());
        }
        user = userService.matchUser(user);
        if (user == null) {
            return CommonReturnType.errorMsg("用户名或密码错误");
        } else {
            user.setPassword("");
            return CommonReturnType.ok(user);
        }
    }
}
