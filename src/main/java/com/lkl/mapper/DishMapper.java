package com.lkl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkl.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish>
{
}
