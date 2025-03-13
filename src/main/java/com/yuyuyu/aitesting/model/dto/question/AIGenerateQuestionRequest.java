package com.yuyuyu.aitesting.model.dto.question;


import lombok.Data;

import java.io.Serializable;

/**
 * AI生成题目请求
 */
@Data
public class AIGenerateQuestionRequest implements Serializable {
    /**
     * 应用id
     */
    private Long appId;
    /**
     * 生成题目的数量
     */
    private Integer QuestionNumber=10;
    /**
     * 生成题目选项个数
     */
    private Integer optionNumber=2;
    /**
     * 是否是VIP
     */
//    private Boolean isVip=false;
    private static  final long serialVersionUID = 1L;

}
