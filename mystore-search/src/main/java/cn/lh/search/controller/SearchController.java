package cn.lh.search.controller;

import cn.lh.search.pojo.SearchRequest;
import cn.lh.search.pojo.SearchResult;
import cn.lh.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 搜索控制层
 */
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
     * 分页查询商品
     * @param searchRequest
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<SearchResult> findByPage(@RequestBody SearchRequest searchRequest){
        SearchResult searchResult = searchService.findByPage(searchRequest);

        if(searchResult == null || CollectionUtils.isEmpty(searchResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(searchResult);
    }
}
