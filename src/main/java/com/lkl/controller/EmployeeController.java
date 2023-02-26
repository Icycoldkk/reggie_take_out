package com.lkl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkl.common.R;
import com.lkl.entity.Employee;
import com.lkl.service.EmployeeService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController
{
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,
            @RequestBody Employee employee)
    {
        String password = employee.getPassword();
        employee.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if(emp == null)
            return R.error("登陆失败");
        if(!emp.getPassword().equals(employee.getPassword()))
            return R.error("登陆失败");
        if(emp.getStatus() == 0)
            return R.error("账户已禁用");
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request)
    {
        request.getSession().removeAttribute("employee");
        return  R.success("退出成功");
    }

    @PostMapping("")
    public R<String> save(HttpServletRequest request,
            @RequestBody Employee employee)
    {
        log.info("新增员工，员工信息：{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name)
    {
        Page pageInfo = new Page<>(page,pageSize);
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(name),"name",name);
        queryWrapper.orderByAsc("update_time");
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee,
                            HttpServletRequest request)
    {
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id)
    {
        Employee emp = employeeService.getById(id);
        if(emp != null)
            return R.success(emp);
        return R.error("没有查询到对应员工信息");
    }
}
