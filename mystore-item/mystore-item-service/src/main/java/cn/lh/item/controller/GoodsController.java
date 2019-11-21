package cn.lh.item.controller;

import cn.lh.item.bo.SpuBo;
import cn.lh.item.pojo.PageResult;
import cn.lh.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品控制层
 */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询Spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> findSpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    ){
        PageResult<SpuBo> pageResult = goodsService.findSpuBoByPage(key,saleable,page,rows);

        if(pageResult == null || CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addGoods(@RequestBody SpuBo spuBo){
        try {
            goodsService.addGoods(spuBo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
