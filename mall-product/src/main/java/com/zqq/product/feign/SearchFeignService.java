package com.zqq.product.feign;

import com.zqq.common.es.SkuEsModel;
import com.zqq.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-elastic")
public interface SearchFeignService {

    //    上架商品
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}
