package com.zyj.gulimall.product.web;

import com.zyj.gulimall.product.entity.CategoryEntity;
import com.zyj.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author llx
 * @date 2021-11-03 22:12
 **/
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @RequestMapping({"/","/index.html"})
    public String indexPage(Model model){
        // 查出所有一级分类
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categorys);
        return "index";
    }
}
