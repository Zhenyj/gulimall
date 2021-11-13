package com.zyj.gulimall.product.web;

import com.zyj.gulimall.product.entity.CategoryEntity;
import com.zyj.gulimall.product.service.CategoryService;
import com.zyj.gulimall.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author llx
 * @date 2021-11-03 22:12
 **/
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    @RequestMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 查出所有一级分类
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categorys);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    /**
     * 测试redisson
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");
        //2、加锁
//        lock.lock(); // 阻塞式等待。默认加的锁都是30s时间。
        // 1)、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间长，锁自动过期被删掉
        // 2)、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除。
//        lock.lock(10, TimeUnit.SECONDS);  // 10s解锁，自动解锁时间一定要大于业务执行时间
        // 问题: Lock.Lock(10,TimeUnit.SECONDS);在锁时间到了以后，不会自动续期。
        // 1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        // 2、如果我们未指定锁的超时时间，就使用30 * 1000【LockWatchdogTimeout看门狗的默认时间】;

        // 推荐使用自定义超时时间,省去续期操作。超时时间设大一点,业务时间太长本身就是有问题
        lock.lock(30, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {

        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock(); // 写锁
        String s = "";
        rLock.lock(30, TimeUnit.SECONDS);
        try {
            // 1、改数据加写锁，读数据加读锁
            s = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("writeValue", s);
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock(); // 读锁
        rLock.lock();
        String s = "";
        try {
            s = redisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 信号量
     * 类似停车场车位
     * 信号量也可以用于分布式限流
     * @return
     * @throws InterruptedException
     */
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
//        park.acquire(); // 获取信号量,阻塞方法
        boolean b = park.tryAcquire(); // 尝试获取信号量，不会阻塞
        if(b){
            // 执行业务
        }else{
            return "error";
        }
        return "ok==>" + b;
    }

    @ResponseBody
    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release(); // 释放信号量
        return "ok";
    }

    /**
     * 闭锁
     * 类似学校放假，锁门
     * 所有班级的人都走完了，可以锁大门了
     * @return
     */
    @ResponseBody
    @GetMapping("lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5L);
        door.await(); // 等待闭锁完成
        return "放假了....";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown(); // 计数减一
        return id + "班的人都走了";
    }
}
