package com.zqq.product.productDB.service.impl;

import com.zqq.product.productDB.entity.AttrEntity;
import com.zqq.product.productDB.service.AttrService;
import com.zqq.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.AttrGroupDao;
import com.zqq.product.productDB.entity.AttrGroupEntity;
import com.zqq.product.productDB.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 如果没有三级分类就传入0
     * @param params
     * @param catalogId
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catalogId) {

        String key = (String)params.get("key");
//            按照三级分类查 select * from pms_attr_group catalog_id=? and (attr_group_id = key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        if(catalogId==0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else {
            wrapper.eq("catelog_id",catalogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类Id查出所有的分组以及这里面的属性
     * @param cateId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long cateId) {
//        1，查询分组信息
//        查出所有分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cateId));
//        查处组里面的所有属性
        List<AttrGroupWithAttrsVO> collect = attrGroupEntities.stream().map(item -> {
            AttrGroupWithAttrsVO withAttrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(item,withAttrsVO);
            List<AttrEntity> relationAttrs = attrService.getRelationAttr(withAttrsVO.getAttrGroupId());
            withAttrsVO.setAttrs(relationAttrs);
            return withAttrsVO;
        }).collect(Collectors.toList());
        return collect;
    }

}