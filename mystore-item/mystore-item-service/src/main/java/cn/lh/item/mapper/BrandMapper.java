package cn.lh.item.mapper;

import cn.lh.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * 品牌持久层接口
 */
public interface BrandMapper extends Mapper<Brand> {
    //在分类和商品中间表添加数据
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void addBrandAndCategory(@Param("bid") Long bid, @Param("cid") Long cid);
}
