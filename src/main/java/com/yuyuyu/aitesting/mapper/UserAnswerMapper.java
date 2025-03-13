package com.yuyuyu.aitesting.mapper;


import com.yuyuyu.aitesting.model.dto.statistic.AppAnswerCountDTO;
import com.yuyuyu.aitesting.model.dto.statistic.AppAnswerResultCountDTO;
import com.yuyuyu.aitesting.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 玉圣玲
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2025-03-06 16:22:35
 * @Entity com.yuyuyu.aitesting.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
    @Select("select appId,count(userId) as anwsercount from user_answer\n" +
            "    group by appId order by anwsercount desc limit 10;")
    List<AppAnswerCountDTO> doAppAnswerCount();

    @Select("select resultName,count(resultName) as resultcount from user_answer\n" +
            "where appId= #{appId}\n" +
            " group by resultName order by resultcount desc ;")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);
}




