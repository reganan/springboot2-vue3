package com.weiziplus.springboot.pc.system.service;

import com.github.pagehelper.PageHelper;
import com.weiziplus.springboot.common.config.GlobalConfig;
import com.weiziplus.springboot.common.models.SysUser;
import com.weiziplus.springboot.common.utils.Md5Util;
import com.weiziplus.springboot.common.utils.PageBean;
import com.weiziplus.springboot.common.utils.ResponseBean;
import com.weiziplus.springboot.common.utils.ValidateUtil;
import com.weiziplus.springboot.common.utils.token.JwtTokenUtil;
import com.weiziplus.springboot.pc.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/5/10 9:00
 */
@Slf4j
@Service
public class SysUserService {
    @Autowired
    SysUserMapper mapper;

    /**
     * 获取用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param userName
     * @param allowLogin
     * @return
     */
    public Map<String, Object> getUserList(Integer pageNum, Integer pageSize, String userName, Integer allowLogin, String createTime) {
        PageHelper.startPage(pageNum, pageSize);
        return PageBean.pageInfo(mapper.getUserList(userName, allowLogin, createTime));
    }

    /**
     * 添加用户
     *
     * @param sysUser
     * @return
     */
    public Map<String, Object> addUser(SysUser sysUser) {
        if (ValidateUtil.notUsername(sysUser.getUsername())) {
            return ResponseBean.error("用户名不能包含特殊字符");
        }
        if (ValidateUtil.notPassword(sysUser.getPassword())) {
            return ResponseBean.error("密码为6-20位大小写和数字");
        }
        if (null != sysUser.getRealName() && ValidateUtil.notRealName(sysUser.getRealName())) {
            return ResponseBean.error("真实姓名不能包含特殊字符");
        }
        SysUser user = mapper.getUserInfoByName(sysUser.getUsername());
        if (null != user) {
            return ResponseBean.error("用户名已存在");
        }
        try {
            sysUser.setPassword(Md5Util.encode(sysUser.getPassword()));
        } catch (UnsupportedEncodingException e) {
            log.warn("添加用户密码MD5加密出错" + e);
            return ResponseBean.error("未知错误,请重试");
        }
        return ResponseBean.success(mapper.addUser(sysUser));
    }

    /**
     * 更新用户
     *
     * @param sysUser
     * @return
     */
    public Map<String, Object> updateUser(SysUser sysUser) {
        if (ValidateUtil.notUsername(sysUser.getUsername())) {
            return ResponseBean.error("用户名不能包含特殊字符");
        }
        if (null != sysUser.getRealName() && ValidateUtil.notRealName(sysUser.getRealName())) {
            return ResponseBean.error("真实姓名不能包含特殊字符");
        }
        SysUser user = mapper.getUserInfoByName(sysUser.getUsername());
        if (null != user && !sysUser.getId().equals(user.getId())) {
            return ResponseBean.error("用户名已存在");
        }
        return ResponseBean.success(mapper.updateUser(sysUser));
    }

    /**
     * 删除用户
     *
     * @param ids
     * @return
     */
    public Map<String, Object> deleteUser(Long[] ids) {
        if (0 >= ids.length) {
            return ResponseBean.error("ids为空");
        }
        for (Long id : ids) {
            if (GlobalConfig.SUPER_ADMIN_ID.equals(id)) {
                return ResponseBean.error("不能删除超级管理员");
            }
        }
        return ResponseBean.success(mapper.deleteUserByIds(ids));
    }

    /**
     * 更新用户角色
     *
     * @param userId
     * @param roleId
     * @return
     */
    public Map<String, Object> updateUserRole(Long userId, Long roleId) {
        if (null == userId || 0 >= userId) {
            return ResponseBean.error("userId不能为空");
        }
        if (GlobalConfig.SUPER_ADMIN_ID.equals(userId)) {
            return ResponseBean.error("不能修改超级管理员角色");
        }
        if (null == roleId || 0 > roleId) {
            return ResponseBean.error("roleId不能为空");
        }
        return ResponseBean.success(mapper.updateRoleIdByUserIdAndRoleId(userId, roleId));
    }

    /**
     * 重置用户密码
     *
     * @param request
     * @param userId
     * @param password
     * @return
     */
    public Map<String, Object> resetUserPassword(HttpServletRequest request, Long userId, String password) {
        if (null == userId || 0 > userId) {
            return ResponseBean.error("id不能为空");
        }
        if (ValidateUtil.notPassword(password)) {
            return ResponseBean.error("密码为6-20位大小写和数字");
        }
        if (GlobalConfig.SUPER_ADMIN_ID.equals(userId) && !GlobalConfig.SUPER_ADMIN_ID.equals(JwtTokenUtil.getUserIdByHttpServletRequest(request))) {
            return ResponseBean.error("您没有权限");
        }
        String newPwd = null;
        try {
            newPwd = Md5Util.encode(password);
        } catch (UnsupportedEncodingException e) {
            log.warn("重置用户密码MD5加密出错" + e);
            return ResponseBean.error("未知错误,请重试");
        }
        return ResponseBean.success(mapper.resetUserPassword(userId, newPwd));
    }
}
