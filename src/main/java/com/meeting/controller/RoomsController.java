package com.meeting.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.meeting.commen.result.Result;

import com.meeting.domain.dto.rooms.RoomsAddRequest;
import com.meeting.domain.pojos.Rooms;

import com.meeting.expection.UnauthorizedException;
import com.meeting.service.RoomsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/rooms")
public class RoomsController {
    @Resource
    private RoomsService roomsService;

    @PostMapping("/add")
    public Result addRooms(@RequestBody RoomsAddRequest roomsAddRequest, HttpServletRequest request) {
        if (roomsAddRequest == null) {
            throw new UnauthorizedException("请求错误");
        }
        Rooms rooms = new Rooms();
        BeanUtils.copyProperties(roomsAddRequest, rooms);
        boolean result = roomsService.save(rooms);
        return Result.succ(200,"添加成功",rooms);
    }

    @PostMapping("/update")
    public Result updateRoom(@RequestBody RoomsAddRequest roomAddRequest,
                                   HttpServletRequest request) {
        if ( roomAddRequest == null ) {
            throw new UnauthorizedException("请求错误");
        }
        Rooms rooms = new Rooms();
        BeanUtils.copyProperties(roomAddRequest, rooms);
        boolean result =roomsService.updateById(rooms);
        return Result.succ(200,"更新成功",rooms);
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteRoom(@PathVariable("id") Integer id, HttpServletRequest request) {
        if (id== null || id <= 0) {
            throw new UnauthorizedException("请求错误");
        }
        Rooms rooms = new Rooms();
        rooms = roomsService.getById(id);
        boolean b = roomsService.removeById(id);
        return Result.succ(200,"删除成功",null);
    }

    @GetMapping("/listAll")
    public Result listAll() {
        List<Rooms> list = roomsService.list();

        return Result.succ(list);
    }

    @GetMapping("/search")
    public Result searchRooms(String name, HttpServletRequest request) {

        QueryWrapper<Rooms> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        List<Rooms> roomsList = roomsService.list(queryWrapper);
        return Result.succ(roomsList);
    }

}
