package com.yingda.lkj.controller.system;

import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.controller.BaseController;
import com.yingda.lkj.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * <span>角色管理页</span>
 * <span>角色修改，因为是跟授权一起的，所以放在AuthController里了</span>
 *
 * @author hood  2019/12/18
 */
@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    private RoleService roleService;

    /**
     * 角色列表
     */
    @RequestMapping("")
    public ModelAndView role() {
        return new ModelAndView("/system/role", Map.of("roles", roleService.showDown()));
    }

    @RequestMapping("/getRoles")
    @ResponseBody
    public Json getRoles() {
        return new Json(JsonMessage.SUCCESS, roleService.showDown());

    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
