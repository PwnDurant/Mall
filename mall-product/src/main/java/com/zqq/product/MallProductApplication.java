package com.zqq.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1，整合MyBatis-Plus
 * 	1）导入依赖
 * 	2）配置
 * 		1，数据源
 * 			1）导入数据库驱动
 * 			2）配置数据源
 * 		2，配置Mybatis-Plus相关信息
 * 			1）使用 @MapperScan
 * 			2）告诉映射文件位置
 */
@MapperScan("com.zqq.product.productDB.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class MallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallProductApplication.class, args);
	}

}
