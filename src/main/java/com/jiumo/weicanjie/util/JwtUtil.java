package com.jiumo.weicanjie.util;

import com.jiumo.weicanjie.entity.AdminUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // 秘钥必须足够长，否则会报错（HS256 至少 32 字节）
    private static final String SECRET = "weicanjie_super_secret_key_1234567890";
    private static final long EXPIRE = 1000 * 60 * 60 * 24; // 24小时

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 Token */
    public String createToken(AdminUser user) {
        return Jwts.builder()
                .claim("uid", user.getId())
                .claim("role", user.getRole())
                .claim("restaurantId", user.getRestaurantId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** 解析 Token */
    public Long parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }
}
