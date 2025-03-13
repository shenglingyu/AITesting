package com.yuyuyu.aitesting.scoring;

import com.yuyuyu.aitesting.model.entity.App;
import com.yuyuyu.aitesting.model.entity.UserAnswer;

import java.util.List;

public interface ScoringStrategy {
    /**
     * 评分策略
     *
     * @param choices 用户答案
     * @return userAnswer
     */
    UserAnswer doScore(List<String> choices, App app);
}
