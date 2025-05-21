package com.zqq.product.productDB.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zqq.common.constant.ProductConstant;
import com.zqq.product.productDB.dao.AttrAttrgroupRelationDao;
import com.zqq.product.productDB.dao.AttrGroupDao;
import com.zqq.product.productDB.dao.CategoryDao;
import com.zqq.product.productDB.entity.AttrAttrgroupRelationEntity;
import com.zqq.product.productDB.entity.AttrGroupEntity;
import com.zqq.product.productDB.entity.CategoryEntity;
import com.zqq.product.productDB.service.CategoryService;
import com.zqq.product.vo.AttrGroupRelationVO;
import com.zqq.product.vo.AttrRespVO;
import com.zqq.product.vo.AttrVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.AttrDao;
import com.zqq.product.productDB.entity.AttrEntity;
import com.zqq.product.productDB.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
@Slf4j
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
//        保存基本数据
        this.save(attrEntity);
//        保存关联关系
        if(attr.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()&& attr.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(attrType)?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){
            wrapper.and((wrapper1)->{
                wrapper1.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils=new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVO> respVOs = records.stream().map((attrEntity) -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVO);

            if(attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
                //            设置分类和分组的名字
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if("base".equalsIgnoreCase(attrType)){
                    if (attrId != null && attrId.getAttrGroupId()!=null) {
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                        attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }


            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }
            return attrRespVO;
        }).collect(Collectors.toList());
        pageUtils.setList(respVOs);
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrRespVO respVO=new AttrRespVO();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,respVO);

//        设置分组信息
        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if(relationEntity!=null){
            respVO.setAttrGroupId(relationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
            if(attrGroupEntity!=null){
                respVO.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
//        设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVO.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity!=null){
            respVO.setCatelogName(categoryEntity.getName());
        }
        return respVO;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        if(attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //        修改分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if(count>0){
                relationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else{
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组Id 找到组内关联的所有属性
     * @param attrgroupId 分组Id
     * @return 返回关联的属性，而且是基本属性 --> 规格参数
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if(attrIds.isEmpty()){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>)attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
//        relationDao.delete(new QueryWrapper<>().eq("attr_id",).eq("attr_group_id",))
//                最好是批量删除
        List<AttrAttrgroupRelationEntity> relationEntities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(relationEntities);
    }

    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
//        1,当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity groupEntity = attrGroupDao.selectById(attrgroupId);
//        根据分组Id找到自己所在的分类
        Long catelogId = groupEntity.getCatelogId();
//        2,当前分组只能关联别的分组没有引用的属性
//        1),查出当前分类下的其他分组Id
        List<AttrGroupEntity> groups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        List<Long> groupIds = groups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
//        2),查出这些分组所关联的属性
        List<AttrAttrgroupRelationEntity> attrGroupId = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
//        查出所有被关联过的属性
        List<Long> attrIds = attrGroupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
//        3),从当前分类的所有属性移除这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(!attrIds.isEmpty()){
            wrapper.notIn("attr_id",attrIds);
        }
//        判断是否有参数进行模糊查询
        String key=(String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page=this.page(new Query<AttrEntity>().getPage(params),wrapper);
        return new PageUtils(page);
    }

    /**
     * 在指定的所有属性集合里面挑出检索属性
     * @param attrIds
     * @return
     */
    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }

}