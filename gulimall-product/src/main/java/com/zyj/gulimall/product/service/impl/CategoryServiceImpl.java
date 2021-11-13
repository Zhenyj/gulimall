package com.zyj.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.utils.PageUtils;
import com.zyj.common.utils.Query;
import com.zyj.gulimall.product.dao.CategoryDao;
import com.zyj.gulimall.product.entity.CategoryEntity;
import com.zyj.gulimall.product.service.CategoryBrandRelationService;
import com.zyj.gulimall.product.service.CategoryService;
import com.zyj.gulimall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 三级分类菜单
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        // 1、查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2、组装成树形结构
        List<CategoryEntity> levelMenus = entities.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            // 排序，避免空指针
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        //collect(Collectors.toList())表示收集流数据并转换为List
        return levelMenus;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            // 1、递归查找子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            // 2、排序，避免空指针
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 批量删除菜单
     *
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前册除的菜单，是否被别的地方引用

        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        findParentPath(paths, catelogId);
        // 逆序,[父,子,孙]
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    // [孙,子,父]
    private void findParentPath(List<Long> paths, Long catelogId) {
        // 1、查找父id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(paths, byId.getParentCid());
        }
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (StringUtils.hasText(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                .eq("parent_cid", 0));
        if (CollectionUtils.isEmpty(categoryEntities)) {
            log.error(BizCodeEnum.PRODUCT_CATEGORY_EXCEPTION.getMsg());
            throw new RuntimeException(BizCodeEnum.PRODUCT_CATEGORY_EXCEPTION.getMsg());
        }
        return categoryEntities;
    }

    /**
     * TODO 会产生对外内存溢出:OutOfDirectMemoryError
     * 1) 、springboot2.e以后默认使用Lettuce作为操作redis的客户端。它使用netty进行网络通信。
     * 2) 、lettuce的bug导致netty堆外内存溢出-Xmx300m;
     * netty如果没有指定堆外内存，默认使用-Xmx300m可以通过-Dio.netty.maxDirectMemory进行设置
     * 解决方案:不能使用-Dio.netty.maxDirectNemory只去调大堆外内存。
     * 1)、升级lettuce客户端。
     * 2)、切换使用jedis
     */
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        //给缓存中放json字符串，拿出的json字符串，还用逆转为能用的对象类型;【序列化与反序列化】
        /**
         * 1、空结果缓存:解决缓存穿透
         * 2、设置过期时间(加随机值):解决缓存雪崩
         * 3、加锁:解决缓存击穿
         */
        // 1、缓存
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.hasLength(catalogJSON)) {
            // 缓存中没有数据,查询数据库
            System.out.println("缓存不命中...准备查询数据库...");
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            return catalogJsonFromDb;
        }

        // 缓存中有数据,转为指定的对象
        System.out.println("命中缓存...直接返回...");
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,
                new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
        return result;
    }

    /**
     * 从数据库查询分类数据
     * 使用本地锁
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        // TODO 本地锁: synchronized，juc (Lock)，在分布式情况下，想要锁住所有，必须使用分布式锁
        synchronized (this) {
            //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
            if (StringUtils.hasLength(catalogJSON)) {
                // 缓存不为空直接返回
                Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,
                        new TypeReference<Map<String, List<Catalog2Vo>>>() {
                        });
                return result;
            }
            System.out.println("查询数据库...");
            List<CategoryEntity> selectList = baseMapper.selectList(null);

            // 查询所有一级分类
            List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

            // 封装数据
            Map<String, List<Catalog2Vo>> Catalog2VoMap = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                // 查询二级分类
                List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getParentCid());
                // 封装结果
                List<Catalog2Vo> Catalog2Vos = null;
                if (!CollectionUtils.isEmpty(categoryEntities)) {
                    Catalog2Vos = categoryEntities.stream().map(l2 -> {
                        // 查找当前二级分类的三级分类数据
                        List<CategoryEntity> categoryEntities1 = getParentCid(selectList, l2.getParentCid());
                        List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
                        if (!CollectionUtils.isEmpty(categoryEntities1)) {
                            catalog3Vos = categoryEntities1.stream().map(l3 -> {
                                Catalog2Vo.Catalog3Vo Catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(),
                                        l3.getCatId().toString(), l3.getName());
                                return Catalog3Vo;
                            }).collect(Collectors.toList());
                        } else {
                            log.info("当前分类:{},没有三级分类", l2.getCatId());
                        }

                        Catalog2Vo Catalog2Vo = new Catalog2Vo(v.getCatId().toString(), catalog3Vos,
                                l2.getCatId().toString(), l2.getName());
                        return Catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return Catalog2Vos;
            }));
            // 将查询出来的数据加入到缓存中,讲对象转为JSON字符串放入缓存,JSON跨语言、跨平台
            // 在锁里将数据放入缓存中，因为放入缓存时发生IO有延迟可能会导致多次查询数据库
            String s = JSON.toJSONString(Catalog2VoMap);
            redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
            return Catalog2VoMap;
        }
    }

    /**
     * 从数据库查询分类数据
     * 使用redis分布式锁
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        // 1、占用分布式锁,setIfAbsent("lock","111");对应redis命令中的set lock 111 NX
        // set key value nx 如果key不存在，将key设置值为value
        // 是否占用成功
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue()
                .setIfAbsent("lock", uuid,100,TimeUnit.SECONDS);
        // setIfAbsent("lock", "111",100,TimeUnit.SECONDS) ==> set lock 111 EX 100 NX
        if(lock){
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catalog2Vo>> dataFromDb = null;
            try{
                dataFromDb = getDataFromDb();
            }finally {
                // 执行成功后,删除锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                // 删除成功返回1，失败返回0
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            return dataFromDb;
        }else{
            System.out.println("获取分布式锁失败...等待重试");
            // 未获取锁,重试,设置休眠时间100ms
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.hasLength(catalogJSON)) {
            // 缓存不为空直接返回
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,
                    new TypeReference<Map<String, List<Catalog2Vo>>>() {
                    });
            return result;
        }
        System.out.println("查询数据库中...");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 查询所有一级分类
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        // 封装数据
        Map<String, List<Catalog2Vo>> Catalog2VoMap = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查询二级分类
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getParentCid());
            // 封装结果
            List<Catalog2Vo> Catalog2Vos = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                Catalog2Vos = categoryEntities.stream().map(l2 -> {
                    // 查找当前二级分类的三级分类数据
                    List<CategoryEntity> categoryEntities1 = getParentCid(selectList, l2.getParentCid());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
                    if (!CollectionUtils.isEmpty(categoryEntities1)) {
                        catalog3Vos = categoryEntities1.stream().map(l3 -> {
                            Catalog2Vo.Catalog3Vo Catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(),
                                    l3.getCatId().toString(), l3.getName());
                            return Catalog3Vo;
                        }).collect(Collectors.toList());
                    } else {
                        log.info("当前分类:{},没有三级分类", l2.getCatId());
                    }

                    Catalog2Vo Catalog2Vo = new Catalog2Vo(v.getCatId().toString(), catalog3Vos,
                            l2.getCatId().toString(), l2.getName());
                    return Catalog2Vo;
                }).collect(Collectors.toList());
            }
            return Catalog2Vos;
        }));
        // 将查询出来的数据加入到缓存中,讲对象转为JSON字符串放入缓存,JSON跨语言、跨平台
        // 在锁里将数据放入缓存中，因为放入缓存时发生IO有延迟可能会导致多次查询数据库
        String s = JSON.toJSONString(Catalog2VoMap);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return Catalog2VoMap;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> categoryEntities, Long parentCid) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> {
            return item.getParentCid() == parentCid;
        }).collect(Collectors.toList());
        return collect;
    }
}