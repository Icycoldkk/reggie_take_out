package com.lkl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.common.CustomException;
import com.lkl.dto.SetmealDto;
import com.lkl.entity.Dish;
import com.lkl.entity.DishFlavor;
import com.lkl.entity.Setmeal;
import com.lkl.entity.SetmealDish;
import com.lkl.mapper.SetmealMapper;
import com.lkl.service.CategroyService;
import com.lkl.service.SetmealDishService;
import com.lkl.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService
{
    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDishes(SetmealDto setmealDto)
    {
        save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes)
        {
            setmealDish.setSetmealId(setmealDto.getId());

        }
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public SetmealDto getByIdWithDishes(Setmeal setmeal,String categoryName)
    {
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setCategoryName(categoryName);

        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id",setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto)
    {
        this.updateById(setmealDto);

        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id",setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes)
        {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeWithDishes(List<Long> ids)
    {
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.in("id",ids);
        setmealQueryWrapper.eq("status",1);
        if(this.count(setmealQueryWrapper) > 0)
            throw new CustomException("套餐正在售卖，无法删除");
        this.removeByIds(ids);

        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("setmeal_id",ids);
        setmealDishService.remove(queryWrapper);
    }
}
