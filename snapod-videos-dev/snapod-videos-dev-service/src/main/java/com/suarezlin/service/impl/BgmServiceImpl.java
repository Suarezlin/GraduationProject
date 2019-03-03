package com.suarezlin.service.impl;

import com.suarezlin.mapper.BgmMapper;
import com.suarezlin.pojo.Bgm;
import com.suarezlin.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Bgm> getBgmList() {
        return bgmMapper.selectAll();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Bgm getBgmById(String id) {
        return bgmMapper.selectByPrimaryKey(id);
    }
}
