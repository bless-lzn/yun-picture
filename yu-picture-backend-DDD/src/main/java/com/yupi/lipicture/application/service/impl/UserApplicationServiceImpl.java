package com.yupi.lipicture.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.lipicture.application.service.UserApplicationService;
import com.yupi.lipicture.domain.user.entity.User;

import com.yupi.lipicture.domain.user.service.UserDomainService;
import com.yupi.lipicture.infrastructure.common.DeleteRequest;
import com.yupi.lipicture.infrastructure.exception.BusinessException;
import com.yupi.lipicture.infrastructure.exception.ErrorCode;
import com.yupi.lipicture.infrastructure.exception.ThrowUtils;
import com.yupi.lipicture.interfaces.dto.user.UserLoginRequest;
import com.yupi.lipicture.interfaces.dto.user.UserQueryRequest;
import com.yupi.lipicture.interfaces.dto.user.UserRegisterRequest;
import com.yupi.lipicture.interfaces.vo.uservo.LoginUserVO;
import com.yupi.lipicture.interfaces.vo.uservo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author henan
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-07-02 09:46:37
 */
@Service
@Slf4j
public class UserApplicationServiceImpl
        implements UserApplicationService {
    @Resource
    private UserDomainService userDomainService;

    @Override

    public long userRegister(UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        User.validUserRegister(userAccount, userPassword, checkPassword);
       return userDomainService.userRegister(userAccount, userPassword, checkPassword);
        //主键回填
    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest  userLoginRequest , HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
      User.validUserLogin(userAccount, userPassword);
      return userDomainService.userLogin(userAccount, userPassword, request);
      //账号登录登录
       //保存登录的用户信息
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        return userDomainService.getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否登录
      return userDomainService.userLogout(request);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
      return userDomainService.getLoginUser(request);
    }


    public String getEncryptPasssword(String userPassword) {
        //加盐
      return userDomainService.getEncryptPassword(userPassword);
    }

    @Override
    public UserVO getUserVO(User user) {
      return userDomainService.getUserVO(user);
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
      return userDomainService.getUserVOList(userList);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
      return userDomainService.getQueryWrapper(userQueryRequest);


    }

    @Override
    public User getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userDomainService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return user;
    }

    @Override
    public UserVO getUserVOById(long id) {
        return userDomainService.getUserVO(getUserById(id));
    }
    @Override
    public List<User> listByIds(Set<Long> userIdSet) {
        return userDomainService.listByIds(userIdSet);
    }

    @Override
    public boolean deleteUser(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userDomainService.removeById(deleteRequest.getId());
    }

    @Override
    public void updateUser(User user) {
        boolean result = userDomainService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userDomainService.page(new Page<>(current, size),
                userDomainService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userDomainService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return userVOPage;
    }


    @Override
    public long addUser(User user) {
        return userDomainService.addUser(user);
    }





}




