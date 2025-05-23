package com.zqq.elasticsearch.service;

import com.zqq.elasticsearch.vo.SearchParam;
import com.zqq.elasticsearch.vo.SearchResult;

public interface MallSearchService {


    /**
     * @param param 检索的所有参数
     * @return 检索的所有结果,里面包含页面的所有信息
     */
    SearchResult search(SearchParam param);
}
