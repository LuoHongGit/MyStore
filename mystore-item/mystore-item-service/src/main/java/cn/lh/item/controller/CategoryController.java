package cn.lh.item.controller;

import cn.lh.item.pojo.Category;
import cn.lh.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> findByPid(@RequestParam(value = "pid",defaultValue = "0")Long pid){
        if(pid == null || pid < 0){
            return ResponseEntity.badRequest().build();
        }

        List<Category> categoryList = categoryService.findByPid(pid);

        if(CollectionUtils.isEmpty(categoryList)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoryList);
    }
}
