package com.yupi.lipicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.lipicture.domain.space.entity.SpaceUser;
import com.yupi.lipicture.domain.space.repository.SpaceUserRepository;
import com.yupi.lipicture.infrastructure.mapper.SpaceUserMapper;

public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}
