package com.zqq.thirdpart;

import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ThirdPartApplicationTests {

    @Autowired
    OSSClient ossClient;

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

        FileInputStream fileInputStream = new FileInputStream("/Users/zhaoqianqian/Downloads/pic/智学OJ.png");

        ossClient.putObject("l-oj-test","test.jpg",fileInputStream);

        ossClient.shutdown();

        System.out.println("上传成功");


    }

}
