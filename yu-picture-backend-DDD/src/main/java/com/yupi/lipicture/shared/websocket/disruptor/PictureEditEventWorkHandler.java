package com.yupi.lipicture.shared.websocket.disruptor;

import cn.hutool.json.JSONUtil;
import com.lmax.disruptor.WorkHandler;
import com.yupi.lipicture.shared.websocket.PictureEditHandler;
import com.yupi.lipicture.shared.websocket.model.PictureEditMessageTypeEnum;
import com.yupi.lipicture.shared.websocket.model.PictureEditRequestMessage;
import com.yupi.lipicture.shared.websocket.model.PictureEditResponseMessage;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.domain.user.service.UserDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

@Component
@Slf4j
public class PictureEditEventWorkHandler implements WorkHandler<PictureEditEvent> {
    //    WorkHandler<PictureEditEvent>-----disruptor的消费者接口
    @Resource
    private PictureEditHandler pictureEditHandler;
    @Resource
    private UserDomainService userDomainService;

    @Override
    public void onEvent(PictureEditEvent pictureEditEvent) throws Exception {
        PictureEditRequestMessage pictureEditRequestMessage = pictureEditEvent.getPictureEditRequestMessage();
        //1.解析消息-----前端发送大的 数据
//        PictureEditRequestMessage pictureEditRequestMessage = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        //2.判断消息类型
        WebSocketSession session = pictureEditEvent.getSession();
        String type = pictureEditRequestMessage.getType();
        PictureEditMessageTypeEnum pictureEditMessageTypeEnum = PictureEditMessageTypeEnum.getValueByValue(type);
        if(pictureEditMessageTypeEnum==null){
            log.error("消息类型不存在");
            return;
        }
        User user = (User) session.getAttributes().get("user");
        Long userId = (Long) session.getAttributes().get("userId");
        Long pictureId = (Long) session.getAttributes().get("pictureId");
        //调用对应的消息1处理方法
        switch (pictureEditMessageTypeEnum) {
            case ENTER_EDIT:
                pictureEditHandler.handleEnterEditMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            case EXIT_EDIT:
                pictureEditHandler.handleExitEditMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            case EDIT_ACTION:
                pictureEditHandler.handleEditActionMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            default://错误信息仅仅发送给自己
                PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage(PictureEditMessageTypeEnum.ERROR.getValue(), "未知操作", null, userDomainService.getUserVO(user));
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(pictureEditResponseMessage)));
                break;
        }
    }
}
