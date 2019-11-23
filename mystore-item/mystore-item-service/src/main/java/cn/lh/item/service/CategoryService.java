package cn.lh.item.service;

import cn.lh.item.mapper.CategoryMapper;
import cn.lh.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类业务层实现类
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点id查询分类
     * @param pid 父节点id
     * @return 分类集合
     */
    public List<Category> findByPid(Long pid){
        Category category = new Category();
        category.setParentId(pid);

        List<Category> categoryList = categoryMapper.select(category);

        return categoryList;
    }

    /**
     * 通过分类id集合查询分类名称集合
     * @param ids
     * @return
     */
    public List<String> findNamesByIds(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);

        return categories.stream().map(categorie -> categorie.getName()).collect(Collectors.toList());
    }

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    public Category findById(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }
}
