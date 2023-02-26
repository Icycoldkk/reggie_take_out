package com.lkl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lkl.common.BaseContext;
import com.lkl.common.R;
import com.lkl.entity.ShoppingCart;
import com.lkl.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController
{
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart)
    {
        shoppingCart.setUserId(BaseContext.getCurrentId());

        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",shoppingCart.getUserId());
        if(shoppingCart.getDishId() != null)
        {
            queryWrapper.eq("dish_id",shoppingCart.getDishId());
//            queryWrapper.eq("dish_flavor",shoppingCart.getDishFlavor());

        }else
        {
            queryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one != null)
        {
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }else
        {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list()
    {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",BaseContext.getCurrentId());
        queryWrapper.orderByDesc("create_time");
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/sub")
    public R<List<ShoppingCart>> sub(@RequestBody ShoppingCart shoppingCart)
    {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        if(shoppingCart.getDishId() != null)
        {
            queryWrapper.eq("dish_id",shoppingCart.getDishId());
        }else
        {
            queryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one.getNumber() == 1)
            shoppingCartService.remove(queryWrapper);
        else
        {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }
        return list();
    }

    @DeleteMapping("/clean")
    public R<List<ShoppingCart>> clean()
    {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return list();
    }
}
