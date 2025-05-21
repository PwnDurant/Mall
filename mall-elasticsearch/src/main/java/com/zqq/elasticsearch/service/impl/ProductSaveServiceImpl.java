package com.zqq.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.zqq.common.es.SkuEsModel;
import com.zqq.elasticsearch.config.ElasticConfig;
import com.zqq.elasticsearch.constant.EsConstant;
import com.zqq.elasticsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
//        将数据保存到 es 中
//        1，给 es 中建立索引：product 并建立号映射关系

//        2，给 es 中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : skuEsModels) {
//            构造保存请求
            IndexRequest request = new IndexRequest(EsConstant.PRODUCT_INDEX);
            request.id(model.getSkuId().toString());
            String jsonString = JSON.toJSONString(model);
            request.source(jsonString, XContentType.JSON);
//            都放进去然后统一进行批量操作
            bulkRequest.add(request);
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, ElasticConfig.COMMON_OPTIONS);
//        TODO  记录错误
        boolean b = bulkResponse.hasFailures();
        List<String> collect = Arrays.stream(bulkResponse.getItems()).map(item -> item.getId()).collect(Collectors.toList());
        log.error("商品上架成功:{},返回数据:{}",collect,bulkResponse);
        return b;
    }
}
