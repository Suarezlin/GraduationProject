package com.suarezlin.controller;

import com.suarezlin.pojo.UsersReport;
import com.suarezlin.pojo.VO.UsersVO;
import com.suarezlin.pojo.Users;
import com.suarezlin.pojo.VO.PublisherVO;
import com.suarezlin.pojo.Videos;
import com.suarezlin.service.UserService;
import com.suarezlin.service.VideoService;
import com.suarezlin.utils.CommonReturnType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FrameFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@Api(value = "用户接口", description = "用户相关操作接口")
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;

    @Autowired
    private VideoService videoService;




    // 文件保存命名空间
    @Value("${com.suarezlin.filePath}")
    private String filePath = "/Users/hayashikoushi/Documents/code/GraduationProject/ProjectFile";

    @ApiOperation(value = "头像上传接口", notes = "用户上传头像接口")
    @PostMapping("/face")
    public CommonReturnType uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws IOException {

        if (StringUtils.isBlank(userId)) {
            return CommonReturnType.errorMsg("用户 id 不能为空");
        }

        // 数据库保存路径
        String uploadPathDB = "/" + userId + "/face";

        if (files != null && files.length > 0) {
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            String fileName = files[0].getOriginalFilename();
            try {
                if (StringUtils.isNoneBlank(fileName)) {
                    // 文件上传绝对路径
                    String fileUploadPath = filePath + uploadPathDB + "/" + fileName;
                    // 设计数据库保存路径
                    uploadPathDB += ("/" + fileName);

                    // 检测目录是否存在，若不存在则创建目录
                    File outFile = new File(fileUploadPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建目录
                        outFile.getParentFile().mkdirs();
                    }

                    // 文件输出
                    fileOutputStream = new FileOutputStream(fileUploadPath);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                    Users user = userService.getUserById(userId);
                    user.setFaceImage(uploadPathDB);
                    UsersVO usersVO = new UsersVO();
                    userService.updateUser(user);
                    BeanUtils.copyProperties(user, usersVO);
                    return CommonReturnType.ok(usersVO);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return CommonReturnType.errorMsg(e.getMessage());
            } finally {
                if (fileOutputStream != null) {
                    // 保存后关闭输出流
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } else {
            return CommonReturnType.errorMsg("上传出错");
        }
        return CommonReturnType.ok();
    }

    @GetMapping("/info")
    @ApiOperation(value = "用户信息接口", notes = "查询用户信息接口")
    @ApiImplicitParam(name="id", value = "用户 ID", required = true, dataType = "String", paramType = "query")
    public CommonReturnType getInfo(String id) {
        try {
            Users user = userService.getUserById(id);
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            return CommonReturnType.ok(usersVO);
        } catch (Exception e) {
            return CommonReturnType.errorMsg(e.getMessage());
        }
    }

    @GetMapping("/isLogin")
    public CommonReturnType isLogin(String userId, String userToken) {
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
                return CommonReturnType.ok(false);
            } else {
                if (!uniqueToken.equals(userToken)) {
                    return CommonReturnType.ok(false);
                }
            }
        } else {
            return CommonReturnType.ok(false);
        }

        return CommonReturnType.ok(true);
    }

    @GetMapping("/publisher/{videoId}")
    public CommonReturnType getPublisher(@PathVariable String videoId,  String loginUserId) {
        if (StringUtils.isBlank(videoId)) {
            return CommonReturnType.errorMsg("视频 ID 不能为空");
        }
        // 查询视频发布者 id
        Videos video = videoService.getVideoById(videoId);
        Users user = userService.getUserById(video.getUserId());
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(user, publisher);
        // 查询当前登陆者与用户点赞关系
        boolean isLike = userService.isUserLikeVideo(loginUserId, videoId);
        PublisherVO publisherVO = new PublisherVO();
        publisherVO.setPublisher(publisher);
        publisherVO.setLikeVideo(isLike);
        return CommonReturnType.ok(publisherVO);
    }

    @PostMapping("/fan")
    @ApiOperation(value = "关注", notes = "执行关注功能的接口")
    public CommonReturnType fan(String userId, String fanId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return CommonReturnType.errorMsg("Id 不能为空");
        }

        if (userService.hasUserFollow(userId, fanId)) {
            return CommonReturnType.errorMsg("用户已关注");
        }

        userService.saveUserFanRelation(userId, fanId);
        return CommonReturnType.ok();
    }

    @GetMapping("/fan")
    public CommonReturnType isFan(String userId, String fanId) {
        return CommonReturnType.ok(userService.hasUserFollow(userId, fanId));
    }

    @PostMapping("/cencelFan")
    @ApiOperation(value = "取消关注", notes = "执行取消关注功能的接口")
    public CommonReturnType cancelFan(String userId, String fanId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return CommonReturnType.errorMsg("Id 不能为空");
        }

        if (!userService.hasUserFollow(userId, fanId)) {
            return CommonReturnType.errorMsg("用户未关注");
        }

        userService.deleteUserFanRelation(userId, fanId);
        return CommonReturnType.ok();
    }

    @PostMapping("/report")
    public CommonReturnType reportUser(@RequestBody UsersReport usersReport) {

        //TODO: 完成举报接口功能

        return CommonReturnType.ok();
    }

}
