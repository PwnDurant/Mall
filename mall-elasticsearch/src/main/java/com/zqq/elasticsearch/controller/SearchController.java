package com.zqq.elasticsearch.controller;

import com.zqq.elasticsearch.service.MallSearchService;
import com.zqq.elasticsearch.vo.SearchParam;
import com.zqq.elasticsearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    private MallSearchService searchService;

    /**
     * SpringMVC 可以自动将页面提交过来的所有请求查询参数封装为指定对象
     * @param param
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){
//        根据传递过来的参数去 es 中检索商品
        SearchResult result=  searchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }

}
