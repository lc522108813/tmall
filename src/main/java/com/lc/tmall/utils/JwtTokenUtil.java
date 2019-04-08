package com.lc.tmall.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenUtil {
    /** 当前的Claims当中又两个信息
     * 1 sub:xxx 表示主体
     * 2 created: xxx 表示该负载生成的时间
     * **/
    private static final String CLAIM_KEY_USERNAME="sub";
    private static final String CLAIM_KEY_CREATED="created";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;



    /** 根据用户信息生成token **/
    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims=new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME,userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claims);
    }


    /** 根据负载生成token **/
    public String generateToken(Map<String,Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    private Date generateExpirationDate(){
        return new Date(System.currentTimeMillis()+expiration*1000);
    }

    /** 从token当中获取负载 **/
    public Claims getClaimsFromToken(String token){
        Claims claims=null;
        try{
            // 在当前场景中，负载只使用了 sub已经 expireDate
            claims=Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

        }catch (Exception e){
            log.info("jwt格式校验失败 {}",token);
        }
        return claims;
    }

    /** 从token当中获取用户 **/
    public String getUsernameFromToken(String token){
        String username;
        try{
            Claims claims=getClaimsFromToken(token);
            username=claims.getSubject();
        }catch (Exception e){
            username=null;
        }
        return username;
    }

    /** 校验token是否有效 **/
    /** 不会空指针，因为调用方的 UserDetails在查询动作中不会为空，否则会报账号或密码错误异常 **/
    public boolean validateToken(String token,UserDetails userDetails){
        String username=getUsernameFromToken(token);
        return username.equals(userDetails.getUsername())&& !isTokenExpired(token);
    }

    /** 从token当中获取过期时间 **/
    public Date getExpiredDateFromToken(String token){
        Claims claims=getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /** 校验token是否过期 **/
    public boolean isTokenExpired(String token){
        Date date=getExpiredDateFromToken(token);
        return new Date().after(date);
    }

    /** 判断token是否可以被刷新 **/
    public boolean canRefresh(String token){
        return !isTokenExpired(token);
    }

    /** 刷新token **/
    public String refreshToken(String token){
        if(!canRefresh(token)){
            return null;
        }
        Claims claims=getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claims);
    }
}
