package com.zqq.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zqq.product.productDB.entity.BrandEntity;
import com.zqq.product.productDB.service.BrandService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MallProductApplicationTests {

	@Autowired
	private BrandService brandService;

	@Test
	public void contextLoads() {

		BrandEntity brandEntity = new BrandEntity();
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



	}

}
