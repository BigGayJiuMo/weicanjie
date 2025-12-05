package com.jiumo.weicanjie.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "admin-secret-key";
    private static final long EXPIRE = 24 * 60 * 60 * 1000; // 24小时

    // 创建 token
    public String createToken(Long adminId) {
        return Jwts.builder()
                .setSubject(adminId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // 解析 token
    public Long parseToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}
