package com.lkl.dto;


import com.lkl.entity.Setmeal;
import com.lkl.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal
{

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
