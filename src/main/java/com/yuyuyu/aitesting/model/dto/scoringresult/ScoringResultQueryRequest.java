package com.yuyuyu.aitesting.model.dto.scoringresult;

import com.yuyuyu.aitesting.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询评分结果请求
 *
 * @author <a href="https://github.com/shenglingyu">玉圣玲</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScoringResultQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private List<String> content;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 结果名称，如物流师
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;


    /**
     * 结果属性集合 JSON，如 [I,S,T,J]
     */
    private String resultProp;

    /**
     * 结果得分范围，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultScoreRange;

    /**
     * 应用 id
     */
    private Long appId;


    private static final long serialVersionUID = 1L;
}