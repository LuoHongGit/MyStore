package cn.lh.item.api;

import cn.lh.item.bo.SpuBo;
import cn.lh.item.pojo.PageResult;
import cn.lh.item.pojo.Sku;
import cn.lh.item.pojo.Spu;
import cn.lh.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品控制层
 */
public interface GoodsApi {

    /**
     * 分页查询Spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> findSpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    /**
     * 根据spuid查询spu详情
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuid}")
    public SpuDetail findSpuDetailBySpuId(@PathVariable("spuid")Long spuId);

    /**
     * 根据spuid查询sku集合
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public List<Sku> findSkusBySpuId(@RequestParam("id")Long spuId);

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    @GetMapping("/spu/id")
    public Spu findById(@RequestParam("id")Long id);
}
