package com.yuyuyu.aitesting.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuyuyu.aitesting.common.ErrorCode;
import com.yuyuyu.aitesting.exception.ThrowUtils;
import com.yuyuyu.aitesting.model.dto.question.QuestionContentDTO;
import com.yuyuyu.aitesting.model.entity.App;
import com.yuyuyu.aitesting.model.entity.Question;
import com.yuyuyu.aitesting.model.entity.ScoringResult;
import com.yuyuyu.aitesting.model.entity.UserAnswer;
import com.yuyuyu.aitesting.model.vo.QuestionVO;
import com.yuyuyu.aitesting.service.AppService;
import com.yuyuyu.aitesting.service.QuestionService;
import com.yuyuyu.aitesting.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private AppService appService;
    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private QuestionService questionService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        //1.根据题目id查询题目结果信息（按分数降序排序）
        Long appId = app.getId();
        ThrowUtils.throwIf(appService.getById(appId) == null, ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        //获取题目
        Question questions = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        //获取结果列表,并进行排序
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );
        //2.统计用户的总得分
        //只有一个用户，所以只需要计算一次总得分
        int totalScore=0;
        //将题目转为列表
        QuestionVO questionVO = QuestionVO.objToVo(questions);
        List<QuestionContentDTO> questionContentList = questionVO.getQuestionContent();
        //遍历题目
        for (QuestionContentDTO questionContentDTO : questionContentList) {
            //遍历结果列表
            for (String answer : choices) {
                //获得每一个选项
                for (QuestionContentDTO.Option option: questionContentDTO.getOptions()) {
                    //如果选项匹配
                    if(option.getKey().equals(answer)){
                        int score= Optional.ofNullable(option.getScore()).orElse(0);
                        totalScore +=score;
                    }
                }
            }

        }
        //3.遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果

        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if(totalScore >= scoringResult.getResultScoreRange()){
                maxScoringResult = scoringResult;
                break;
            }
        }
        //4.构造返回值，填充答案对象的对应属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
