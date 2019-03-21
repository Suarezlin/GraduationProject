package com.suarezlin.controller;

import com.suarezlin.VO.UsersVO;
import com.suarezlin.pojo.Bgm;
import com.suarezlin.pojo.Users;
import com.suarezlin.pojo.VO.VideosVO;
import com.suarezlin.pojo.Videos;
import com.suarezlin.service.BgmService;
import com.suarezlin.service.VideoService;
import com.suarezlin.utils.CommonReturnType;
import com.suarezlin.utils.MergeVideoBgm;
import com.suarezlin.utils.PagedResult;
import com.suarezlin.utils.UUID;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/video")
@Api(value = "视频接口", tags = {"处理与视频相关操作的 controller"})
public class VideoController {

    // 文件保存命名空间
    @Value("${com.suarezlin.filePath}")
    private String filePath;

    @Value("${com.suarezlin.ffmpegPath}")
    private String ffmpegPath;

    @Autowired
    private VideoService videoService;

    @Autowired
    private BgmService bgmService;


    @PostMapping(value = "/{userId}", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "视频上传", notes = "用户上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户的 Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "bgmId", value = "视频 bgm 的 Id", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoSeconds", value = "上传视频的时长", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "videoWidth", value = "上传视频的宽度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "videoHeight", value = "上传视频的高度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "上传视频的描述", required = false, dataType = "String", paramType = "query")
            //@ApiImplicitParam(name = "file", value = "上传视频文件", required = true, dataType = "MultipartFile", paramType = "body"),
    })
    public CommonReturnType upload(
                                   @PathVariable("userId") String userId,
                                   String bgmId,
                                   double videoSeconds,
                                   int videoWidth,
                                   int videoHeight,
                                   String description,
                                   @ApiParam(value = "上传视频", required = true) MultipartFile file
    ) throws IOException {
        if (StringUtils.isBlank(userId)) {
            return CommonReturnType.errorMsg("用户 Id 不能为空");
        }

        // 数据库中保存的视频路径
        String uploadPathDB = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                if (StringUtils.isBlank(fileName)) {
                    return CommonReturnType.errorMsg("视频名称不能为空");
                } else {
                    // 文件上传绝对路径
                    String name = UUID.generateUUID();
                    String fileUploadPath = filePath + uploadPathDB + "/" + name + "." + fileName.split("\\.")[1];
                    // 设计数据库保存路径
                    uploadPathDB += ("/" + name + "." + fileName.split("\\.")[1]);

                    // 检测目录是否存在，若不存在则创建目录
                    File outFile = new File(fileUploadPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建目录
                        outFile.getParentFile().mkdirs();
                    }

                    // 文件输出
                    fileOutputStream = new FileOutputStream(fileUploadPath);
                    inputStream = file.getInputStream();
                    Videos video = new Videos();
                    video.setUserId(userId);
                    video.setVideoPath(uploadPathDB);
                    video.setVideoSeconds((float)videoSeconds);
                    video.setVideoHeight(videoHeight);
                    video.setVideoWidth(videoWidth);
                    video.setLikeCounts(0L);
                    video.setStatus(1);
                    video.setCreateTime(new Date());
                    if (bgmId != null && StringUtils.isNotBlank(bgmId)) {
                        video.setAudioId(bgmId);
                    }
                    if (description != null && StringUtils.isNotBlank(description)) {
                        video.setVideoDesc(description);
                    }
                    IOUtils.copy(inputStream, fileOutputStream);

                    // 获取视频截图作为封面
                    // 数据库保存路径
                    uploadPathDB = "/" + video.getUserId() + "/video/cover";
                    uploadPathDB += ("/" + name + ".jpg");
                    String coverFinalPath = filePath + uploadPathDB;

                    File cover = new File(coverFinalPath);
                    if (cover.getParentFile() == null || !cover.getParentFile().isDirectory()) {
                        cover.getParentFile().mkdirs();
                    }

                    MergeVideoBgm tool = new MergeVideoBgm(ffmpegPath);
                    tool.getScreenShot(fileUploadPath, coverFinalPath);
                    video.setCoverPath(uploadPathDB);

                    videoService.saveVideo(video);


                    // 若 bgmId 不为空，则需合并视频
                    if (StringUtils.isNotBlank(bgmId)) {
                        Bgm bgm = bgmService.getBgmById(bgmId);
                        String bgmPath = filePath + bgm.getPath();
                        String outputVideoName = video.getId() + ".mp4";
                        String outputVideoPath = filePath + "/" + userId + "/video" + "/" + outputVideoName;
                        System.out.println(bgmPath);
                        tool.convert(filePath + video.getVideoPath(), bgmPath, outputVideoPath, video.getVideoSeconds());
                        video.setVideoPath("/" + userId + "/video" + "/" + outputVideoName);
                        videoService.updateVideo(video);
                    }

                    return CommonReturnType.ok(video);
                }
            } else {
                return CommonReturnType.errorMsg("上传出错");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return CommonReturnType.errorMsg(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return CommonReturnType.errorMsg(e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                return CommonReturnType.errorMsg(e.getMessage());
            }
        }
    }

    @PostMapping("/list")
    @ApiOperation(value = "分页查询视频", notes = "分页查询视频列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videos", value = "搜索体", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "isSaveRecords", value = "为1搜索", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "当前页面数", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = false, dataType = "int", paramType = "query")
    })
    public CommonReturnType getVideoList(@RequestBody Videos videos,@RequestParam(required = false) Integer isSaveRecords, Integer page, Integer pageSize) {

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            page = 10;
        }

        PagedResult pagedResult = videoService.getAllVideos(videos, isSaveRecords, page, pageSize);

        return CommonReturnType.ok(pagedResult);
    }

    @GetMapping("/hot")
    @ApiOperation(value = "查询热词", notes = "分页查询热词列表接口")
    public CommonReturnType getHot() {


        return CommonReturnType.ok(videoService.getHotWords());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取视频信息", notes = "通过视频 id 获取视频信息")
    @ApiImplicitParam(name = "id", value = "所查询视频的 id", required = true, dataType = "String", paramType = "path")
    public CommonReturnType getVideo(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            return CommonReturnType.errorMsg("视频 id 不能为空");
        }
        Videos video = videoService.getVideoById(id);
        return CommonReturnType.ok(video);
    }

    @PostMapping(value = "/like")
    public CommonReturnType userLike(String userId, String videoId, String videoCreaterId) {
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return CommonReturnType.ok();
    }

    @PostMapping(value = "/notlike")
    public CommonReturnType userNotLike(String userId, String videoId, String videoCreaterId) {
        videoService.userNotLikeVideo(userId, videoId, videoCreaterId);
        return CommonReturnType.ok();
    }

//    @GetMapping("/getComment")
//    public CommonReturnType getComment() {
//
//    }

    @GetMapping("/getVideo")
    public CommonReturnType getUserVideos(String publisherId, Integer page, Integer pageSize) {
        if (StringUtils.isBlank(publisherId) || StringUtils.isEmpty(publisherId)) {
            return CommonReturnType.errorMsg("用户 Id 不能为空");
        }
        PagedResult pagedResult = videoService.getUserVideos(publisherId, page, pageSize);
        return CommonReturnType.ok(pagedResult);
    }

    @GetMapping("/getLikeVideo")
    public CommonReturnType getLikeVideo(String userId, Integer page, Integer pageSize) {
        if (StringUtils.isBlank(userId) || StringUtils.isEmpty(userId)) {
            return CommonReturnType.errorMsg("用户 Id 不能为空");
        }
        PagedResult pagedResult = videoService.getUserLikeVideos(userId, page, pageSize);
        return CommonReturnType.ok(pagedResult);
    }

    @GetMapping("/getFollowVideo")
    public CommonReturnType getFollowVideo(String userId, Integer page, Integer pageSize) {
        if (StringUtils.isBlank(userId) || StringUtils.isEmpty(userId)) {
            return CommonReturnType.errorMsg("用户 Id 不能为空");
        }
        PagedResult pagedResult = videoService.getUserFollowVideos(userId, page, pageSize);
        return CommonReturnType.ok(pagedResult);
    }

    @PostMapping("/delete")
    public CommonReturnType deleteVideo(String videoId) {
        if (StringUtils.isBlank(videoId) || StringUtils.isEmpty(videoId)) {
            return CommonReturnType.errorMsg("视频 Id 不能为空");
        }
        Videos video = videoService.getVideoById(videoId);
        videoService.deleteVideo(videoId);
        File videoFile = new File(filePath + video.getVideoPath());
        File coverFile = new File(filePath + video.getCoverPath());
        if (!videoFile.delete()) {
            return CommonReturnType.errorMsg("删除失败");
        }
        if (!coverFile.delete()) {
            return CommonReturnType.errorMsg("删除失败");
        }
        return CommonReturnType.ok();
    }

}
