package com.weiziplus.springboot.common.utils.token;

import com.weiziplus.springboot.common.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户token配置
 *
 * @author wanglongwei
 * @data 2019/5/8 9:06
 */
@Slf4j
public class TokenUtil {

    /**
     * 根据用户id获取用户Redis的key值
     *
     * @param audience---用户角色
     * @param userId---用户ID
     * @return
     */
    public static String getAudienceRedisKey(String audience, Long userId) {
        return audience + userId;
    }

    /**
     * 根据用户id创建token
     *
     * @param audience---用户角色
     * @param userId---用户ID
     * @param expireTime---过期时间
     * @return
     */
    protected static String createToken(String audience, Long userId, Long expireTime) {
        String token = null;
        try {
            token = JwtTokenUtil.createToken(userId, audience);
        } catch (Exception e) {
            log.error(audience.concat("用户token创建失败,userId:") + userId + e);
            return null;
        }
        RedisUtil.set(getAudienceRedisKey(audience, userId), token, expireTime);
        return token;
    }
}
