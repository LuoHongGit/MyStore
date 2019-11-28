package cn.lh.item.service;

import cn.lh.item.bo.SpuBo;
import cn.lh.item.mapper.*;
import cn.lh.item.pojo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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

    @Autowired
    private AmqpTemplate amqpTemplate;

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
        AddSkuAndStock(spuBo);
    }

    /**
     * @param spuBo
     */
    private void AddSkuAndStock(SpuBo spuBo) {
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

        //发送消息到RabbitMQ服务器
        sendMessage(spuBo.getId(),"insert");
    }

    /**
     * 根据spuid查询spu详情
     * @param spuId
     * @return
     */
    public SpuDetail findSpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuid查询sku集合
     * @param spuId
     * @return
     */
    public List<Sku> findSkusBySpuId(Long spuId){
        Sku sku = new Sku();
        sku.setSpuId(spuId);

        List<Sku> skuList = skuMapper.select(sku);

        skuList.forEach(sku1 -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        });
        return skuList;
    }

    /**
     * 修改商品
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //根据spuid查询sku集合
        Sku instance = new Sku();
        instance.setSpuId(spuBo.getId());
        List<Sku> skuList = skuMapper.select(instance);

        if(!CollectionUtils.isEmpty(skuList)){
            //删除每个sku的库存信息
            skuList.forEach(sku -> {
                Stock stock = new Stock();
                stock.setSkuId(sku.getId());
                stockMapper.delete(stock);
            });

            //删除sku信息
            Sku sku = new Sku();
            sku.setSpuId(spuBo.getId());
            skuMapper.delete(sku);
        }

        //插入新的sku和库存信息
        AddSkuAndStock(spuBo);

        //更新spu
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);

        //更新spudetail
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //发送消息到RabbitMQ服务器
        sendMessage(spuBo.getId(),"update");
    }

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 通用发送消息到RabbitMQ服务器
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过skuid查询sku
     * @param id
     * @return
     */
    public Sku findSkuById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }
}
