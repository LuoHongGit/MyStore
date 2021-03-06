package cn.lh.item.controller;

import cn.lh.item.pojo.Brand;
import cn.lh.item.pojo.PageResult;
import cn.lh.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌控制层
 */
@Controller
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
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

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addBrand(Brand brand, @RequestParam("cids")List<Long> cids){

        brandService.addBrand(brand,cids);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> findByCid(@PathVariable("cid")Long cid){
        List<Brand> brandList = brandService.findByCid(cid);

        if(CollectionUtils.isEmpty(brandList)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brandList);
    }

    /**
     * 根据品牌id查询品牌
     */
    @GetMapping("/id")
    public ResponseEntity<Brand> findById(@RequestParam("id")Long id){
        Brand brand = brandService.findById(id);

        if(brand == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brand);
    }

}
