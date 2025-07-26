package com.yupi.lipicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.lipicture.domain.space.entity.Space;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.space.SpaceQueryRequest;

/**
 * @author 李鱼皮
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2024-12-18 19:53:34
 */
public interface SpaceDomainService {

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别填充空间对象
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

}
