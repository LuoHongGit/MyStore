package cn.lh.item.mapper;

import cn.lh.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 品牌持久层接口
 */
public interface BrandMapper extends Mapper<Brand> {
    //在分类和商品中间表添加数据
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void addBrandAndCategory(@Param("bid") Long bid, @Param("cid") Long cid);

    //根据分类id查询品牌集合
    @Select("select * from tb_category_brand cb join tb_brand b on cb.brand_id = b.id where cb.category_id = #{cid}")
    List<Brand> findByCid(@Param("cid")Long cid);
}
