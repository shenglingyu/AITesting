package com.yuyuyu.aitesting.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest implements Serializable {
    /**
     * id
     * */
    private Long id;
    /**
    * 状态：0-待审核，1-审核通过，2-审核不通过
    * */
    private Integer ReviewStatus;
    /**
    * 审核人
    * */
    private Integer Reviewer;
    /**
    * 审核信息
    * */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}
