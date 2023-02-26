package com.lkl.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkl.common.R;
import com.lkl.entity.Category;
import com.lkl.service.CategroyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController
{
    @Autowired
    private CategroyService categroyService;


    @PostMapping
    public R<String> save(@RequestBody Category category)
    {
        categroyService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize)
    {
        Page pageInfo = new Page<>(page,pageSize);
        QueryWrapper<Category> queryWrapper =new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        categroyService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids)
    {
        categroyService.remove(ids);

        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category)
    {
        categroyService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category)
    {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(category.getType()!= null,"type",category.getType());
        queryWrapper.orderByAsc("sort");
        queryWrapper.orderByDesc("update_time");
        List<Category> list = categroyService.list(queryWrapper);
        return R.success(list);
    }
}
