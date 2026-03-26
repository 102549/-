package com.baymax.exam.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.center.model.StudentAbilityProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentAbilityProfileMapper extends BaseMapper<StudentAbilityProfile> {

    @Select("SELECT * FROM ed_student_ability_profile WHERE user_id = #{userId} AND course_id = #{courseId}")
    StudentAbilityProfile findByUserAndCourse(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
}