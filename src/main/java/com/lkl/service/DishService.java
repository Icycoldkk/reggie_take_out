package com.lkl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkl.dto.DishDto;
import com.lkl.entity.Dish;
import com.lkl.entity.DishFlavor;

import java.util.List;

public interface DishService extends IService<Dish>
{
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavors(List<Long> ids);
}
