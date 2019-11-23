package cn.lh.item.api;

import cn.lh.item.pojo.Brand;
import cn.lh.item.pojo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 品牌控制层
 */
@RequestMapping("/brand")
public interface BrandApi {
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
    public PageResult<Brand> findByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc
    );

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public List<Brand> findByCid(@PathVariable("cid")Long cid);

    /**
     * 根据品牌id查询品牌
     */
    @GetMapping("/id")
    public Brand findById(@RequestParam("id")Long id);

}
