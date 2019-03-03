package com.suarezlin.mapper;

import com.suarezlin.pojo.SearchRecords;
import com.suarezlin.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    public List<String> getHotWords();
}