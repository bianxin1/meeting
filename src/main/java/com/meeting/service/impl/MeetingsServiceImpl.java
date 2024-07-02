package com.meeting.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.domain.pojo.Meetings;
import com.meeting.mapper.MeetingsMapper;
import com.meeting.service.MeetingsService;
import org.springframework.stereotype.Service;

/**
* @author shanmingxi
* @description 针对表【meetings】的数据库操作Service实现
* @createDate 2024-07-01 16:57:41
*/
@Service
public class MeetingsServiceImpl extends ServiceImpl<MeetingsMapper, Meetings>
    implements MeetingsService {

}




