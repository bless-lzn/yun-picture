package com.yupi.yupicturebackend.manager.websocket.model;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum PictureEditMessageTypeEnum {
    //定义枚举类型
    INFO("发送通知", "INFO"),
    ERROR("发送错误", "ERROR"),
    ENTER_EDIT("进入编辑状态", "ENTER_EDIT"),
    EXIT_EDIT("退出编辑状态", "EXIT_EDIT"),
    EDIT_ACTION("执行编辑动作", "EDIT_ACTION");


    private final String text;
    private final String value;

    PictureEditMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditMessageTypeEnum getValueByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        for (PictureEditMessageTypeEnum typeEnum : PictureEditMessageTypeEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
