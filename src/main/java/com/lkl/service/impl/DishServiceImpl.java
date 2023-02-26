package com.lkl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkl.common.CustomException;
import com.lkl.dto.DishDto;
import com.lkl.entity.Dish;
import com.lkl.entity.DishFlavor;
import com.lkl.mapper.DishMapper;
import com.lkl.service.DishFlavorService;
import com.lkl.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService
{

    @Autowired
    private DishFlavorService dishFlavorService;


    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto)
    {
        this.save(dishDto);

        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors)
        {
            flavor.setDishId(id);
        }

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id)
    {
        Dish dish = getById(id);
        DishDto dishDto = new DishDto();
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto)
    {
        this.updateById(dishDto);

        Long id = dishDto.getId();
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",id);
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors)
        {
            flavor.setDishId(id);
        }

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeWithFlavors(List<Long> ids)
    {
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.in("id",ids);
        dishQueryWrapper.eq("status",1);
        if(this.count(dishQueryWrapper) > 0)
            throw new CustomException("菜品正在售卖，无法删除");
        this.removeByIds(ids);

        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("dish_id",ids);
        dishFlavorService.remove(queryWrapper);

    }
}
