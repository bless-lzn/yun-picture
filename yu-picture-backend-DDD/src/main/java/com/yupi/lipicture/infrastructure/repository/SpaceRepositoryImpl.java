package com.yupi.lipicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.lipicture.domain.space.entity.Space;
import com.yupi.lipicture.domain.space.repository.SpaceRepository;
import com.yupi.lipicture.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Service;

@Service
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}
