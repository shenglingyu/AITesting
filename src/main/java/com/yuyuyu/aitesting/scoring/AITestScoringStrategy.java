package com.yuyuyu.aitesting.scoring;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yuyuyu.aitesting.common.ErrorCode;
import com.yuyuyu.aitesting.exception.ThrowUtils;
import com.yuyuyu.aitesting.manager.AIManager;
import com.yuyuyu.aitesting.model.dto.question.AIQuestionAnswerDTO;
import com.yuyuyu.aitesting.model.dto.question.QuestionContentDTO;
import com.yuyuyu.aitesting.model.entity.App;
import com.yuyuyu.aitesting.model.entity.Question;
import com.yuyuyu.aitesting.model.entity.UserAnswer;
import com.yuyuyu.aitesting.model.vo.QuestionVO;
import com.yuyuyu.aitesting.service.AppService;
import com.yuyuyu.aitesting.service.QuestionService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*AI测评类应用评分策略
* */
@ScoringStrategyConfig(appType = 1,scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private AppService appService;
    @Resource
    private AIManager aiManager;
    @Resource
    private RedissonClient redissonClient;
    //AI分布式锁的key
    private static final String AI_ANWSER_LOCK="AI_ANWSER_LOCK";
    /**
     * AI 评分结果缓存
     */
    private final Cache<String, String> anwserCacheMap = Caffeine.newBuilder()
            .initialCapacity(1024)
            //缓存五分钟后移除
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build();
    /**
     * AI评分系统消息
     */
    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE="你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象\n";
    /**
     * AI评分用户消息
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    public String getAITestScoringUserMessage(App app,List<QuestionContentDTO> questionContentDTOList,List<String> choices){
        StringBuilder userMassage = new StringBuilder();
        userMassage.append(app.getAppName()).append("\n");
        userMassage.append("【【【").append(app.getAppDesc()).append("】】】").append("\n");
        List<AIQuestionAnswerDTO> aiQuestionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < choices.size(); i++) {
            AIQuestionAnswerDTO aiQuestionAnswerDTO = new AIQuestionAnswerDTO();
            aiQuestionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            aiQuestionAnswerDTO.setUserAnswer(choices.get(i));
            aiQuestionAnswerDTOList.add(aiQuestionAnswerDTO);
        }
        userMassage.append(JSONUtil.toJsonStr(aiQuestionAnswerDTOList));
        return userMassage.toString();
    }
    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        //1.根据id查询到题目和题目结果信息
        Long appId = app.getId();
        //先判断在缓存中是否存在
        String jsonStr = JSONUtil.toJsonStr(choices);
        String catchKey = buildCacheKey(appId, jsonStr);
        String answerJson = anwserCacheMap.getIfPresent(catchKey);
        //如果有缓存，则直接返回
        if (StringUtils.isNotBlank(answerJson)) {
            //构造返回值，填充返回对象的属性
            UserAnswer userAnswer =JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }
        //判断id是否存在
        ThrowUtils.throwIf(appService.getById(appId) == null, ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        //定义锁
        RLock lock = redissonClient.getLock(AI_ANWSER_LOCK + catchKey);//最细粒度地加锁
        try {
            //竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            if (!res){
                //如果没有抢到锁，强行返回
                return null;
            }
            //抢到锁了，执行剩下的内容
            //获取所有的题目
            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContentDTOList = questionVO.getQuestionContent();
            //2.调用AI获取结果
            /**
             * 封装用户的prompt
             */
            String aiTestScoringUserMessage = getAITestScoringUserMessage(app, questionContentDTOList, choices);
            String result = aiManager.doSyncStableRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, aiTestScoringUserMessage);
            System.out.println(result);
            System.out.println("=========================");
            //截取需要的数据信息
            int start=result.indexOf("```");
            int end=result.lastIndexOf("```");
            String json=result.substring(start,end+1);
            start=json.indexOf("{");
            end=json.lastIndexOf("}");
            json=json.substring(start,end+1);
            json = json.replace("\\n", ""); // 去掉换行符
            json = json.replace("\\", ""); // 去掉转义符
            //缓存结果
            anwserCacheMap.put(catchKey,json);
            //3.构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);

            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            System.out.println(userAnswer.getResultDesc());
            System.out.println("=====================");
            System.out.println(userAnswer.getResultName());
            return userAnswer;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //释放锁，在finally中释放锁，保证即使有异常，也能够及时释放锁
            if (lock!=null&&lock.isLocked()){
                //只有自己能释放自己的锁，如果是本人的，就释放锁
                if (lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 构建缓存key
     * @param appId
     * @param choices
     * @return
     */
    private String buildCacheKey(Long appId,String choices){
        return DigestUtil.md5Hex(appId+":"+choices);
    }
}
