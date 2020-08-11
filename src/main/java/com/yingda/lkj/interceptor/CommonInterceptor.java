package com.yingda.lkj.interceptor;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.utils.IpUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hood  2020/7/21
 */
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception  {
        if ("http://ylog.hiido.com/c.gif".equals(request.getRequestURL().toString()))
            return false;
//        System.out.println(IpUtil.getIp(request));
        System.out.println(request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }
}
