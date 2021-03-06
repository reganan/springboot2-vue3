package com.weiziplus.springboot.api.service;

import com.github.pagehelper.util.StringUtil;
import com.weiziplus.springboot.api.mapper.UserMapper;
import com.weiziplus.springboot.common.models.User;
import com.weiziplus.springboot.common.utils.Md5Util;
import com.weiziplus.springboot.common.utils.ResponseBean;
import com.weiziplus.springboot.common.utils.ValidateUtil;
import com.weiziplus.springboot.common.utils.token.JwtTokenUtil;
import com.weiziplus.springboot.common.utils.token.WebTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglongwei
 * @data 2019/5/10 17:02
 */
@Slf4j
@Service
public class LoginService {

    @Autowired
    UserMapper userMapper;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    public Map<String, Object> login(String username, String password) {
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            return ResponseBean.error("用户名或密码为空");
        }
        User user = userMapper.getUserInfoByUsername(username);
        if (null == user) {
            return ResponseBean.error("用户名或密码错误");
        }
        try {
            if (!user.getPassword().equals(Md5Util.encode(password))) {
                return ResponseBean.error("用户名或密码错误");
            }
        } catch (UnsupportedEncodingException e) {
            log.warn("web用户登录MD5加密出错" + e);
            return ResponseBean.error("未知错误，请重试");
        }
        Map<String, Object> resMap = new HashMap<>(1);
        String token = WebTokenUtil.createToken(user.getId());
        resMap.put("token", token);
        return ResponseBean.success(resMap);
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    public Map<String, Object> register(String username, String password) {
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            return ResponseBean.error("用户名或密码为空");
        }
        if (ValidateUtil.notUsername(username)) {
            return ResponseBean.error("用户名不能包含特殊字符");
        }
        if (ValidateUtil.notPassword(password)) {
            return ResponseBean.error("密码为6-20位大小写和数字");
        }
        User user = userMapper.getUserInfoByUsername(username);
        if (null != user) {
            return ResponseBean.error("用户名已存在");
        }
        String md5Pwd;
        try {
            md5Pwd = Md5Util.encode(password);
        } catch (UnsupportedEncodingException e) {
            log.warn("web用户注册MD5加密出错" + e);
            return ResponseBean.error("未知错误，请重试");
        }
        return ResponseBean.success(userMapper.addUser(username, md5Pwd));
    }

    /**
     * 退出
     *
     * @return
     */
    public Map<String, Object> logout(HttpServletRequest request) {
        Long userId = JwtTokenUtil.getUserIdByHttpServletRequest(request);
        WebTokenUtil.deleteToken(userId);
        return ResponseBean.success();
    }
}
