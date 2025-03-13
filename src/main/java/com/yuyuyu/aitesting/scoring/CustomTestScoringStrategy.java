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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*自定义测评类应用评分策略
* */
@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private AppService appService;
    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        //1.根据id查询到题目和题目结果信息
        Long appId = app.getId();
        //判断id是否存在
        ThrowUtils.throwIf(appService.getById(appId) == null, ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        //得到判题结果
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
        );
        //2.统计用户每个选择对应的属性个数，如I=10
        //定义集合用于存储结果
        Map<String,Integer> optionCount = new HashMap<>();
        //获取所有的题目
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentDTOList = questionVO.getQuestionContent();
        //遍历题目
        for (QuestionContentDTO questionContentDTO : questionContentDTOList) {
            //遍历答案列表
            for (String choice : choices) {
                //遍历题目中的选项
                for(QuestionContentDTO.Option option: questionContentDTO.getOptions()){
                    //如果用户答案和选项的key匹配
                    if(choice.equals(option.getKey())){
                        //获取选项的属性
                        String result = option.getResult();
                        //如果集合不包含该属性，则置为0
                        if(!optionCount.containsKey(result)){
                            optionCount.put(result,0);
                        }
                        //在原来的基础上加1
                        optionCount.put(result,optionCount.get(result)+1);
                    }
                }
            }
        }
        //3.遍历每个评分结果，看哪一个评分结果更高
        //初始化最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            //计算当前评分结果的分数
            int sum = resultProp.stream()
                    .mapToInt(result -> optionCount.getOrDefault(result, 0))
                    .sum();
            //如果当前评分结果的分数大于最高分数，
            // 则更新最高分数和最高分数对应的评分结果
            if (sum>maxScore){
                maxScore = sum;
                maxScoringResult = scoringResult;
            }
        }
        //4.构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());

        return userAnswer;
    }
}
