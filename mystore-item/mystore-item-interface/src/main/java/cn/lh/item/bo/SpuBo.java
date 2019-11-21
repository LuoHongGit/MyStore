package cn.lh.item.bo;

import cn.lh.item.pojo.Sku;
import cn.lh.item.pojo.Spu;
import cn.lh.item.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

/**
 * Spu业务领域对象
 */
@Data
public class SpuBo extends Spu{
    private String cname;
    private String bname;

    private SpuDetail spuDetail;

    private List<Sku> skus;
}
