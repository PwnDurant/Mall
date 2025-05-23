package com.zqq.product.web;


import com.zqq.product.productDB.entity.CategoryEntity;
import com.zqq.product.productDB.service.CategoryService;
import com.zqq.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){

//        TODO 1，查出所有的一级分类
        List<CategoryEntity> categoryEntityList=categoryService.getLevelOneCatagorys();

//        利用试图解析器进行拼字符串
//        classpath:/templates/    .html
        model.addAttribute("categorys",categoryEntityList);  //给页面放一个这个属性
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
//        获得一把锁 只要锁的名字一样就是一把锁
        RLock lock = redissonClient.getLock("my-lock");
//        加锁
//        lock.lock();  //阻塞式等待，默认加的锁都是 30s 时间
//        1)，锁的自动续期，如果业务超长，运行期间自动给锁续上新的 30s 不用担心业务时间长，锁自动过期被删除
//        2)，加锁的业务只要运行完成，就不会给当前续期，即使不手动解锁，默认在 30s 之后自动删除
//        lock.lock(10, TimeUnit.SECONDS);   10秒自动解锁，自动解锁时间一定要大于业务执行时间
//        1，如果传递了锁的超时时间就会发送给 redis 执行脚本，进行占锁，默认超时时间就是我们指定的时间
//        2，如果未指定锁的超时时间，就使用 30*1000 【LockWatchTimeout 看门狗的默认时间】
//         只要占锁成功就会触发启动一个定时任务 【重新给锁设置过期时间，新的过期时间是看门狗默认时间】
//         internalLockLeaseTime【看门狗时间】/3，10s
        lock.lock(30,TimeUnit.SECONDS);
//        最佳实战
//        1）lock.lock(30, TimeUnit.SECONDS);   省掉了整个续期操作
        try{
            System.out.println("加锁成功，执行业务......"+Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {

        }finally {
//            解锁， 假设解锁代码没有执行，redisson 也不会出现死锁
            System.out.println("释放锁..."+Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    @GetMapping("/write")
    @ResponseBody
    public String writeValue(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String s="";
        RLock rLock = readWriteLock.writeLock();
        try{
//            改数据加写锁，读数据加读锁
            rLock.lock();
            s= UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue",s);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String s="";
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try{
            s= (String) redisTemplate.opsForValue().get("writeValue");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 车库停车
     * 3个车位
     * 信号量也可以做粉分布式限流
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException{
        RSemaphore park = redissonClient.getSemaphore("park");
//        park.acquire(); 获取一个信号，获取一个值占一个车位 如果没获取到就会一直等待
        boolean b = park.tryAcquire();  //如果没获取到就会返回 false ，不影响程序执行
        if(b){
//            执行业务
        }else{
            return "error";
        }
        return "ok=>"+b;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException{
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release(); //释放一个车位
        return "ok";
    }

    /**
     * 要等全部的锁都被拿走才可以执行
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException{
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();
        return "放假了...";
    }

    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown(); //计数减一
        return id+"班的人都走了...";
    }
}
