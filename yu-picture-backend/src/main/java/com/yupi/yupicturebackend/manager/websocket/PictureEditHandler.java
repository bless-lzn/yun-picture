package com.yupi.yupicturebackend.manager.websocket;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yupi.yupicturebackend.manager.websocket.disruptor.PictureEditEventProducer;
import com.yupi.yupicturebackend.manager.websocket.model.PictureEditActionEnum;
import com.yupi.yupicturebackend.manager.websocket.model.PictureEditMessageTypeEnum;
import com.yupi.yupicturebackend.manager.websocket.model.PictureEditRequestMessage;
import com.yupi.yupicturebackend.manager.websocket.model.PictureEditResponseMessage;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import org.jsoup.internal.StringUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//用户进入房间，退出1房间，进入编辑，退出编辑的处理器类
@Component
public class PictureEditHandler extends TextWebSocketHandler {
    //保存用户的ID,执行编辑操作，进入或者退出编辑的时候都会校验
    private final Map<Long, Long> pictureEditingUsers = new ConcurrentHashMap<>();
    //保存所有的会话，key:picture,value:用户会话集合
    private final Map<Long, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();
    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private PictureEditEventProducer pictureEditEventProducer;

    //广播
    private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage, WebSocketSession excludeSession) throws IOException {
//        得到pictureId对应的所有会话
        Set<WebSocketSession> webSocketSessions = pictureSessions.get(pictureId);
        if (CollUtil.isNotEmpty(webSocketSessions)) {
            //序列化
//           ObjectMapper objectMapper = builder.createXmlMapper(false).build();
            //创建objectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(module);
            //序列化为json字符串
            String message = objectMapper.writeValueAsString(pictureEditResponseMessage);
            TextMessage textMessage = new TextMessage(message);
            //依次发送消息
            for (WebSocketSession session : webSocketSessions) {
                if (excludeSession != null && session.equals(excludeSession)) {
                    continue;
                }
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }

    private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage) throws
            IOException {
        broadcastToPicture(pictureId, pictureEditResponseMessage, null);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //1.得到信息1放到session中
        User user = (User) session.getAttributes().get("user");
        Long userId = (Long) session.getAttributes().get("userId");
        Long pictureId = (Long) session.getAttributes().get("pictureId");
        //加入到hashmap中
        pictureSessions.putIfAbsent(pictureId, ConcurrentHashMap.newKeySet());
        pictureSessions.get(pictureId).add(session);
        //构建广播信息
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
        String message = String.format("%s加入编辑", user.getUserName());
        pictureEditResponseMessage.setMessage(message);
//        pictureEditResponseMessage.setEditAction(PictureEditActionEnum.);
        pictureEditResponseMessage.setUser(userService.getUserVO(user));
        //发送广播信息
        broadcastToPicture(pictureId, pictureEditResponseMessage);


    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //1.解析消息-----前端发送大的 数据
        PictureEditRequestMessage pictureEditRequestMessage = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        //2.判断消息类型

        User user = (User) session.getAttributes().get("user");
        Long userId = (Long) session.getAttributes().get("userId");
        Long pictureId = (Long) session.getAttributes().get("pictureId");
        //调用对应的消息1处理方法：：：：生产消息到disruptor队列中
        pictureEditEventProducer.publishEvent(pictureEditRequestMessage, session, user, pictureId);

    }

    public void handleEditActionMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws IOException {
        //进行信息的编辑
        PictureEditActionEnum editActionEnum = PictureEditActionEnum.getEnumByValue(pictureEditRequestMessage.getEditAction());
        if (editActionEnum == null) {
            return;
        }
        //确认是当前编辑者
        if (!pictureEditingUsers.containsKey(pictureId) || !pictureEditingUsers.get(pictureId).equals(user.getId())) {
            return;
        }
//        构建响应体
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EDIT_ACTION.getValue());
        pictureEditResponseMessage.setEditAction(editActionEnum.getValue());
        pictureEditResponseMessage.setUser(userService.getUserVO(user));
        pictureEditResponseMessage.setMessage(String.format("%s执行编辑%s", user.getUserName(), editActionEnum.getText()));
//        System.out.println(StrUtil.format("%s执行编辑%s", user.getUserName(), editActionEnum.getText()));
        //广播除了自己的所有人。
        broadcastToPicture(pictureId, pictureEditResponseMessage, session);


    }

    public void handleExitEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws IOException {
        //1.判断是不是在里面
        Long userId = pictureEditingUsers.get(pictureId);
        if (userId != null && userId.equals(user.getId())) {
            //移除
            //要是本人才能移除
            pictureEditingUsers.remove(pictureId);
            //构建响应体
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EXIT_EDIT.getValue());
            pictureEditResponseMessage.setUser(userService.getUserVO(user));
            pictureEditResponseMessage.setMessage(String.format("%s退出编辑", user.getUserName()));
            //发起广播
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }


    }

    public void handleEnterEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws IOException {
        //没有人在编辑，加入进去
        if (!pictureEditingUsers.containsKey(pictureId)) {
            //设置当前用户为编辑用户
            pictureEditingUsers.put(pictureId, user.getId());
            //构建响应体
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
            pictureEditResponseMessage.setUser(userService.getUserVO(user));
            pictureEditResponseMessage.setMessage(String.format("%s开始编辑图片", user.getUserName()));
            //发起广播
            broadcastToPicture(pictureId, pictureEditResponseMessage);

        }
        // 如果已经有用户在编辑了，那么就将该用户广播给请求进行编辑的用户
        // 通知其它用户
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        // 已经有用户在编辑，获取当前编辑用户ID
        Long editingUserId = pictureEditingUsers.get(pictureId);
        User editingUser = userService.getById(editingUserId);
        String message = String.format("用户 %s 正在编辑", editingUser.getUserName());
        pictureEditResponseMessage.setMessage(message);
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
        pictureEditResponseMessage.setUser(userService.getUserVO(editingUser));
        // 单独发送消息给当前请求编辑的session


        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(module);
        //序列化为json字符串
        String str = objectMapper.writeValueAsString(pictureEditResponseMessage);
        TextMessage textMessage = new TextMessage(str);
        if (session.isOpen()) {
            session.sendMessage(textMessage);
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //退出会话
        Map<String, Object> attributes = session.getAttributes();
        User user = (User) attributes.get("user");
        Long pictureId = (Long) attributes.get("pictureId");
        //移除当前用户的编辑状态
        handleExitEditMessage(null, session, user, pictureId);
        //删除会话
        Set<WebSocketSession> webSocketSessions = pictureSessions.get(pictureId);
        if (webSocketSessions != null) {
            webSocketSessions.remove(session);
            if (webSocketSessions.isEmpty()) {
                pictureSessions.remove(pictureId);
            }
        }
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
        String message = StrUtil.format("%s退出编辑", user.getUserName());
        System.out.println( message);
        pictureEditResponseMessage.setMessage(message);
//        pictureEditResponseMessage.setEditAction(PictureEditActionEnum.);
        pictureEditResponseMessage.setUser(userService.getUserVO(user));
        //发送广播信息
        broadcastToPicture(pictureId, pictureEditResponseMessage);

    }
}

