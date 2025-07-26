package com.yupi.lipicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.infrastructure.common.DeleteRequest;
import com.yupi.lipicture.interfaces.dto.user.UserLoginRequest;
import com.yupi.lipicture.interfaces.dto.user.UserQueryRequest;
import com.yupi.lipicture.interfaces.dto.user.UserRegisterRequest;
import com.yupi.lipicture.interfaces.vo.uservo.LoginUserVO;
import com.yupi.lipicture.interfaces.vo.uservo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author henan
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-07-02 09:46:37
 */
public interface UserApplicationService {
    long userRegister(UserRegisterRequest registerRequest);

    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    boolean userLogout(HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    User getUserById(long id);


    UserVO getUserVOById(long id);

    List<User> listByIds(Set<Long> userIdSet);

    boolean deleteUser(DeleteRequest deleteRequest);

    void updateUser(User user);

    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    long addUser(User user);
}


