package com.lkl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.entity.ShoppingCart;
import com.lkl.mapper.ShoppingCartMapper;
import com.lkl.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService
{
}
