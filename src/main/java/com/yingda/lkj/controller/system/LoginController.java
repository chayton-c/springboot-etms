package com.yingda.lkj.controller.system;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.controller.BaseController;
import com.yingda.lkj.service.system.UserService;
import com.yingda.lkj.utils.DESUtil;
import com.yingda.lkj.utils.LicensingUtil;
import com.yingda.lkj.utils.StringUtils;
import com.yingda.lkj.utils.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author hood  2019/12/16
 */
@Controller
@RequestMapping("/auth")
public class LoginController extends BaseController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/loginPage")
    public ModelAndView logout(HttpServletRequest req) {
        String errorMsg = req.getParameter("errorMsg");
        if (StringUtils.isEmpty(errorMsg))
            return new ModelAndView("login");

        return new ModelAndView("login", Map.of("errorMsg", errorMsg));
    }

    /**
     *
     * @param akagi 账号，登保不让参数起名叫userName
     * @param kaga 密码
     */
    @RequestMapping(value = "/login")
    @ResponseBody
    public Json login(String akagi, String kaga) throws Exception {
        akagi = DESUtil.decrypt(DESUtil.KEY, akagi, DESUtil.KEY);
        kaga = DESUtil.decrypt(DESUtil.KEY, kaga, DESUtil.KEY);
        User user = userService.getUserByUserName(akagi);
        if (user == null)
            return new Json(JsonMessage.AUTH_ERROR, "找不到该用户");

        String realPassword = user.getPassword();
        if (!realPassword.equals(kaga))
            return new Json(JsonMessage.AUTH_ERROR, "密码错误");

        Json validate = LicensingUtil.validate();
        if (!validate.isSuccess()) {
            userService.login(user);
            return validate;
        }

        // 验证是否修改了系统时间到证书验证时间前
        Timestamp loginTime = user.getLoginTime();
        long certValidateTime = (long) validate.getObj();
        if (loginTime != null && certValidateTime <= loginTime.getTime())
            return new Json(JsonMessage.LICENSING_ERROR, "检测到您修改了服务器系统时间，请联系管理员后重试");
        userService.login(user);

        String token = JWTUtil.createToken(akagi);
        resp.addCookie(new Cookie(Constant.TOKEN_ATTRIBUTE_NAME, token));
        return new Json(JsonMessage.SUCCESS, Map.of("token", token));
    }

    // TODO 密码未加密
    @RequestMapping(value = "/test")
    @ResponseBody
    public Json test(String gongshen) {
        if ("huoerdi".equals(gongshen))
            return new Json(JsonMessage.SUCCESS);

        return new Json(JsonMessage.PARAM_INVALID);
    }
}