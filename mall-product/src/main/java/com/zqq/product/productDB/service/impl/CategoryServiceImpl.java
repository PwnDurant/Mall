package com.zqq.product.productDB.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.CategoryDao;
import com.zqq.product.productDB.entity.CategoryEntity;
import com.zqq.product.productDB.service.CategoryService;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有分类，然后组装成父子的树形结构
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {

//        查处所有分类
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        return entityList.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
//                找到子菜单
                .peek((menu)-> menu.setChildren(getChildren(menu,entityList)))
//                菜单排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

    }

    /**
     * 批量删除数据
     * @param list
     */
    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO 检查当前删除的菜单是否被别的地方引用

//        使用逻辑删除
        baseMapper.deleteBatchIds(list);

    }

    /**
     * 找到catelogId 完整路径
     * @param catelogId
     * @return 【2，25，225】
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> paths=new ArrayList<>();
        List<Long> path = findParentPath(catelogId,paths);

//        Collections.reverse(path); //这个是逆序链表的

        return path.toArray(new Long[path.size()]);

    }

//    2,25,225
    private List<Long> findParentPath(Long catelogId, List<Long> paths){
//            先查出当前分类的信息
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid()!=0){
//            说明他有一个有效的父分类
            findParentPath(byId.getParentCid(),paths);
        }
    //        收集当前节点Id (最外层节点Id)
        paths.add(catelogId);
        return paths;
    }

    /**
     * 递归查找所有菜单的子菜单
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){

        return all.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
//                找到子菜单
                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity,all)))
//                菜单的排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

}