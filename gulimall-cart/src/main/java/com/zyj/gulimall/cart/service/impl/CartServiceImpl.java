package com.zyj.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.utils.Constant;
import com.zyj.common.utils.R;
import com.zyj.gulimall.cart.constant.CartConstant;
import com.zyj.gulimall.cart.feign.ProductFeignService;
import com.zyj.gulimall.cart.interceptor.CartInterceptor;
import com.zyj.gulimall.cart.service.CartService;
import com.zyj.gulimall.cart.vo.Cart;
import com.zyj.gulimall.cart.vo.CartItem;
import com.zyj.gulimall.cart.vo.SkuInfoVo;
import com.zyj.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lulx
 * @date 2022-01-22 23:52
 **/
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemString = (String) cartOps.get(skuId.toString());
        CartItem cartItemRed = JSON.parseObject(cartItemString, CartItem.class);
        //无商品时 添加新商品到购物车
        if (cartItemRed == null) {
            //1.远程查询要添加的商品信息
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                    SkuInfoVo data = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItem.setCheck(true);
                    cartItem.setCount(num);
                    cartItem.setImage(data.getSkuDefaultImg());
                    cartItem.setTitle(data.getSkuTitle());
                    cartItem.setSkuId(skuId);
                    cartItem.setPrice(data.getPrice());
                }
            }, executor);
            //2. 远程查询sku属性信息
            CompletableFuture<Void> getSkuSaleAttrValuesTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuSaleAttrValues(skuId);
                if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                    List<String> data = r.getData(new TypeReference<List<String>>() {
                    });
                    cartItem.setSkuAttr(data);
                }
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValuesTask).get();
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            // TODO 临时用户只保存7天时间
            return cartItem;
        } else {
            //购物车有此商品 修改数量
            cartItemRed.setCount(cartItemRed.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItemRed));
            return cartItemRed;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(str, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        // 获取拦截器封装的用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if (userInfoTo.getUserId() != null) {
            //登录
            String cartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserId();
            //如果临时购物车的数据还没有进行合并 进行合并并且清空
            //合并
            String tempCartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null && tempCartItems.size() > 0) {
                for (CartItem item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清空临时购物车
                clearCart(tempCartKey);
            }
            //获取登陆后的购物车数据 合并后的数据
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        } else {
            //没登录
            String cartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void updateCheckItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void updateCountItem(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUseCartItem() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            String cartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            if (cartItems != null && cartItems.size() > 0) {
                cartItems = cartItems.stream().filter(CartItem::getCheck)
                        .map(item -> {
                            R r = productFeignService.getSkuPrice(item.getSkuId());
                            //TODO 1 更新为最新价格
                            if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                                item.setPrice(r.getData(new TypeReference<BigDecimal>() {
                                }));
                            }
                            return item;
                        }).collect(Collectors.toList());
                return cartItems;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            return values.stream().map(obj -> JSON.parseObject((String) obj, CartItem.class))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }

    private void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String cartKey = CartConstant.CART_REDIS_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            // 筛选出被选中的商品项
            cartItems = cartItems.stream().filter(CartItem::getCheck).collect(Collectors.toList());
            // 获取商品实时价格等信息
            List<Long> skuIds = cartItems.stream().map(CartItem::getSkuId).collect(Collectors.toList());
            R r = productFeignService.getSkuInfoBySkuIds(skuIds);
            if (Constant.SUCCESS_CODE.equals(r.getCode())) {
                List<SkuInfoVo> skuInfoVos = r.getData(new TypeReference<List<SkuInfoVo>>() {
                });
                if (skuInfoVos == null || skuInfoVos.size() != skuIds.size()) {
                    log.error(BizCodeEnum.CART_PRODUCT_INFO_EXCEPTION.getMsg());
                    throw new RuntimeException(BizCodeEnum.CART_PRODUCT_INFO_EXCEPTION.getMsg());
                }
                Map<Long, SkuInfoVo> skuInfoVoMap = skuInfoVos.stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, Function.identity()));
                cartItems = cartItems.stream().map(item -> {
                    Long skuId = item.getSkuId();
                    SkuInfoVo skuInfoVo = skuInfoVoMap.get(skuId);
                    if (skuInfoVo == null) {
                        log.error(BizCodeEnum.CART_PRODUCT_INFO_EXCEPTION.getMsg());
                        throw new RuntimeException(BizCodeEnum.CART_PRODUCT_INFO_EXCEPTION.getMsg());
                    }
                    item.setPrice(skuInfoVo.getPrice());
                    return item;
                }).collect(Collectors.toList());

            }
            return cartItems;
        }
    }


}
