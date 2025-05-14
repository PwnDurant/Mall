package com.zqq.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 1，要远程调用别的服务
 *  1）引入 openFeign
 *  2）编写接口，告诉 SpringCloud 这个接口需要调用远程服务
 *      1,声明接口每一个方法都是调用哪一个远程服务的哪一个请求
 *  3）开启远程调用功能
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.zqq.member.feign") //远程调用接口所在的包
public class MallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMemberApplication.class, args);
    }

}
