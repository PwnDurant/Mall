package com.zqq.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.zqq.elasticsearch.config.ElasticConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallElasticsearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() throws IOException {
//        System.out.println(client);
        IndexRequest indexRequest = new IndexRequest("user");  //创一个索引请求
        indexRequest.id("1"); //数据的 Id
//        indexRequest.source("userName","zhangsan","age",18,"gender","男");  //构造数据
        User user = new User();
        user.setUserName("zhangsan");
        user.setAge(18);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);  //要保存的内容

//        执行操作
        IndexResponse index = client.index(indexRequest, ElasticConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Data
    class User{
        private String userName;
        private Integer age;
        private String gender;
    }


    @ToString
    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    public void testSearch() throws IOException {
//        创建一个检索请求
        SearchRequest searchRequest = new SearchRequest();
//        指定索引 在哪里检索
        searchRequest.indices("bank");
//        指定 DSL 检索条件     SearchSourceBuilder sourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        1.1)构造检索条件
//        searchSourceBuilder.query();
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
//        按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
//        计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        System.out.println("这是检索条件:"+searchSourceBuilder);
//        把构造好的条件放进请求中
        searchRequest.source(searchSourceBuilder);

//        执行检索并拿到响应
        SearchResponse searchResponse = client.search(searchRequest, ElasticConfig.COMMON_OPTIONS);

//        分析解析
        System.out.println(searchResponse.toString());
//        Map map = JSON.parseObject(searchResponse.toString(), Map.class); //可以把结果封装为map
//        获取所有查到的记录
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String string = hit.getSourceAsString();
            Account account = JSON.parseObject(string, Account.class);
            System.out.println(account);
        }

//        获取这次检索到的分析信息（聚合过后的信息）
        Aggregations aggregations = searchResponse.getAggregations();
//        for (Aggregation aggregation : aggregations.asList()) {
//            System.out.println("当前聚合的名字是："+aggregation.getName());
//        }
        Terms aggregation = aggregations.get("ageAgg");

        for (Terms.Bucket bucket : aggregation.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄:"+keyAsString+"==>"+bucket.getDocCount());
        }

        Avg aggregation1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资："+aggregation1.getValue());
    }

}
