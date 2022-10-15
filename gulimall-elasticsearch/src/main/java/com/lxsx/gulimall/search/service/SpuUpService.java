package com.lxsx.gulimall.search.service;

import com.lxsx.gulimall.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface SpuUpService {

    boolean spuStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
