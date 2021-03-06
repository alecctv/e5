package io.qyi.e5.config.security;

import com.google.gson.Gson;
import io.qyi.e5.util.ResultUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2019-12-27 08:57
 **/
@Component
public class SecurityAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler , LogoutSuccessHandler  {
    @Autowired
    RedisUtil redisUtil;
    @Value("${redis.user.token}")
    String token_;

    private static Gson gson = new Gson();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken at = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        Map<String, Object> token = new HashMap<>();
        token.put("token", at.getToken());
        token.put("username", at.getName());
        token.put("authority", at.getAuthority());
        token.put("expire", redisUtil.getExpire(token_ + at.getToken()));
        writer.write(gson.toJson(ResultUtil.success(token)) );
        writer.flush();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write(gson.toJson(ResultUtil.error(-1, "failed!")));
        writer.flush();
    }


    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write("logout success");
        writer.flush();
    }
}
