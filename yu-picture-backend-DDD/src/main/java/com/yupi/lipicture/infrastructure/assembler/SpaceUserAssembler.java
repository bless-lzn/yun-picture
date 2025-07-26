package com.yupi.lipicture.infrastructure.assembler;

import com.yupi.lipicture.domain.space.entity.SpaceUser;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.spaceuser.SpaceUserAddRequest;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.spaceuser.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}
