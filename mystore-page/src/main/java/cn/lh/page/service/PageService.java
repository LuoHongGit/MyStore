package cn.lh.page.service;

import cn.lh.item.pojo.*;
import cn.lh.page.client.BrandClient;
import cn.lh.page.client.CategoryClient;
import cn.lh.page.client.GoodsClient;
import cn.lh.page.client.SpecClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 详情页业务层
 */
@Service
public class PageService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    public Map<String,Object> loadData(Long spuId){
        HashMap<String, Object> resultMap = new HashMap<>();

        //查询spu
        Spu spu = goodsClient.findById(spuId);

        //查询skus
        List<Sku> skus = goodsClient.findSkusBySpuId(spuId);

        //查询spudetail
        SpuDetail spuDetail = goodsClient.findSpuDetailBySpuId(spuId);

        //查询categories
        List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.findNamesByIds(ids);
        List<Map<String, Object>> categoryList = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",ids.get(i));
            map.put("name",names.get(i));
            categoryList.add(map);
        }

        //查询品牌
        Brand brand = brandClient.findById(spu.getBrandId());

        //查询规格参数组及组下参数
        List<SpecGroup> groups = specClient.findSpecGroupWithParamByCid(spu.getCid3());

        //查询非通用参数
        List<SpecParam> params = specClient.findSpecParamByCondition(null, spu.getCid3(), false, null);
        HashMap<Long, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(),param.getName());
        });

        resultMap.put("spu",spu);
        resultMap.put("skus",skus);
        resultMap.put("spuDetail",spuDetail);
        resultMap.put("categories",categoryList);
        resultMap.put("brand",brand);
        resultMap.put("groups",groups);
        resultMap.put("params",paramMap);
        return resultMap;
    }
}
