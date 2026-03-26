package com.baymax.exam.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.center.model.Tags;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TagsMapper extends BaseMapper<Tags> {
    List<Tags> getCoursePublicTags(Integer courseId, Integer parentId);
}