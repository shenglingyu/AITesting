package com.yuyuyu.aitesting.model.dto.statistic;

import lombok.Data;

/**
 * App用户提交答案统计
 */
@Data
public class AppAnswerCountDTO {
    /**
     * 应用id
     */
    private  Long appId;
    /**
     * 用户提交答案数量
     */
    private Integer anwsercount;
}
