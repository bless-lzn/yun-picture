package com.yupi.yupicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum SpaceLevelEnum {
    COMMON("普通版", 0, 100L * 1024 * 1024, 100),
    PROFESSIONAL("专业版", 1, 1000L * 1024 * 1024, 1000),
    FLAGSHIP("旗舰版", 2, 10000L * 1024 * 1024, 10000);


    private final String text;
    private final int value;
    private final long maxSize;
    private final long maxCount;

    //构造方法
    SpaceLevelEnum(String text, int value, long maxSize, long maxCount) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    public static SpaceLevelEnum getEnumByValue(int value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum SpaceLevelEnum : SpaceLevelEnum.values()) {
            if (SpaceLevelEnum.value == value) {
                return SpaceLevelEnum;
            }
        }
        return null;
    }


}
