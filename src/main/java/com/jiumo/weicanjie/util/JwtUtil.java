package com.jiumo.weicanjie.util;

import com.jiumo.weicanjie.entity.AdminUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类，负责生成和解析 JSON Web Token。
 * 使用 HMAC SHA-256 算法生成签名并对 token 进行验证。
 */
@Component
public class JwtUtil {

    private static final String SECRET = "weicanjie_super_secret_key_1234567890";  // 密钥，用于签名和验证 token

    /**
     * 获取签名密钥（HMACSHA256）。
     *
     * @return 返回生成的 SecretKey 实例
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token。
     * 根据提供的管理员信息生成一个包含管理员 ID、角色、餐厅 ID 等信息的 JWT token。
     *
     * @param user 管理员信息
     * @return 返回生成的 JWT token 字符串
     */
    public String createToken(AdminUser user) {
        return Jwts.builder()
                .claim("uid", user.getId())  // 用户 ID
                .claim("role", user.getRole())  // 用户角色
                .claim("restaurantId", user.getRestaurantId() != null ? user.getRestaurantId() : 0)  // 处理 null 情况，给默认值
                .setIssuedAt(new Date())  // 设置 token 的发行时间
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 设置 token 的过期时间（1天）
                .signWith(getKey(), SignatureAlgorithm.HS256)  // 使用密钥和算法签名生成 token
                .compact();  // 返回生成的 token
    }

    /**
     * 解析 JWT token 并提取 Claims。
     *
     * @param token JWT token 字符串
     * @return 返回解析后的 Claims 对象，包含 token 中的所有信息
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())  // 设置签名验证的密钥
                .build()
                .parseClaimsJws(token)  // 解析 token
                .getBody();  // 返回解析出的 Claims 对象
    }
}
