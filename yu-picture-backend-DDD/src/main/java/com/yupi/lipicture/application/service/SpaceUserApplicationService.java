package com.yupi.lipicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.spaceuser.SpaceUserAddRequest;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.spaceuser.SpaceUserQueryRequest;
import com.yupi.lipicture.domain.space.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.lipicture.interfaces.vo.sapce.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author henan
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-07-13 10:58:47
*/
public interface SpaceUserApplicationService extends IService<SpaceUser> {

    /**
     * 添加空间用户
     *
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验
     *
     * @param spaceUser
     * @param add
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取查询条件
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取空间用户视图
     *
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间用户视图列表
     *
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

}
