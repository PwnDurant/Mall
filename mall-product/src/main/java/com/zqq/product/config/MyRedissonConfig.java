package com.zqq.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {
    /**
     * 所有对 Redisson 的使用都是通过 RedissonClient 对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException{
//        1，创建配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://123.56.253.179:6380");
//        2，根据 config 创建出 RedissonClient 示例
        return Redisson.create(config);
    }
}
