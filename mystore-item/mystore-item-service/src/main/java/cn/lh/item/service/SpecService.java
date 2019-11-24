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
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> findSpecParamByCondition(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam instance = new SpecParam();
        instance.setGroupId(gid);
        instance.setCid(cid);
        instance.setGeneric(generic);
        instance.setSearching(searching);

        List<SpecParam> paramList = specParamMapper.select(instance);

        return paramList;
    }

    /**
     * 根据分类id查询该分类下的规格参数组及组下的参数
     * @param cid
     * @return
     */
    public List<SpecGroup> findSpecGroupWithParamByCid(Long cid) {
        SpecGroup instance = new SpecGroup();
        instance.setCid(cid);

        List<SpecGroup> groupList = specGroupMapper.select(instance);

        groupList.forEach(group -> {
            SpecParam param = new SpecParam();
            param.setGroupId(group.getId());
            List<SpecParam> specParams = specParamMapper.select(param);
            group.setParams(specParams);
        });

        return groupList;
    }
}
