package com.meeting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meeting.commen.result.Result;
import com.meeting.domain.dto.deparement.DepartmentAddRequest;
import com.meeting.domain.pojos.Departments;
import com.meeting.expection.UnauthorizedException;
import com.meeting.service.DepartmentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/department")
public class DepartmentController {
    @Resource
    private DepartmentsService departmentsService;

    @PostMapping("/add")
    public Result addDepartment(@RequestBody DepartmentAddRequest departmentAddRequest, HttpServletRequest request) {
        if (departmentAddRequest == null) {
            throw new UnauthorizedException("请求错误");
        }
            Departments departments = new Departments();
        BeanUtils.copyProperties(departmentAddRequest, departments);
        boolean result = departmentsService.save(departments);
        return Result.succ(200,"添加成功",departments);
    }

    @PostMapping("/update")
    public Result updateDepartment(@RequestBody DepartmentAddRequest departmentAddRequest,
                                            HttpServletRequest request) {
        if ( departmentAddRequest == null ||  departmentAddRequest.getId() == null) {
            throw new UnauthorizedException("请求错误");
        }
        Departments departments = new Departments();
        BeanUtils.copyProperties(departmentAddRequest, departments);
        boolean result =departmentsService.updateById(departments);
        return Result.succ(200,"更新成功",departments);
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteDepartment(@PathVariable("id") Integer id, HttpServletRequest request) {
        if (id== null || id <= 0) {
            throw new UnauthorizedException("请求错误");
        }
        Departments departments = new Departments();
        departments = departmentsService.getById(id);
        boolean b = departmentsService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }

    @GetMapping("/listAll")
    public Result listAll() {
        List<Departments> list = departmentsService.list();
        return Result.succ(list);
    }



}
