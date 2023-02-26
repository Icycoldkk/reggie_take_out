package com.lkl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkl.entity.Orders;
import org.springframework.core.annotation.Order;

public interface OrdersService extends IService<Orders>
{
    public void submit(Orders orders);
}
