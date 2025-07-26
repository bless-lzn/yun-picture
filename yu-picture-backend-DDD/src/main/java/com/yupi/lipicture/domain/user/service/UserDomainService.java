package com.yupi.lipicture.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.lipicture.interfaces.dto.user.UserQueryRequest;
import com.yupi.lipicture.domain.user.entity.User;
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
public interface UserDomainService {
    /**
     * 用户注册函数
     * <p>
     * 该函数用于处理用户注册请求，接收用户账号、用户密码和确认密码作为参数
     * 它的主要职责是验证密码和确认密码是否一致，以及是否符合密码策略（如果有的话）
     * 如果验证通过，该函数将用户信息保存在数据库或其他持久化存储中
     *
     * @param userAccount   用户账号，应确保唯一性以便在系统中标识用户
     * @param userPassword  用户设置的密码，需要与checkPassword参数进行一致性校验
     * @param checkPassword 用户再次输入的密码，用于确认userPassword的正确性
     * @return 返回一个长整型值，表示用户注册的结果如果返回值为-1，表示注册失败；
     * 其他值可以用于表示成功注册的用户ID或其他确认信息
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    User getLoginUser(HttpServletRequest request);

    String getEncryptPassword(String userPassword);
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
/**
 * 用户登录方法
 * 该方法用于验证用户账户和密码，并返回用户登录信息
 *
 * @param userAccount 用户账号，用于识别用户
 * @param userPassword 用户密码，用于验证用户身份
 * @param request HTTP请求对象，可能包含会话信息等
 * @return 登录用户信息对象，包含用户登录状态等信息
 */
LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

/**
 * 获取登录用户视图对象
 * 根据用户实体对象获取登录用户视图对象，用于展示用户登录相关信息
 *
 * @param user 用户实体对象，包含用户基本信息
 * @return 登录用户视图对象，用于展示用户登录状态等信息
 */
LoginUserVO getLoginUserVO(User user);

/**
 * 用户登出方法
 * 该方法用于处理用户登出操作，结束用户会话
 *
 * @param request HTTP请求对象，可能包含会话信息等
 * @return 登出操作结果，true表示成功，false表示失败
 */
boolean userLogout(HttpServletRequest request);

/**
 * 获取用户视图对象
 * 根据用户实体对象获取用户视图对象，用于展示用户相关信息
 *
 * @param user 用户实体对象，包含用户基本信息
 * @return 用户视图对象，用于展示用户信息
 */
UserVO getUserVO(User user);

/**
 * 获取用户视图对象列表
 * 根据用户实体对象列表获取用户视图对象列表，用于批量展示用户相关信息
 *
 * @param userList 用户实体对象列表，包含多个用户的基本信息
 * @return 用户视图对象列表，用于展示多个用户的详细信息
 */
List<UserVO> getUserVOList(List<User> userList);

/**
 * 获取查询包装类
 *
 * @param userQueryRequest
 * @return
 */

QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    Long addUser(User user);

    Boolean removeById(Long id);

    boolean updateById(User user);

    User getById(long id);

    Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper);

    List<User> listByIds(Set<Long> userIdSet);
}


