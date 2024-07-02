package com.meeting.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting.domain.pojos.Users;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author shanmingxi
* @description 针对表【users】的数据库操作Mapper
* @createDate 2024-07-01 17:56:56
* @Entity generator.domain.Users
*/
public interface UsersMapper extends BaseMapper<Users> {
    @Select("select * from users where email = #{email} or account = #{account}")
    Users selectByAccountOrEmail(@Param("email") String email,@Param("account") String account);
}




