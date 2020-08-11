package com.yingda.lkj.interceptor;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.service.system.UserService;
import com.yingda.lkj.utils.IpUtil;
import com.yingda.lkj.utils.StringUtils;
import com.yingda.lkj.utils.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * token解析拦截器
 *
 * @author hood  2019/12/18
 */
public class TokenInterceptor implements HandlerInterceptor {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);

        if (StringUtils.isEmpty(token))
            throw new CustomException(new Json(JsonMessage.AUTH_ERROR, "未生成签名"));

        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null)
            throw new CustomException(new Json(JsonMessage.AUTH_ERROR, "签名不正确"));

        if (!JWTUtil.verify(token, username))
            throw new CustomException(new Json(JsonMessage.AUTH_ERROR));

        // 暂时把User放到request中便于controller和后面的拦截器获取到登录的用户,controller执行后，移除
        User user = userService.getUserByUserName(username);
        request.setAttribute(Constant.USER_ATTRIBUTE_NAME, user);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 暂时把User放到request中便于controller和后面的拦截器获取到登录的用户,controller执行后，移除
        request.removeAttribute(Constant.USER_ATTRIBUTE_NAME);
    }

    private String getToken(HttpServletRequest request) throws CustomException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            throw new CustomException(new Json(JsonMessage.AUTH_ERROR, "未生成签名"));

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(Constant.TOKEN_ATTRIBUTE_NAME))
                .reduce((x, y) -> y)
                .orElse(new Cookie(Constant.TOKEN_ATTRIBUTE_NAME, ""))
                .getValue();
    }
}
