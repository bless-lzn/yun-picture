package com.yupi.lipicture.shared.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.yupi.lipicture.domain.picture.repository.PictureRepository;
import com.yupi.lipicture.shared.auth.SpaceUserAuthManager;
import com.yupi.lipicture.shared.auth.model.SpaceUserPermissionConstant;
import com.yupi.lipicture.domain.picture.entity.Picture;
import com.yupi.lipicture.domain.space.entity.Space;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.domain.space.valueobject.SpaceTypeEnum;
import com.yupi.lipicture.domain.picture.service.PictureDomainService;
import com.yupi.lipicture.application.service.SpaceApplicationService;
import com.yupi.lipicture.domain.user.service.UserDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {
    @Resource
    private UserDomainService userDomainService;
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;
    @Resource
    private PictureDomainService pictureDomainService;
    @Resource
    private SpaceApplicationService spaceApplicationService;
    @Resource
    private PictureRepository pictureRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //获取请求参数
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletServerHttpRequest.getServletRequest();
            String pictureId = httpServletRequest.getParameter("pictureId");
            //判断
            if (StrUtil.isBlank(pictureId)) {
                log.error("缺少图片参数，拒绝握手");
                return false;
            }
            //对用户进行判断
            User loginUser = userDomainService.getLoginUser(httpServletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("用户未登录，拒绝握手");
                return false;
            }
            Picture picture = pictureRepository.getById(pictureId);
            if (ObjUtil.isEmpty(picture)) {
                log.error("图片不存在，拒绝握手");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null)//私有图库
            {
                space = spaceApplicationService.getById(spaceId);
                if (ObjUtil.isEmpty(space)) {
                    log.error("空间不存在，拒绝握手");
                    return false;
                }//团队空间
                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.error("非团队空间，拒绝握手");
                    return false;
                }
            }
            //公共空间，团队空间
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);

            //检查用户是否有编辑该图片的权限
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("没有图片编辑权限");
                return false;
            }
            //将值设置进去
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId));//转换为Long类型
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
