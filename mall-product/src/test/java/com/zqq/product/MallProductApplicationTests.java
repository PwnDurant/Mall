package com.zqq.product;

import com.zqq.product.productDB.service.BrandService;
import com.zqq.product.productDB.service.impl.CategoryServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.UUID;


/**
 * 引入 oss 依赖
 * 配置 key，endpoint 相关信息
 * 使用 OSSClient 进行相关操作
 */
@SpringBootTest
@RunWith(SpringRunner.class)  //使用 Spring 的测试运行器来运行测试，并完成依赖注入
public class MallProductApplicationTests {

	@Autowired
	private BrandService brandService;
    @Autowired
    private CategoryServiceImpl categoryService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedissonClient redisson;

	@Test
	public void redisson(){
		System.out.println(redisson);

	}

//	@Autowired
//	OSS ossClient;

	@Test
	public void contextLoads() throws FileNotFoundException {

//		BrandEntity brandEntity = new BrandEntity();
//		brandEntity.setDescript("cehsi");
//		brandEntity.setName("测试");
//		brandService.save(brandEntity);
//		System.out.println("保存成功");

//		brandEntity.setBrandId(18L);
//		brandEntity.setDescript("ceshi");
//		brandService.updateById(brandEntity);

//		List<BrandEntity> brandId = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 18L));
//		brandId.forEach((item)->{
//			System.out.println(item);
//		});
//		FileInputStream fileInputStream = new FileInputStream("/Users/zhaoqianqian/Downloads/pic/智学OJ.png");
//		ossClient.putObject("l-oj-test","test.jpg",fileInputStream);
//		ossClient.shutdown();
//		System.out.println("上传成功");
		System.out.println(Arrays.toString(categoryService.findCatelogPath(225L)));
	}

	@Test
	public void testRedis(){
		ValueOperations<String, String> forValue = stringRedisTemplate.opsForValue();
//		保存数据
		forValue.set("hello","world_"+ UUID.randomUUID());
//		查询数据
		String s = forValue.get("hello");
		System.out.println("之前保存的数据："+s);
	}

}
