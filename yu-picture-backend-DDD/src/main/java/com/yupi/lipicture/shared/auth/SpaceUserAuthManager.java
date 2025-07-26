package com.yupi.lipicture.shared.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.lipicture.shared.auth.model.SpaceUserAuthConfig;
import com.yupi.lipicture.shared.auth.model.SpaceUserRole;
import com.yupi.lipicture.domain.space.entity.Space;
import com.yupi.lipicture.domain.space.entity.SpaceUser;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.domain.space.valueobject.SpaceRoleEnum;
import com.yupi.lipicture.domain.space.valueobject.SpaceTypeEnum;
import com.yupi.lipicture.application.service.SpaceUserApplicationService;
import com.yupi.lipicture.domain.user.service.UserDomainService;
import org.jsoup.internal.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Component
public class SpaceUserAuthManager {
    //加载配置文件到对象，并且提供角色获取权限列表的方法
    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;
    @Resource
    private UserDomainService userDomainService;

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
            if (loginUser.isAdmin()) {
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
                if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserApplicationService.lambdaQuery()
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
