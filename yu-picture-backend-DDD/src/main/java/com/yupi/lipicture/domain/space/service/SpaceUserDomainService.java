package com.yupi.lipicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.lipicture.domain.space.entity.SpaceUser;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.spaceuser.SpaceUserQueryRequest;

/**
* @author henan
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-07-13 10:58:47
*/
public interface SpaceUserDomainService extends IService<SpaceUser> {


    /**
     * 获取查询条件
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

}
