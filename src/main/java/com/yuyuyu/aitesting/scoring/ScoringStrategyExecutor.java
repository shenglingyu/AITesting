package com.yuyuyu.aitesting.scoring;

import com.yuyuyu.aitesting.common.ErrorCode;
import com.yuyuyu.aitesting.exception.BusinessException;
import com.yuyuyu.aitesting.model.entity.App;
import com.yuyuyu.aitesting.model.entity.UserAnswer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ScoringStrategyExecutor {
    //策略列表
    @Resource
    private List<ScoringStrategy> scoringStrategyList;
    /**
    评分策略
     */
    public UserAnswer doScore(List<String> choiceList,App app) {
        Integer appType = app.getAppType();
        Integer scoringStrategy = app.getScoringStrategy();
        if (appType==null||scoringStrategy==null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到对应的配置");
        }
        //根据注解获取策略
        for (ScoringStrategy strategy : scoringStrategyList) {
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)){
                ScoringStrategyConfig annotation = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if (annotation.appType()==appType&&annotation.scoringStrategy()==scoringStrategy) {
                    return strategy.doScore(choiceList,app);
                }
            }

        }

        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到对应的配置");
    }
}
