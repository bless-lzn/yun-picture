package com.yupi.lipicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.domain.user.repository.UserRepository;
import com.yupi.lipicture.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户仓库实现类
 */
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}
