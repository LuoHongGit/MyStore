package cn.lh.item.controller;

import cn.lh.item.bo.SpuBo;
import cn.lh.item.pojo.PageResult;
import cn.lh.item.pojo.Sku;
import cn.lh.item.pojo.SpuDetail;
import cn.lh.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/goods")
    public ResponseEntity<Void> addGoods(@RequestBody SpuBo spuBo){
        try {
            goodsService.addGoods(spuBo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuid查询spu详情
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuid}")
    public ResponseEntity<SpuDetail> findSpuDetailBySpuId(@PathVariable("spuid")Long spuId){
        SpuDetail spuDetail = goodsService.findSpuDetailBySpuId(spuId);

        if(spuDetail == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuid查询sku集合
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> findSkusBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skuList = goodsService.findSkusBySpuId(spuId);
        if(StringUtils.isEmpty(skuList)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(skuList);
    }

    /**
     * 修改商品
     * @param spuBo
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        try {
            goodsService.updateGoods(spuBo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
