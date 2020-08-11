package com.yingda.lkj.utils;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.service.system.UserService;
import com.yingda.lkj.utils.jwt.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hood  2019/12/18
 */
public class RequestUtil {

    public static void checkParameters(HttpServletRequest req, String... paramNames) throws CustomException {
        for (String paramName : paramNames)
            if (StringUtils.isEmpty(req.getParameter(paramName)))
                throw new CustomException(JsonMessage.PARAM_INVALID, String.format("缺少%s参数", paramName));
    }

    public static Map<String, Object> getParameterMap(HttpServletRequest req) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, Object> params = new HashMap<>();
        parameterMap.forEach((k, v) -> {
            params.put(k, String.join(",", v));
        });
        return params;
    }

    private static UserService getUserService() {
        return SpringContextUtil.getBean(UserService.class);
    }

    public static User getUser(HttpServletRequest request) {
        return (User) request.getAttribute(Constant.USER_ATTRIBUTE_NAME);
    }

    public static String getSectionId(HttpServletRequest request) { return getUser(request).getSectionId(); }

    public static boolean isAjax(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With")) || req.getRequestURI().contains("app");
    }
}
