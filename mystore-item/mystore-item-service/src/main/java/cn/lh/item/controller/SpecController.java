package cn.lh.item.controller;

import cn.lh.item.pojo.SpecGroup;
import cn.lh.item.pojo.SpecParam;
import cn.lh.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 规格参数相关控制层
 */
@Controller
@RequestMapping("/spec")
public class SpecController {
    @Autowired
    private SpecService specService;

    /**
     * 根据分类id查询该分类下的规格参数组
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> findSpecGroupByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = specService.findSpecGroupByCid(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }

    /**
     * 根据参数组id查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> findSpecParamByGid(@RequestParam("gid")Long gid){
        List<SpecParam> params = specService.findSpecParamByGid(gid);

        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(params);
    }

}
