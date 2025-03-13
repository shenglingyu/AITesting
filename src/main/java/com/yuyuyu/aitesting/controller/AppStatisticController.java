package com.yuyuyu.aitesting.controller;

import com.yuyuyu.aitesting.common.BaseResponse;
import com.yuyuyu.aitesting.common.ErrorCode;
import com.yuyuyu.aitesting.common.ResultUtils;
import com.yuyuyu.aitesting.exception.ThrowUtils;
import com.yuyuyu.aitesting.mapper.UserAnswerMapper;
import com.yuyuyu.aitesting.model.dto.statistic.AppAnswerCountDTO;
import com.yuyuyu.aitesting.model.dto.statistic.AppAnswerResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 统计分析接口
 *
 */
@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 热门应用回答即回答数统计
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        System.out.println("*******************************");
        System.out.println(userAnswerMapper);
        System.out.println("*******************************");
        System.out.println(userAnswerMapper.doAppAnswerCount());
        System.out.println("*******************************");
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 某应用回答结果分布统计
     * @return
     */
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null||appId<=0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}
