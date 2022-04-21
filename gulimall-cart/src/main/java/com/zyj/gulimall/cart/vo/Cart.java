package com.zyj.gulimall.cart.vo;

import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 *
 * @author lulx
 * @date 2022-01-22 23:48
 **/
@Setter
public class Cart {
    /** 商品项列表 */
    private List<CartItem> items;
    /** 商品数量 */
    private Integer countNum;
    /** 商品类型数量 */
    private Integer countType;
    /** 商品总价 */
    private BigDecimal totalAmount;
    /** 减免价格 */
    private BigDecimal reduce = new BigDecimal(0);

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            count = items.size();
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        //计算购物项总价
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                if (item.getCheck()) {
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        //减去优惠总价
        amount = amount.subtract(getReduce());
        return amount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public List<CartItem> getItems() {
        return items;
    }
}
