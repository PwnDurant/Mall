package com.zqq.product.productDB.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zqq.product.productDB.service.CategoryBrandRelationService;
import com.zqq.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.common.utils.PageUtils;
import com.zqq.common.utils.Query;

import com.zqq.product.productDB.dao.CategoryDao;
import com.zqq.product.productDB.entity.CategoryEntity;
import com.zqq.product.productDB.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private Map<String,Object> cache=new HashMap<>();

//    @Autowired
//    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

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
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {

//        查处所有分类
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        return entityList.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
//                找到子菜单
                .peek((menu) -> menu.setChildren(getChildren(menu, entityList)))
//                菜单排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

    }

    /**
     * 批量删除数据
     *
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
     *
     * @param catelogId
     * @return 【2，25，225】
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> paths = new ArrayList<>();
        List<Long> path = findParentPath(catelogId, paths);

//        Collections.reverse(path); //这个是逆序链表的

        return path.toArray(new Long[path.size()]);

    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
       //缓存实效模式使用
//    @Caching(evict = {
//            @CacheEvict(value = "category", key = "'getLevelOneCatagorys'"),
//            @CacheEvict(value = "category", key = "'getCatalogJson'")
//    }) //组合删除
    @CacheEvict(value = "category",allEntries = true)  //删除某个分区下所有的数据
//    存储同一类型的数据，都可以指定成同一个分区 分区名默认就是缓存前缀
//    @CachePut //双写模式，删除完之后，把这次的结果再返放入缓存中
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

    }

    /**
     * 查询一级分类
     *
     * @return
     */
//    每一个要缓存的数据需要指定放到哪一个名字的缓存 【缓存分区（按照业务类型分）】
    @Cacheable(value = {"category"}, key = "'getLevelOneCatagorys'",sync = true) //代表当前方法的结果需要缓存，如果缓存中存在，方法就不用调用
    @Override
    public List<CategoryEntity> getLevelOneCatagorys() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        long l1 = System.currentTimeMillis();
        System.out.println("时间：" + (l1 - l));
        return entities;
    }

    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> levelOneCatagorys = getParentCid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parentCid = levelOneCatagorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(L2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, L2.getCatId().toString(), L2.getName());
                    List<CategoryEntity> levelThird = getParentCid(selectList, L2.getCatId());
                    if (!levelThird.isEmpty()) {
                        List<Catelog2Vo.Category3Vo> collect = levelThird.stream().map(L3 -> {
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(L2.getCatId().toString(), L3.getCatId().toString(), L3.getName());
                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parentCid;
    }

    //    TODO 会产生堆外内存溢出异常 OutOfDirectMemoryError
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        /**
         * 1，空结果缓存（缓存穿透）
         * 2，设置 TTL（加随机值）
         * 3，加锁（缓存击穿）
         */
//        加入缓存逻辑，缓存中存入数据是 JSON 字符串，跨语言跨平台兼容
        ValueOperations<String, String> forValue = redisTemplate.opsForValue();
        String s = forValue.get("catalogJSON");
        if (StringUtils.isEmpty(s)) {
//            缓存中没有数据，查询数据库
            Map<String, List<Catelog2Vo>> fromDB = getCatalogJsonFromDB();
            return fromDB;
        }
//        要将JSON逆转（反序列化）
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(s, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return result;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock() throws InterruptedException {
//        锁的名字：锁的粒度，越细越快。 product-11-lock product-12-lock
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDB = null;
        try {
            dataFromDB = getDataFromDB();
        } finally {
            lock.unlock();
        }
        return dataFromDB;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() throws InterruptedException {
//        1，占分布式锁，去 redis 占坑  set XXX NX
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "111");
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            System.out.println("获取分布式锁成功");
//            加锁成功  执行业务
//            设置过期时间,必须和加锁是同步的保证原子性
//            redisTemplate.expire("lock",30,TimeUnit.SECONDS);  //30秒过后这个锁也会自动删除，即使执行一半出现问题了后面的线程还是会拿到锁，避免了死锁的问题
            Map<String, List<Catelog2Vo>> dataFromDB = null;
            try {   //保证就算业务时间很长也能释放锁不会还没处理完就自动过期了
                dataFromDB = getDataFromDB();
            } finally {
                String script = "if redis.call('get,KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);//原子删除锁
            }
//            先获取值对比，获取成功删除，所以必须是原子操作，需要使用脚本 -》 lua 脚本
//            String s = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(s)){
////                中间这段返回锁的值的时候会发生可见性问题，返回数据之后，刚好过期，这时候虽然相同但还是删除的还是别的锁
////                删除自己的锁
//                redisTemplate.delete("lock");  //删除锁
//            }
            return dataFromDB;
        } else {
//            加锁失败  重试
//            也可以休眠 100ms 重试
            Thread.sleep(200);
            System.out.println("获取分布式锁失败");
            return getCatalogJsonFromDBWithRedisLock();   //自旋的方式
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        synchronized (this) {
            ValueOperations<String, String> forValue = redisTemplate.opsForValue();
            String s = forValue.get("catalogJSON");
            if (!StringUtils.isEmpty(s)) {
                return JSON.parseObject(s, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
            }
            List<CategoryEntity> selectList = baseMapper.selectList(null);
            List<CategoryEntity> levelOneCatagorys = getParentCid(selectList, 0L);
            Map<String, List<Catelog2Vo>> parentCid = levelOneCatagorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (categoryEntities != null) {
                    catelog2Vos = categoryEntities.stream().map(L2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, L2.getCatId().toString(), L2.getName());
                        List<CategoryEntity> levelThird = getParentCid(selectList, L2.getCatId());
                        if (!levelThird.isEmpty()) {
                            List<Catelog2Vo.Category3Vo> collect = levelThird.stream().map(L3 -> new Catelog2Vo.Category3Vo(L2.getCatId().toString(), L3.getCatId().toString(), L3.getName())).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect);
                        }
                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            String jsonString = JSON.toJSONString(parentCid);
            forValue.set("catalogJSON", jsonString, 1, TimeUnit.DAYS);
            return parentCid;
        }
    }

    /**
     * 从数据库查询并封装整个分类数据
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {

//         如果缓存中有就用缓存的
//        Map<String, List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//        if(cache.get("catalogJson")==null){
//        只要是同一把锁就可以锁住所有线程
//        1，使用 this，SpringBoot 在容器中所有组件都是单例，相当于 1000000请求同时用一个this
//        2，给方法加入 synchronized
//        本地锁只能锁住当前进程，在分布式情况下想要锁住所有必须要加分布式锁
        synchronized (this) {
//               得到锁以后应该再去缓存中确定一次，如果没有才需要继续
            ValueOperations<String, String> forValue = redisTemplate.opsForValue();
            String s = forValue.get("catalogJSON");
            if (!StringUtils.isEmpty(s)) {
//                   缓存不为空，返回
                Map<String, List<Catelog2Vo>> result = JSON.parseObject(s, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return result;
            }
            /**
             * 将数据库的多次查询变为一次
             */
            List<CategoryEntity> selectList = baseMapper.selectList(null);
//        查出所有一级分类
            List<CategoryEntity> levelOneCatagorys = getParentCid(selectList, 0L);
//        封装数据
            Map<String, List<Catelog2Vo>> parentCid = levelOneCatagorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            1,每一个的一级分类，查到这个一级分类的二级分类
                List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
//             2，封装上面的结果
                List<Catelog2Vo> catelog2Vos = null;
                if (categoryEntities != null) {
                    catelog2Vos = categoryEntities.stream().map(L2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, L2.getCatId().toString(), L2.getName());
//                    找当前二级分类的三级分类封装为 VO
                        List<CategoryEntity> levelThird = getParentCid(selectList, L2.getCatId());
                        if (!levelThird.isEmpty()) {
//                        封装为指定格式
                            List<Catelog2Vo.Category3Vo> collect = levelThird.stream().map(L3 -> {
                                Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(L2.getCatId().toString(), L3.getCatId().toString(), L3.getName());
                                return category3Vo;
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect);
                        }

                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
//            cache.put("catalogJson",parentCid);
            //            将查到数据放入缓存,将对象转换为JSON放入
            String jsonString = JSON.toJSONString(parentCid);
            forValue.set("catalogJSON", jsonString, 1, TimeUnit.DAYS);
            return parentCid;
//        }
        }
//        return catalogJson;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parent_cid) {
        return selectList.stream().filter(item -> Objects.equals(item.getParentCid(), parent_cid)).collect(Collectors.toList());
    }

    //    2,25,225
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
//            先查出当前分类的信息
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
//            说明他有一个有效的父分类
            findParentPath(byId.getParentCid(), paths);
        }
        //        收集当前节点Id (最外层节点Id)
        paths.add(catelogId);
        return paths;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
//                找到子菜单
                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, all)))
//                菜单的排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

}

