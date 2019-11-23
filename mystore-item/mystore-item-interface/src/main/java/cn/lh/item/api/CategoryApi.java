package cn.lh.item.api;

import cn.lh.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 分类控制层
 */
@RequestMapping("/category")
public interface CategoryApi {

    /**
     * 根据父节点查询所有分类
     * @param pid
     * @return
     */
    @GetMapping("/list")
    public List<Category> findByPid(@RequestParam(value = "pid",defaultValue = "0")Long pid);

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/id")
    public Category findById(@RequestParam("id")Long id);

    /**
     * 根据id集合查询分类名称集合
     * @param ids
     * @return
     */
    @GetMapping("/names")
    public List<String> findNamesByIds(@RequestParam("ids")List<Long> ids);
}
