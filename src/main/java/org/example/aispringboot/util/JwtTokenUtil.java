package org.example.aispringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.example.aispringboot.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final String issuer = "AI-PSYCHOLOGY";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }

    public static String generateToken(Long userId, String username, Integer roleType) {
        try {
            JwtConfig jwtConfig = getJwtConfig();
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());
            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expiration)
                    .withIssuedAt(new Date())
                    .withIssuer(issuer)
                    .sign(algorithm);
            return token;

        } catch (Exception e) {
            throw new RuntimeException("生成token失败" + e);
        }

    }

    //提取token
    public static String extractToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String tokenHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(tokenHeader)) {
            tokenHeader = request.getHeader("token");
        }
        if (!StringUtils.hasText(tokenHeader)) {
            return null;
        }
        String token = tokenHeader.trim();
        return token.regionMatches(true, 0, "Bearer ", 0, 7)
                ? token.substring(7).trim()
                : token;
    }
    //获取当前的token
    public static String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null){
           HttpServletRequest request = attributes.getRequest();
           String token=(String)request.getAttribute("jwtToken");
           if(token!=null)
           {
                return token;
           }
           //备用：从请求头中获取token
            String headerToken=extractToken(request);
           return headerToken;
        }
        return null;
    }
    //验证token
    public static JwtToken TokenVerificationResult(String token)
    {
        DecodedJWT jwt= validateToken(token);
        Long userId= jwt.getClaim("userId").asLong();
        String username= jwt.getClaim("username").asString();
        Integer roleType= jwt.getClaim("roleType").asInt();
        if(userId!=null&&StringUtils.hasText(username)&&roleType!=null){
            return new JwtToken(userId, username, roleType, true);
        }
        return null;
    }
    //验证token有效性
    public static DecodedJWT validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("token不能为空");
        }
        //token解码
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return jwtVerifier.verify(token);
    }

    //token返回包装类
    @Getter
    public static class JwtToken {
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final Boolean valid;

        public JwtToken(long userId, String username, Integer roleType, Boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }
}
