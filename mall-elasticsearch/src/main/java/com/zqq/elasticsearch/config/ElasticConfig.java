package com.zqq.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 引入依赖
 * 编写配置,给容器中注入一个 RestHighLevelClient
 */
@Configuration
public class ElasticConfig {

    @Bean
    public RestHighLevelClient esRestClient(){
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("123.56.166.56",9200,"http")
                ));
    }


    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.addHeader("Authorization", "Bearer " + TOKEN);
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

}
