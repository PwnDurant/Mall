package com.zqq.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 * 	2，逻辑删除
 * 	1）配置全局的逻辑删除规则
 * 	2）配置逻辑删除组件
 * 	3）加上逻辑删除注解
 *
 * 	3，JSR303
 * 	1),给Bean添加校验注解,并定义消息提示
 * 	2),开启校验注解 @Valid 校验错误之后就会有默认响应
 * 	3),可以紧跟一个BindingResult就可以获取到校验结果
 *
 * 	4,统一异常处理
 * 	1）使用 @ControllerAdvice
 *  2）分组校验,默认没有指定的分组校验的注解不生效（多场景的复杂校验)
 *
 *  5,自定义校验注解
 *  1),编写一个自定义的校验注解
 *  2),编写一个自定义的校验器
 *  3),关联校验器和校验注解
 *
 *  页面开发：
 *  1）引入模版引擎 thymeleaf-starter
 *  2）按照要求静态资源都放在 static 文件夹下就可以直接按照路径访问
 *  3）页面放在 templates 下也可以直接访问
 *  4）SpringBoot 访问项目的时候默认会找 index
 *  5）不重启服务器实时更新:
 *  	1)引入 dev-tools
 *  	2)修改完页面 controller shift f9 重新自动编译页面，如果是代码配置还是推荐重启
 */
@EnableFeignClients(basePackages = "com.zqq.product.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class MallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallProductApplication.class, args);
	}

}
