package com.lc.tmall.component;

import com.lc.tmall.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * jwt登录权限过滤器
 **/
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            // 如果能够在header当中获取到 Authorization
            String authToken = authHeader.substring(this.tokenHead.length());
            // 从该token当中获取到 username信息
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            // 当从token当中解析出来的username不为空，并且SecurityContextHolder中的context未设置验证结果时
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 校验从token当中解析出来的 username与真正的username是否能对应上，并且判断token的有效期
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // 将请求信息设置到 UsernamePasswordAuthenticationToken 当中，包括了发请求的ip以及session等
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 设置SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }

        }
        chain.doFilter(request, response);
    }
}
