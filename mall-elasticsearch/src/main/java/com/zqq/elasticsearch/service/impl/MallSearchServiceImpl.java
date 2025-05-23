package com.zqq.elasticsearch.service.impl;

import com.zqq.elasticsearch.service.MallSearchService;
import com.zqq.elasticsearch.vo.SearchParam;
import com.zqq.elasticsearch.vo.SearchResult;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam param) {
//        动态构建出查询需要的 DSL 语句


        return null;
    }
}
