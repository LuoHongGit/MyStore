package cn.lh.search.service;

import cn.lh.item.pojo.*;
import cn.lh.search.client.BrandClient;
import cn.lh.search.client.CategoryClient;
import cn.lh.search.client.GoodsClient;
import cn.lh.search.client.SpecClient;
import cn.lh.search.pojo.Goods;
import cn.lh.search.pojo.SearchRequest;
import cn.lh.search.pojo.SearchResult;
import cn.lh.search.repository.GoodsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 通用构建Goods对象方法
     * @param spu
     * @return
     * @throws Exception
     */
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
     * 通用选择范围方法
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
    public SearchResult findByPage(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder basicQuery = buildBooleanQueryBuilder(searchRequest);

        //添加搜索条件
        queryBuilder.withQuery(basicQuery);

        //添加分页
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage() - 1,searchRequest.getSize()));

        //添加字段过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        String brandsAggName = "brands";
        String categoriesAggName = "categories";

        //聚合品牌id
        queryBuilder.addAggregation(AggregationBuilders.terms(brandsAggName).field("brandId"));

        //聚合分类id
        queryBuilder.addAggregation(AggregationBuilders.terms(categoriesAggName).field("cid3"));

        //获取查询结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)goodsRepository.search(queryBuilder.build());

        // 解析聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoriesAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandsAggName));

        //初始化规格参数聚合结果集
        List<Map<String,Object>> specs = null;

        //判断分类是否唯一
        if(!CollectionUtils.isEmpty(categories) && categories.size() == 1){
            //获取规格参数聚合结果
            specs = getSpecsAggResult(categories.get(0).get("id"),basicQuery);
        }

        return new SearchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent(),brands,categories,specs);
    }

    /**
     * 构建bool查询构建器
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {

            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }

    /**
     * 查询并返回规格参数聚合结果
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String,Object>> getSpecsAggResult(Object id, QueryBuilder basicQuery) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加相同的查询条件
        queryBuilder.withQuery(basicQuery);

        //查询分类下的所有可搜索参数
        List<SpecParam> specParamList = specClient.findSpecParamByCondition(null, (Long) id, null, true);

        //遍历添加查询条件
        specParamList.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });

        //排除查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));

        //获取聚合结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)goodsRepository.search(queryBuilder.build());

        // 定义一个集合，收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();

        // 解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            // 放入规格参数名
            map.put("key", entry.getKey());
            // 收集规格参数值
            List<Object> options = new ArrayList<>();
            // 解析每个聚合
            StringTerms terms = (StringTerms)entry.getValue();
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("value", options);
            paramMapList.add(map);
        }

        return paramMapList;
    }

    /**
     * 从聚合结果中解析出分类结果
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation){
        return ((LongTerms)aggregation).getBuckets().stream().map(bucket -> {
            HashMap<String, Object> map = new HashMap<>();
            Category category = categoryClient.findById(Long.valueOf(bucket.getKeyAsString()));
            map.put("id",category.getId());
            map.put("name",category.getName());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 从聚合结果中获取品牌信息
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation){
        return ((LongTerms)aggregation).getBuckets().stream().map(bucket -> {
            return brandClient.findById(Long.valueOf(bucket.getKeyAsString()));
        }).collect(Collectors.toList());
    }

    /**
     * 添加或新增数据
     * @param id
     * @throws Exception
     */
    public void createIndex(Long id) throws Exception {

        Spu spu = this.goodsClient.findById(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    /**
     * 删除指定id的数据
     * @param id
     */
    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
