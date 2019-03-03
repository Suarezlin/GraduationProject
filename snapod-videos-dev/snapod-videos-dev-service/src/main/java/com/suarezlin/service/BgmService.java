package com.suarezlin.service;

import com.suarezlin.pojo.Bgm;

import java.util.List;

public interface BgmService {

    /**
     * 获取 BGM 列表
     * @return 全部 BGM 列表
     */
    public List<Bgm> getBgmList();

    public Bgm getBgmById(String id);

}
