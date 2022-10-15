package com.lxsx.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.lxsx.gulimall.constant.EsConstant;
import com.lxsx.gulimall.search.config.ElasticSearchConfig;
import com.lxsx.gulimall.search.service.SpuUpService;
import com.lxsx.gulimall.to.es.SkuEsModel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpuUpServiceImpl implements SpuUpService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean spuStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //建立批量请求
        BulkRequest bulkRequest = new BulkRequest();

        skuEsModels.forEach(skuEsModel -> {
            //创建 保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //设置id 唯一标识
            indexRequest.id(skuEsModel.getSpuId().toString());
            //转化数据json
            String toJSONString = JSON.toJSONString(skuEsModel);
            //将资源添加到保存请求
            indexRequest.source(toJSONString, XContentType.JSON);
            //添加每一个保存记录
            bulkRequest.add(indexRequest);
        });

        BulkResponse bulkResponse =
                restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //感知错误上传的数据 ,有错误就是true
        boolean res = bulkResponse.hasFailures();
        List<String> collect = Arrays.stream(bulkResponse.getItems()).map(bulkItemResponse -> {
            return bulkItemResponse.getId();
        }).collect(Collectors.toList());
        if (res) {
            log.error("商品上架成功：{}", collect);
            return res;
        } else {
            return !res;
        }
    }

}
