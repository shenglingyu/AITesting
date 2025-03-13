package com.yuyuyu.aitesting.manager;

import com.yuyuyu.aitesting.common.ErrorCode;
import com.yuyuyu.aitesting.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 通用AI调用管理
 */
@Component
public class AIManager {
    @Resource
    private ClientV4 clientV4;
    /**
     * 稳定的随机数
     */
    private static final float STABLE_TEMPERATURE = 0.05f;
    /**
     * 不稳定的随机数
     */
    private static final float UNSTABLE_TEMPERATURE = 0.99f;
//重载方法，简化方法的参数实现
    /**
     * 同步请求（稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage,String userMessage){
        return doRequest(systemMessage,userMessage,false,STABLE_TEMPERATURE);
    }
    /**
     * 同步请求（不稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnStableRequest(String systemMessage,String userMessage){
        return doRequest(systemMessage,userMessage,false,UNSTABLE_TEMPERATURE);
    }
    /**
     * 通用同步请求
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSyncRequest(String systemMessage,String userMessage,float temperature){
        return doRequest(systemMessage,userMessage,false,temperature);
    }

//重载方法，简化方法的参数实现

    /**
     * 通用请求（简化消息传递）
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(String systemMessage,String userMessage,boolean stream,float temperature){
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);
        return doRequest(messages,stream,temperature);
    }
    /**
     * 通用请求
     * @param messages 聊天消息列表
     * @param stream 是否使用流式响应
     * @param temperature 生成文本的随机性温度
     * @return 请求响应的字符串结果
     */
    public String doRequest(List<ChatMessage> messages,boolean stream,float temperature){
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,e.getMessage());
        }
    }
    /**
     * 通用请求（流式请求,不稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */

    public Flowable<ModelData> doUnStableStreamRequest(String systemMessage,String userMessage) {
        return doStreamRequest(systemMessage,userMessage,UNSTABLE_TEMPERATURE);
    }

    /**
     * 通用请求（流式请求,稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */

    public Flowable<ModelData> doStableStreamRequest(String systemMessage,String userMessage) {
        return doStreamRequest(systemMessage,userMessage,STABLE_TEMPERATURE);
    }

    /**
     * 通用请求（流式请求,简化消息传递）
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage,String userMessage, float temperature) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        messages.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(userChatMessage);
        return doStreamRequest(messages,temperature);
    }
    /**
     * 通用请求（流式请求）
     * @param messages
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messages, float temperature){
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getFlowable();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,e.getMessage());
        }
    }

}
