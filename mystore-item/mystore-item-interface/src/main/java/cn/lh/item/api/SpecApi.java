package cn.lh.item.api;

import cn.lh.item.pojo.SpecGroup;
import cn.lh.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 规格参数相关控制层
 */
@RequestMapping("/spec")
public interface SpecApi {
    /**
     * 根据分类id查询该分类下的规格参数组
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public List<SpecGroup> findSpecGroupByCid(@PathVariable("cid")Long cid);

    /**
     * 根据参数组id查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public List<SpecParam> findSpecParamByCondition(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "generic",required = false)Boolean generic,
            @RequestParam(value = "searching",required = false)Boolean searching
            );

}
