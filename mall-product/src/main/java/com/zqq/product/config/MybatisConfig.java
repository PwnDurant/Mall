package com.zqq.product.config;


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement //开启事物功能
@MapperScan("com.zqq.product.productDB.dao")
public class MybatisConfig {

    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor=new PaginationInterceptor();

//        设置请求的页面大于最大页操作，true调回到首页，false继续请求，默认false
        paginationInterceptor.setOverflow(true);
//        paginationInterceptor.setOverflow(false)
//        设置最大单页数量限制，默认500条，-1不受限制
        paginationInterceptor.setLimit(1000);

        return paginationInterceptor;
    }

}
