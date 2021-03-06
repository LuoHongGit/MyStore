package cn.lh.cart.controller;

import cn.lh.cart.pojo.Cart;
import cn.lh.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     * @param cart
     * @return
     */
    @PostMapping("/addCart")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        Boolean result = cartService.addCart(cart);

        if(!result){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 查询当前用户的购物车
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList() {
        List<Cart> carts = this.cartService.queryCartList();
        if (carts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购物车
     * @param cart
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.cartService.updateCarts(cart);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除购物车中的商品
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }

}
