package com.lkl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkl.common.R;
import com.lkl.dto.SetmealDto;
import com.lkl.entity.Setmeal;
import com.lkl.entity.SetmealDish;
import com.lkl.service.CategroyService;
import com.lkl.service.SetmealDishService;
import com.lkl.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController
{
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategroyService categroyService;


    @GetMapping("/{id}")
    public R<Setmeal> getById(@PathVariable("id") Long id)
    {
        Setmeal setmeal = setmealService.getById(id);
        String categoryName = categroyService.getById(setmeal.getCategoryId()).getName();
        SetmealDto setmealDto = setmealService.getByIdWithDishes(setmeal,categoryName);
        return R.success(setmealDto);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids)
    {
        log.info("进入删除");
        setmealService.removeWithDishes(ids);
        return R.success("删除成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name)
    {
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),"name",name);
        setmealService.page(pageInfo,queryWrapper);

        Page<SetmealDto> pageDto = new Page<>();
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Setmeal> list = pageInfo.getRecords();
        List<SetmealDto> retList = new ArrayList<>();
        for (Setmeal setmeal : list)
        {
            String categoryName = categroyService.getById(setmeal.getCategoryId()).getName();
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            setmealDto.setCategoryName(categoryName);
            retList.add(setmealDto);
        }
        pageDto.setRecords(retList);
        return R.success(pageDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto)
    {
        setmealService.updateWithDishes(setmealDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> stopOrRestart(@PathVariable("status") int status,
                                   @RequestParam List<Long> ids)
    {
        for (Long id : ids)
        {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改套餐状态成功");
    }


    @GetMapping("/list")
    public R<List<SetmealDto>> getListById(Long categoryId,int status)
    {
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",categoryId);
        queryWrapper.eq("status",status);
        List<Setmeal> list = setmealService.list(queryWrapper);
        List<SetmealDto> ret = new ArrayList<>();
        for (Setmeal setmeal : list)
        {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            QueryWrapper<SetmealDish> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("setmeal_id",setmeal.getId());
            List<SetmealDish> list1 = setmealDishService.list(queryWrapper1);
            setmealDto.setSetmealDishes(list1);
            ret.add(setmealDto);
        }
        return R.success(ret);
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithDishes(setmealDto);
        return R.success("保存成功");
    }
}
