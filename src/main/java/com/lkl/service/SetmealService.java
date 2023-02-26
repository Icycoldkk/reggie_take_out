package com.lkl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkl.dto.SetmealDto;
import com.lkl.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal>
{
    void saveWithDishes(SetmealDto setmealDto);

    SetmealDto getByIdWithDishes(Setmeal setmeal,String categoryName);

    void updateWithDishes(SetmealDto setmealDto);

    void removeWithDishes(List<Long> ids);
}
