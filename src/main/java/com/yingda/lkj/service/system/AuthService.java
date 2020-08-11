package com.yingda.lkj.service.system;

import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.entity.system.RoleMenu;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.system.Json;

import java.util.List;

/**
 * @author hood  2019/12/18
 */
public interface AuthService {

    void saveOrUpdate(RoleMenu roleMenu);

    void deleteRoleMenu(RoleMenu roleMenu);

    /**
     * 删除roleId下的所有roleMenu,再按照menuIds和roleId添加
     */
    void updateAuth(String roleId, List<String> menuIds);

    /**
     * 删除roleId下的所有roleMenu,再按照menuIds和roleId添加
     */
    void updateVueAuth(String roleId, List<String> menuIds);

    /**
     * 对应角色是否允许访问该页面
     */
    boolean hasAccess(String roleId, String menuId);

    List<Menu> getValuableMenus(User user);

    List<Menu> getVueValuableMenus(User user);
}
