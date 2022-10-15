package com.lxsx.gulimall.search.config;

import com.lxsx.gulimall.constant.EsConstant;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class ElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder =RequestOptions.DEFAULT.toBuilder();

        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(){

        RestClientBuilder clientBuilder =
                RestClient.builder(new HttpHost(EsConstant.ELASTICSEARCH_HOST,
                        EsConstant.ELASTICSEARCH_PORT,
                        EsConstant.ELASTICSEARCH_PROTOCOL));

        return new RestHighLevelClient(clientBuilder);
    };
}
