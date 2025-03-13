package com.yuyuyu.aitesting.model.dto.question;

import lombok.Data;

/**
 * 题目答案封装类（用于ai评分）
 */
@Data
public class AIQuestionAnswerDTO {
    /***
     * 题目名称
     */
    private String title;
    /**
     * 用户答案
     */
    private String userAnswer;
}
