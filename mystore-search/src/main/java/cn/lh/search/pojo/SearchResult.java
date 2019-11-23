package cn.lh.search.pojo;


import cn.lh.item.pojo.Brand;
import cn.lh.item.pojo.PageResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 搜索结果实体类
 */
@Data
public class SearchResult extends PageResult<Goods> {
    private List<Brand> brands;

    private List<Map<String,Object>> categories;

    private List<Map<String,Object>> specs;

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Brand> brands, List<Map<String, Object>> categories, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.brands = brands;
        this.categories = categories;
        this.specs = specs;
    }

}
