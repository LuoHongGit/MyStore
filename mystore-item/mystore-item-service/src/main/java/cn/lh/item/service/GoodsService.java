package cn.lh.item.service;

import cn.lh.item.bo.SpuBo;
import cn.lh.item.mapper.*;
import cn.lh.item.pojo.PageResult;
import cn.lh.item.pojo.Spu;
import cn.lh.item.pojo.Stock;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品业务层
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    /**
     * 分页查询Spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> findSpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        //创建Example对象
        Example example = new Example(Spu.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //加入标题模糊查询条件
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%" + key+ "%");
        }

        //加入上下架条件
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }

        //设置分页
        PageHelper.startPage(page,rows);

        //获取查询结果
        List<Spu> spuList = spuMapper.selectByExample(example);

        if(spuList == null){
            return null;
        }

        //创建PageInfo对象
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);

        //遍历集合
        List<SpuBo> spuBoList = spuList.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();

            //封装已有属性
            BeanUtils.copyProperties(spu,spuBo);

            //查询三级分类名并封装
            List<String> cnames = categoryService.findNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(cnames,"/"));

            //查询品牌名并封装
            spuBo.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());

            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<SpuBo>(pageInfo.getTotal(),spuBoList);
    }

    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @Transactional
    public void addGoods(SpuBo spuBo) {
        //新增Spu表
        spuBo.setId(null);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuMapper.insertSelective(spuBo);

        //新增SpuDetail表
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuBo.getSpuDetail());

        //新增sku表以及库存
        spuBo.getSkus().forEach(sku ->{
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }
}
