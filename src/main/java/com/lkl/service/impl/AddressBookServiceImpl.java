package com.lkl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.entity.AddressBook;
import com.lkl.mapper.AddressBookMapper;
import com.lkl.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService
{

}
