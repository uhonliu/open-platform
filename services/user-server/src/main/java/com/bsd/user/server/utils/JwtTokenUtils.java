package com.bsd.user.server.utils;

import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.utils.EncryptUtils;
import com.opencloud.common.utils.RandomValueUtils;
import com.opencloud.common.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt token生成工具
 *
 * @author lisongmao
 * 2019年6月29日
 */
public class JwtTokenUtils {
    public static final String JWT_ATTRIBUTE_MOBLIE = "moblie";
    public static final String JWT_ATTRIBUTE_SESSIONID = "sessionId";

    /**
     * token秘钥生成
     *
     * @param key 秘钥盐值
     * @return
     */
    public static SecretKeySpec creatSecretKeySpec(String key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        return secretKeySpec;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String createToken(String key, Map<String, Object> claims) {
        SecretKeySpec secreKey = creatSecretKeySpec(key);
        String compactJws = Jwts.builder().setClaims(claims)
                .compressWith(CompressionCodecs.DEFLATE).signWith(SignatureAlgorithm.HS256, secreKey).compact();
        if (StringUtils.isNotEmpty(compactJws)) {
            //jwt生成的token进行des加密
            compactJws = EncryptUtils.encryptDES(compactJws, key);
        }
        return compactJws;
    }


    /**
     * token解析
     *
     * @param token
     * @return
     */
    public static Claims parseToken(String key, String token) {
        //jwt生成的token进行des解密
        String destoken = EncryptUtils.decryptDES(token, key);
        SecretKeySpec secreKey = creatSecretKeySpec(key);
        Claims claims = Jwts.parser().setSigningKey(secreKey).parseClaimsJws(destoken).getBody();
        if (claims == null || !claims.containsKey(JWT_ATTRIBUTE_MOBLIE) || !claims.containsKey(JWT_ATTRIBUTE_SESSIONID)) {
            throw new OpenAlertException("token错误");
        }
        return claims;
    }

    /**
     * Claims 结果对比
     *
     * @param toClaims   用户传入的参数解析后的Claims对象
     * @param realClaims 真实的Claims对象
     * @return
     */
    public static boolean contrast(Claims toClaims, Claims realClaims) {
        boolean result = false;
        if (toClaims.get(JWT_ATTRIBUTE_MOBLIE).equals(realClaims.get(JWT_ATTRIBUTE_MOBLIE))
                && toClaims.get(JWT_ATTRIBUTE_SESSIONID).equals(realClaims.get(JWT_ATTRIBUTE_SESSIONID))) {
            result = true;
        }
        return result;
    }

    /**
     * 封装jwt生成token需要的Claims
     *
     * @param mobile
     * @return
     */
    public static Map<String, Object> getClaims(String mobile) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(JWT_ATTRIBUTE_MOBLIE, mobile);
        map.put(JWT_ATTRIBUTE_SESSIONID, RandomValueUtils.uuid());
        return map;
    }

    /**
     * 封装响应客户端登录token结果
     *
     * @param token
     * @param sessionId
     * @param time
     * @return
     */
    public static Map<String, Object> resultToken(String token, String sessionId, Long time) {
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("sessionId", sessionId);
        result.put("time", time);
        return result;
    }
}
