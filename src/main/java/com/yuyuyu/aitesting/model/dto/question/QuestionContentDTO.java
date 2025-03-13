package com.yuyuyu.aitesting.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionContentDTO {
    /*
    题目标题
     */
    private String title;
    /*
    题目选项列表
     */
    private List<Option> options;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}

