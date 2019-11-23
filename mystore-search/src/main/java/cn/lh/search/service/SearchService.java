package cn.lh.search.service;

import cn.lh.item.pojo.*;
import cn.lh.search.client.BrandClient;
import cn.lh.search.client.CategoryClient;
import cn.lh.search.client.GoodsClient;
import cn.lh.search.client.SpecClient;
import cn.lh.search.pojo.Goods;
import cn.lh.search.pojo.SearchRequest;
import cn.lh.search.repository.GoodsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 搜索业务层接口
 */
@Service
public class SearchService {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(Spu spu) throws Exception{
        Goods goods = new Goods();

        //设置spu已有信息
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());

        //查询分类名信息
        List<String> cnames = categoryClient.findNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询品牌信息
        Brand brand = brandClient.findById(spu.getBrandId());

        //查询sku集合
        List<Sku> skuList = goodsClient.findSkusBySpuId(spu.getId());

        List<Long> prices = new ArrayList<>();

        List<Map<String, Object>> skuMapList = new ArrayList<>();

        //遍历sku集合
        skuList.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        //获取该分类下的所有可搜索字段
        List<SpecParam> params = specClient.findSpecParamByCondition(null, spu.getCid3(), null, true);

        // 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.findSpuDetailBySpuId(spu.getId());

        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });

        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });

        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();

        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        goods.setAll(spu.getTitle() + " " + StringUtils.join(cnames," ") + " " + brand.getName());
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;
    }

    /**
     * 选择范围
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 分页查询商品
     * @param searchRequest
     * @return
     */
    public PageResult<Goods> findByPage(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加搜索条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));

        //添加分页
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage() - 1,searchRequest.getSize()));

        //添加字段过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //获取查询结果
        Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());

        return new PageResult<Goods>(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent());
    }
}
