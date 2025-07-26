package com.yupi.yupicturebackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.yupi.yupicturebackend.manager.auth.model.SpaceUserRole;
import com.yupi.yupicturebackend.model.entity.Space;
import com.yupi.yupicturebackend.model.entity.SpaceUser;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.SpaceRoleEnum;
import com.yupi.yupicturebackend.model.enums.SpaceTypeEnum;
import com.yupi.yupicturebackend.service.SpaceUserService;
import com.yupi.yupicturebackend.service.UserService;
import org.jsoup.internal.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Component
public class SpaceUserAuthManager {
    //加载配置文件到对象，并且提供角色获取权限列表的方法
    @Resource
    private SpaceUserService spaceUserService;
    @Resource
    private UserService userService;

     public static SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    //读取并且装换为java对象
    static{
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        //转化
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     */
    public static List<String> getPermissionsByRole(String spaceUserRole){
        if(StringUtil.isBlank(spaceUserRole)){
            return new ArrayList<> ();
        }

        //找到了匹配的对象
        for (SpaceUserRole role : SPACE_USER_AUTH_CONFIG.getRoles()) {
            if(role.getKey().equals(spaceUserRole)){
                return role.getPermissions();
            }
        }
        return new ArrayList<>();

    }
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            return new ArrayList<>();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人或管理员有所有权限
                if (space.getUserId().equals(loginUser.getId()) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }



}
