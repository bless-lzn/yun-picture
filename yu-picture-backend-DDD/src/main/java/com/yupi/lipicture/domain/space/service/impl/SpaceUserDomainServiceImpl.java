package com.yupi.lipicture.domain.space.service.impl;

import cn.hutool.core.util.ObjUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupi.lipicture.domain.space.entity.SpaceUser;
import com.yupi.lipicture.domain.space.service.SpaceUserDomainService;

import com.yupi.lipicture.infrastructure.mapper.SpaceUserMapper;

import com.yupi.lipicture.infrastructure.manager.upload.model.dto.spaceuser.SpaceUserQueryRequest;

import org.springframework.stereotype.Service;


/**
 * @author henan
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
 * @createDate 2025-07-13 10:58:47
 */
@Service

public class SpaceUserDomainServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
        implements SpaceUserDomainService {
    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }


}




