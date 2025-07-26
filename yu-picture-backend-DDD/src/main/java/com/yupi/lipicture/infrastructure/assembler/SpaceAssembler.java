package com.yupi.lipicture.infrastructure.assembler;

import com.yupi.lipicture.domain.space.entity.Space;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.space.SpaceAddRequest;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.space.SpaceEditRequest;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}
