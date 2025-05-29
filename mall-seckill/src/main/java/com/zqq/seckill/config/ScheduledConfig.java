package com.zqq.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync   //开启异步功能
@EnableScheduling   //开启定时调度功能
@Configuration
public class ScheduledConfig {



}
