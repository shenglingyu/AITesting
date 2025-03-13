package com.yuyuyu.aitesting.model.dto.statistic;

import lombok.Data;

/**
 * App用户提交答案统计
 */
@Data
public class AppAnswerResultCountDTO {
    /**
     * 结果名称
     */
    private String resultName;
    /**
     * 结果数量
     */
    private Integer resultcount;
}
