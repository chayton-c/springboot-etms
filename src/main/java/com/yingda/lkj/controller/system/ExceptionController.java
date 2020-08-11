package com.yingda.lkj.controller.system;

import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.controller.BaseController;
import com.yingda.lkj.utils.IpUtil;
import com.yingda.lkj.utils.RequestUtil;
import org.dom4j.rule.Mode;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author hood  2019/12/16
 */
@RestControllerAdvice
public class ExceptionController extends BaseController {

    private static final String AUTH_ERROR_PATH = "forward:/auth/loginPage";
    private static final String ERROR_PATH = "error";

    @ExceptionHandler(CustomException.class)
    public Object handleCustomException(CustomException customException) {
        if (customException.isNeedToLog())
            logger.error("customException:  ", customException);

        if (RequestUtil.isAjax(req))
            return customException.getJson();

        // 授权时异常,转发登录页
        if (customException.getJson().getErrorCode().equals(JsonMessage.AUTH_ERROR.getCode()))
            return new ModelAndView(AUTH_ERROR_PATH + "?errorMsg=" + customException.getJson().getMsg());

        return new ModelAndView(ERROR_PATH, Map.of("errorMsg", customException.getJson().getMsg()));
    }

    // 全局异常
    @ExceptionHandler(Exception.class)
    public Object handleCommonException(Exception exception) {
        logger.error("error in " + req.getRequestURI(), exception);

        if (RequestUtil.isAjax(req))
            return new Json(JsonMessage.SYS_ERROR);

        return new ModelAndView(ERROR_PATH, Map.of("errorMsg", exception.getMessage()));
    }

    @RequestMapping("/error/{errorMsg}")
    public ModelAndView error(@PathVariable String errorMsg) {
        return new ModelAndView(ERROR_PATH, Map.of("errorMsg", errorMsg));
    }

}