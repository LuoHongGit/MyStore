package cn.lh.item.controller;

import cn.lh.item.pojo.Brand;
import cn.lh.item.pojo.PageResult;
import cn.lh.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 品牌控制层
 */
@Controller
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @RequestMapping("/page")
    public ResponseEntity<PageResult<Brand>> findByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc
    ){
        PageResult<Brand> pageResult = brandService.findByPage(key, page, rows, sortBy, desc);

        if(CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pageResult);
    }

}
