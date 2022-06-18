package com.pzhu.substitute.filter;

import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.utils.JwtUtil;
import com.pzhu.substitute.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author dengyiqing
 * @description JWT过滤器
 * @date 2022/1/15
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authentication = request.getHeader(CommonConstants.JWT_HEADER);
        if (!StringUtils.hasText(authentication) || authentication.equals("null")) {
            filterChain.doFilter(request, response);
            return;
        }
        Claims claims = JwtUtil.parseJWT(CommonConstants.JWT_SALT, authentication);
        String id = claims.getSubject();
        String redisKey;
        if (CommonConstants.ADMIN_ROLE.equals(claims.get("role"))) {
            filterChain.doFilter(request, response);
            return;
        } else if (CommonConstants.USER_ROLE.equals(claims.get("role"))) {
            redisKey = CommonConstants.USER_PREFIX + id + CommonConstants.LOGIN_SUFFIX;
        } else {
            redisKey = CommonConstants.DRIVER_PREFIX + id + CommonConstants.LOGIN_SUFFIX;
        }
        UserDetails login = (UserDetails) redisUtil.get(redisKey);
        if (Objects.isNull(login)) {
            throw new BizException(ResultCode.UNAUTHENTICATED);
        }
        log.debug("存入SecurityContextHolder");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login, null, login.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
