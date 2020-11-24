package com.fun.gateway.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成token以及校验token相关方法
 * 当前类基于 JJWT 0.11.2 版本
 *
 * @author mrdjun
 */
public class JwtUtils {
    private final static String JWT_PAYLOAD_ROLES = "FUN_ROLES";
    private final static String JWT_PAYLOAD_PERMISSIONS = "FUN_PERMISSIONS";

    /**
     * 公钥解析 token
     *
     * @param token     用户请求中的token
     * @param publicKey 公钥
     * @return Jws<Claims>
     */
    public static Jws<Claims> parserToken(String token, PublicKey publicKey)
            throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException{
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
    }

    /**
     * 获取 Token 中的账号
     *
     * @param token     token
     * @param publicKey publicKey
     * @return loginName
     */
    public static String getLoginName(String token, PublicKey publicKey)
            throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return parserToken(token, publicKey).getBody().getSubject();
    }

    /**
     * 获取 Token 中的用户菜单
     *
     * @param token     token
     * @param publicKey publicKey
     * @return List<String>
     */
    public static List<String> getPerms(String token, PublicKey publicKey) {
        String perms = parserToken(token, publicKey).getBody().get(JWT_PAYLOAD_PERMISSIONS).toString();
        return Arrays.stream(perms.split(",")).collect(Collectors.toList());
    }

}