package com.yuyuyu.aitesting;

import com.yuyuyu.aitesting.controller.QuestionController;
import com.yuyuyu.aitesting.model.dto.question.AIGenerateQuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class QuestionControllerTest {
    @Resource
    private QuestionController questionController;
    @Test
    void aiGenerateQuestionSSETest() throws InterruptedException {
        //模拟调用
        AIGenerateQuestionRequest aiGenerateQuestionRequest=new AIGenerateQuestionRequest();
//        aiGenerateQuestionRequest.setIsVip(false);
        aiGenerateQuestionRequest.setAppId(3L);
        aiGenerateQuestionRequest.setOptionNumber(2);
        aiGenerateQuestionRequest.setQuestionNumber(10);
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest,false);
        // 模拟普通用户调用
         questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest,false);
        // 模拟会员用户调用
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest,true);
        //模拟主线程一直启动
        Thread.sleep(1000000L);
    }
}
