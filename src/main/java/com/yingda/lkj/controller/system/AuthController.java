package com.yingda.lkj.controller.system;

import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.entity.system.Role;
import com.yingda.lkj.beans.entity.system.RoleMenu;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.controller.BaseController;
import com.yingda.lkj.service.base.BaseService;
import com.yingda.lkj.service.system.MenuService;
import com.yingda.lkj.service.system.AuthService;
import com.yingda.lkj.service.system.RoleService;
import com.yingda.lkj.utils.RequestUtil;
import com.yingda.lkj.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hood  2019/12/25
 */
@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private RoleService roleService;
    private MenuService menuService;
    private AuthService authService;
    private BaseService<RoleMenu> roleMenuBaseService;

    private Role role;

    @ModelAttribute
    public void getRole(Role role) {
        this.role = role;
    }

    /**
     *  授权详情页
     */
    @RequestMapping("")
    public ModelAndView auth(String roleId) throws Exception {
        roleId = roleId == null ? "" : roleId;
        Role role = Optional.ofNullable(roleService.getRole(roleId)).orElse(new Role());

        String hql = "from RoleMenu where roleId = :roleId";
        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        List<RoleMenu> auths = roleMenuBaseService.find(hql, params);

        List<String> accessMenuIds = auths.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());

        List<Menu> menus = menuService.showDown();
        for (Menu menu : menus)
            menu.setHasAuth(accessMenuIds.contains(menu.getId()));

        List<Menu> menuTree = menuService.jsonified(menus);
        return new ModelAndView("/system/auth", Map.of("role", role, "menuTree", menuTree));
    }

    /**
     * 角色添加/修改，权限修改
     */
    @PostMapping("/updateAuth")
    @ResponseBody
    public Json updateAuth() {
        // 我懒得写一起加事务了，没啥影响，谁不满意谁写

        Timestamp current = new Timestamp(System.currentTimeMillis());
        // 先保存角色
        if (StringUtils.isEmpty(role.getId())) {
            role.setId(UUID.randomUUID().toString());
            role.setAddTime(current);
        }
        role.setUpdateTime(current);
        roleService.saveOrUpdate(role);

        // 在修改角色
        String[] menus = req.getParameterMap().get("menus");
        authService.updateAuth(role.getId(), Optional.ofNullable(menus).map(Arrays::asList).orElseGet(ArrayList::new));

        return new Json(JsonMessage.SUCCESS);
    }

    /**
     * vue用的
     */
    @PostMapping("/updateAuthMenus")
    @ResponseBody
    public Json updateAuthMenus() {
        String roleId = req.getParameter("roleId");

        // 在修改角色
        String[] menus = req.getParameterMap().get("menus");
        authService.updateVueAuth(roleId, Optional.ofNullable(menus).map(Arrays::asList).orElseGet(ArrayList::new));

        return new Json(JsonMessage.SUCCESS);
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @Autowired
    public void setRoleMenuBaseService(BaseService<RoleMenu> roleMenuBaseService) {
        this.roleMenuBaseService = roleMenuBaseService;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}

