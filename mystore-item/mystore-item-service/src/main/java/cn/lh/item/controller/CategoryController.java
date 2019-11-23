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

/**
 * 分类控制层
 */
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点查询所有分类
     * @param pid
     * @return
     */
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

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/id")
    public ResponseEntity<Category> findById(@RequestParam("id")Long id){
        Category category = categoryService.findById(id);

        if(category == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }

    /**
     * 根据id集合查询分类名称集合
     * @param ids
     * @return
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> findNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = categoryService.findNamesByIds(ids);

        if(CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(names);
    }
}
