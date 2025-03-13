package com.yuyuyu.aitesting.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核状态枚举
 *
 * @author <a href="https://github.com/shenglingyu">玉圣玲</a>
 */
public enum ReviewStatusEnum {
    //1.定义审核状态
    REVIEWING("待审核",0),
    PASS("审核通过",1),
    REJECT("审核拒绝",2);
    private final String text;
    private final int value;
    ReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value=value;
    }
    /*
    获取值的列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
    /*
    根据传入的value获取枚举
     */
    public static ReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ReviewStatusEnum anEnum : ReviewStatusEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }
    public String getText() {
        return text;
    }
    public int getValue() {
        return value;
    }
}
