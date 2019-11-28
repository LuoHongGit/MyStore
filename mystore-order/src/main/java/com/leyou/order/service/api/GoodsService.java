package com.leyou.order.service.api;

import cn.lh.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "mystore-gateway", path = "/api/item")
public interface GoodsService extends GoodsApi {
}
