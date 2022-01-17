package com.zyj.gulimall.search.controller;

import com.zyj.gulimall.search.service.MallSearchService;
import com.zyj.gulimall.search.vo.SearchParam;
import com.zyj.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lulx
 * @date 2022-01-13 14:41
 **/
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        param.setQueryString(request.getQueryString());
        //根据查询参数检索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
