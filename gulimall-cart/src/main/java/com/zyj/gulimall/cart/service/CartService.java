package com.zyj.gulimall.cart.service;

import com.zyj.gulimall.cart.vo.Cart;
import com.zyj.gulimall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author lulx
 * @date 2022-01-22 23:52
 **/
public interface CartService {

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车商品项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 勾选购物项
     *
     * @param skuId
     * @param check
     */
    void updateCheckItem(Long skuId, Integer check);

    /**
     * 修改购物项数量
     *
     * @param skuId
     * @param num
     */
    void updateCountItem(Long skuId, Integer num);

    /**
     * 删除商品项
     * @param skuId
     */
    void deleteItem(Long skuId);

    List<CartItem> getCurrentUseCartItem();
}
