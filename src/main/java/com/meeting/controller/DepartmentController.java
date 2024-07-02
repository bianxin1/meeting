package com.meeting.controller;

import com.meeting.service.DepartmentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/department")
public class DepartmentController {
    @Resource
    private DepartmentsService departmentsService;

    @PostMapping("/add")


}
