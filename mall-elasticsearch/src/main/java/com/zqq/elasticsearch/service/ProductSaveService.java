package com.zqq.elasticsearch.service;


import com.zqq.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {


    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
