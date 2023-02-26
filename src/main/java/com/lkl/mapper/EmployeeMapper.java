package com.lkl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkl.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>
{

}
