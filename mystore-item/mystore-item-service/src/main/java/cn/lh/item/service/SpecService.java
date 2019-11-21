package cn.lh.item.service;

import cn.lh.item.mapper.SpecGroupMapper;
import cn.lh.item.mapper.SpecParamMapper;
import cn.lh.item.pojo.SpecGroup;
import cn.lh.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 规格相关业务层
 */
@Service
public class SpecService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询该分类下的规格参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> findSpecGroupByCid(Long cid) {
        SpecGroup instance = new SpecGroup();
        instance.setCid(cid);

        List<SpecGroup> groupList = specGroupMapper.select(instance);

        return groupList;
    }

    /**
     * 根据参数组id查询规格参数
     * @param gid
     * @return
     */
    public List<SpecParam> findSpecParamByGid(Long gid) {
        SpecParam instance = new SpecParam();
        instance.setGroupId(gid);

        List<SpecParam> paramList = specParamMapper.select(instance);

        return paramList;
    }
}
