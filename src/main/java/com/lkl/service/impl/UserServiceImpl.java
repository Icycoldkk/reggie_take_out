package com.lkl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.entity.User;
import com.lkl.mapper.UserMapper;
import com.lkl.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
}
