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

    private static final String SECRET = "weicanjie_super_secret_key_1234567890";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 token
    public String createToken(AdminUser user) {
        return Jwts.builder()
                .claim("uid", user.getId())
                .claim("role", user.getRole())
                .claim("restaurantId", user.getRestaurantId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

