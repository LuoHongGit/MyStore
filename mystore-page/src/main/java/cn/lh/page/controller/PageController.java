package cn.lh.page.controller;

import cn.lh.page.service.PageService;
import cn.lh.page.service.PageStaticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 详情页控制层
 */
@Controller
@RequestMapping("page")
public class PageController {
    @Autowired
    private PageService pageService;

    @Autowired
    private PageStaticService pageStaticService;

    @GetMapping("{id}.html")
    public String toItemPage(@PathVariable("id")Long id, Model model){
        Map<String, Object> resultMap = pageService.loadData(id);

        model.addAllAttributes(resultMap);

        pageStaticService.createHtml(id);

        return "item";
    }
}
