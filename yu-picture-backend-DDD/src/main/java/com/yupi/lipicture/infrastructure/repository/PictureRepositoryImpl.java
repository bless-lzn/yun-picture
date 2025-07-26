package com.yupi.lipicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.lipicture.domain.picture.entity.Picture;
import com.yupi.lipicture.domain.picture.repository.PictureRepository;
import com.yupi.lipicture.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}
