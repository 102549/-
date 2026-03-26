package com.baymax.exam.center.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baymax.exam.common.core.base.IBaseEnum;
import lombok.Getter;

@Getter
public enum CheatRiskLevelEnum implements IBaseEnum<Integer> {
    LOW(0, "低风险"),
    MEDIUM(1, "中风险"),
    HIGH(2, "高风险");

    @EnumValue
    private final Integer value;
    private final String label;

    CheatRiskLevelEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}