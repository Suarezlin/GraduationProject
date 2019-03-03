package com.suarezlin.controller;

import com.suarezlin.service.BgmService;
import com.suarezlin.utils.CommonReturnType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bgm")
@Api(value = "Bgm 接口", description = "Bgm 操作相关接口")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @GetMapping("/all")
    @ApiOperation(value = "Bgm 列表", notes = "获取全部 Bgm")
    public CommonReturnType getAllBgm() {
        try {
            return CommonReturnType.ok(bgmService.getBgmList());
        } catch (Exception e) {
            return CommonReturnType.errorMsg(e.getMessage());
        }
    }

}
