package com.zyj.gulimall.cart.controller;

import com.zyj.common.utils.R;
import com.zyj.gulimall.cart.vo.Cart;
import com.zyj.gulimall.cart.vo.CartItem;
import com.zyj.gulimall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @author lulx
 * @date 2022-01-22 23:51
 **/
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public R getCurrentUserCartItems() {
//        cartService.getCurrentUserCartItems();
        return R.ok();
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.updateCountItem(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
        cartService.updateCheckItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 浏览器cookie:user-key：表示用户身份，一个月后过期；
     * 如果第一次使用购物车功能，都会给一个临时的用户身份
     * <p>
     * 登录：session有
     * 没登录：按照cookie里面带来的user-key
     * 第一次：如果没有临时用户，创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * RedirectAttributes redirectAttributes
     * redirectAttributes.addFlashAttribute() 将数据放在session里面可以在页面取出，但是只能取一次
     * redirectAttributes.addAttribute() 将数据放在url后面
     *
     * @param skuId
     * @param num
     * @param redirectAttributes
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
        //再次查询购物车数据
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item", item);
        return "success";
    }
}
