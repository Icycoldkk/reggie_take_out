package com.lkl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkl.common.R;
import com.lkl.dto.DishDto;
import com.lkl.entity.Category;
import com.lkl.entity.Dish;
import com.lkl.entity.DishFlavor;
import com.lkl.service.CategroyService;
import com.lkl.service.DishFlavorService;
import com.lkl.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController
{
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategroyService categroyService;

    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") Long id)
    {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto)
    {
        String key = "dish_"+dishDto.getCategoryId() +"_1";
        redisTemplate.delete(key);
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功");
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto)
    {
        String key = "dish_"+dishDto.getCategoryId() +"_1";
        redisTemplate.delete(key);
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> getListById(Long categoryId)
//    {
//        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("category_id",categoryId);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> getListById(Dish dish)
    {
        String key = "dish_"+dish.getCategoryId() +"_"+dish.getStatus();
        List<DishDto> ret = null;
        ret  = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(ret != null)
            return R.success(ret);

        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",dish.getCategoryId());
        List<Dish> list = dishService.list(queryWrapper);
        ret = new ArrayList<>();
        for (Dish dish1 : list)
        {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            QueryWrapper<DishFlavor> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("dish_id",dish1.getId());
            List<DishFlavor> list1 = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(list1);
            ret.add(dishDto);
        }
        redisTemplate.opsForValue().set(key,ret);
        return R.success(ret);
    }

    @PostMapping("/status/{status}")
    public R<String> stopOrRestart(@PathVariable("status") Integer status,
                                   @RequestParam List<Long> ids)
    {
        for (Long id : ids)
        {
            Dish dish = new Dish();
            dish.setStatus(status);
            dish.setId(id);
            dishService.updateById(dish);
        }
        return R.success("菜品状态修改成功");
    }

    @GetMapping("/page")
    public R<Page>  page(int page,int pageSize,String name)
    {
        Page<Dish> pageInfo = new Page<>();
        QueryWrapper<Dish> queryWrapper =new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),"name",name);
        queryWrapper.orderByDesc("update_time");
        dishService.page(pageInfo,queryWrapper);

        Page<DishDto> pageDtoInfo = new Page<>(page,pageSize);
        BeanUtils.copyProperties(pageInfo,pageDtoInfo,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();
        for (Dish dish : records)
        {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Category category = categroyService.getById(dish.getCategoryId());
            if(category != null)
                dishDto.setCategoryName(category.getName());


            list.add(dishDto);
        }

        pageDtoInfo.setRecords(list);
        return R.success(pageDtoInfo);
    }

    @Transactional
    @DeleteMapping
    public R<String> delete(List<Long> ids)
    {
        dishService.removeWithFlavors(ids);
        return R.success("删除成功");
    }
}
