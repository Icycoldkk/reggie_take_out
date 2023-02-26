package com.lkl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.common.BaseContext;
import com.lkl.common.CustomException;
import com.lkl.entity.*;
import com.lkl.mapper.OrdersMapper;
import com.lkl.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService
{
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submit(Orders orders)
    {
        Long user_id = BaseContext.getCurrentId();

        Long orderId = IdWorker.getId();
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user_id);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        BigDecimal sum =new BigDecimal(0);
        List<OrderDetail> list1 = new ArrayList<>();
        for (ShoppingCart shoppingCart : list)
        {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setNumber(shoppingCart.getNumber());
            if(shoppingCart.getDishId()!= null)
            {
                orderDetail.setDishId(shoppingCart.getDishId());
                orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            }else
            {
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
            }
            list1.add(orderDetail);
            sum = sum.add(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())));

        }

        if(list == null || list.size() == 0 )
            throw new CustomException("购物车为空");

        User user = userService.getById(user_id);

        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null)
            throw  new CustomException("地址信息为空");

        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(sum);
        orders.setUserId(user_id);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ?"": addressBook.getProvinceName ())+ (addressBook.getCityName ()  ==null ? "" : addressBook.getCityName())+
        (addressBook.getDistrictName() == null ?  "": addressBook.getDistrictName())+ (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        this.save(orders);
        orderDetailService.saveBatch(list1);

        shoppingCartService.remove(queryWrapper);
    }
}
