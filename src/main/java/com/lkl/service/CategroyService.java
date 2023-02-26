package com.lkl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkl.entity.Category;
import org.springframework.stereotype.Service;

public interface CategroyService extends IService<Category>
{
    public void remove(Long id);
}
