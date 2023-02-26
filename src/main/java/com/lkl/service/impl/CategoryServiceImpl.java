package com.lkl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.common.CustomException;
import com.lkl.entity.Category;
import com.lkl.entity.Dish;
import com.lkl.entity.Setmeal;
import com.lkl.mapper.CategoryMapper;
import com.lkl.service.CategroyService;
import com.lkl.service.DishService;
import com.lkl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategroyService
{
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id)
    {
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("category_id",id);
        long count = dishService.count(dishQueryWrapper);
        if(count > 0)
            throw new CustomException("该分类关联菜品,无法删除");
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id",id);
        count = setmealService.count(setmealQueryWrapper);
        if(count > 0)
            throw new CustomException("该分类关联套餐，无法删除");
        super.removeById(id);
    }
}
