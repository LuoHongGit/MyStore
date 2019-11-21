package cn.lh.item.service;

import cn.lh.item.mapper.BrandMapper;
import cn.lh.item.pojo.Brand;
import cn.lh.item.pojo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 品牌业务层实现类
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 分页查询品牌
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return 分页结果对象
     */
    public PageResult<Brand> findByPage(String key, Integer page,Integer rows,String sortBy,boolean desc){
        //创建Example对象
        Example example = new Example(Brand.class);

        //添加模糊查询条件
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name",key).orEqualTo("letter",key);
        }

        //开启分页
        PageHelper.startPage(page,rows);

        //添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        //获取查询结果
        List<Brand> brandList = brandMapper.selectByExample(example);

        //封装为pageInfo
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brandList);

        return new PageResult<Brand>(brandPageInfo.getTotal(),brandPageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @Transactional
    public void addBrand(Brand brand, List<Long> cids) {
        brandMapper.insert(brand);

        for (Long cid : cids) {
            brandMapper.addBrandAndCategory(brand.getId(),cid);
        }
    }

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    public List<Brand> findByCid(Long cid) {
        return brandMapper.findByCid(cid);
    }
}
