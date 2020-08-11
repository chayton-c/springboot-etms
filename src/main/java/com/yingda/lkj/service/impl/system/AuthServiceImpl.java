package com.yingda.lkj.service.impl.system;

import com.yingda.lkj.annotation.CacheMethod;
import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.entity.system.RoleMenu;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.system.cache.CacheMap;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.system.AuthService;
import com.yingda.lkj.service.system.MenuService;
import com.yingda.lkj.utils.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hood  2019/12/18
 */
@CacheMethod
@Service("authService")
public class AuthServiceImpl implements AuthService {

    private BaseDao<RoleMenu> roleMenuBaseDao;
    private MenuService menuService;

    // key为 roleId + menuId 注意不是id
    private static final Map<String, RoleMenu> ROLE_MENU_MAP = new CacheMap<>();

    @Override
    public void saveOrUpdate(RoleMenu roleMenu) {
        init();
        roleMenuBaseDao.saveOrUpdate(roleMenu);
        ROLE_MENU_MAP.put(roleMenu.getRoleId() + roleMenu.getMenuId(), roleMenu);
    }

    @Override
    public void deleteRoleMenu(RoleMenu roleMenu) {
        init();
        roleMenuBaseDao.delete(roleMenu);
        ROLE_MENU_MAP.remove(roleMenu.getRoleId() + roleMenu.getMenuId());
    }

    @Override
    public void updateAuth(String roleId, List<String> menuIds) {
        init();
        List<String> templateMenuIds = menuService.showDown().stream().filter(x -> x.getType() == Menu.THYMELEAF).map(Menu::getId).collect(Collectors.toList());
        // 先删除roleId下的所有roleMenu
        List<RoleMenu> roleMenus = roleMenuBaseDao.find(
                "from RoleMenu where roleId = :roleId and menuId in :templateMenuIds",
                Map.of("roleId", roleId, "templateMenuIds", templateMenuIds)
        );
        roleMenus.forEach(this::deleteRoleMenu);

        // 再添加menuIds对应的
        for (String menuId : menuIds)
            saveOrUpdate(new RoleMenu(roleId, menuId));
    }

    @Override
    public void updateVueAuth(String roleId, List<String> menuIds) {
        init();
        List<String> vueMenuIds = StreamUtil.getList(menuService.getVueMenus(), Menu::getId);
        // 先删除roleId下的所有roleMenu
        List<RoleMenu> roleMenus = roleMenuBaseDao.find(
                "from RoleMenu where roleId = :roleId and menuId = :vueMenuIds",
                Map.of("roleId", roleId, "vueMenuIds", vueMenuIds)
        );
        roleMenus.forEach(this::deleteRoleMenu);

        // 再添加menuIds对应的
        for (String menuId : menuIds)
            saveOrUpdate(new RoleMenu(roleId, menuId));
    }

    @Override
    public boolean hasAccess(String roleId, String menuId) {
        init();
        return ROLE_MENU_MAP.get(roleId + menuId) != null;
    }

    @Override
    public List<Menu> getValuableMenus(User user) {
        return menuService
                .showDown()
                .stream()
                .filter(x -> x.getType() == Menu.THYMELEAF)
                .filter(x -> hasAccess(user.getRoleId(), x.getId()))
                .filter(x -> x.getHide().equals(Constant.SHOW) || user.getUserName().equals("huoerdi")) // 霍尔蒂什么都给看
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> getVueValuableMenus(User user) {
        return menuService
                .showDown()
                .stream()
                .filter(x -> x.getType() == Menu.VUE)
                .filter(x -> hasAccess(user.getRoleId(), x.getId()))
                .filter(x -> x.getHide().equals(Constant.SHOW) || user.getUserName().equals("huoerdi")) // 霍尔蒂什么都给看
                .collect(Collectors.toList());
    }

    private void init() {
        if (!ROLE_MENU_MAP.isEmpty())
            return;
        List<RoleMenu> roleMenus = roleMenuBaseDao.find("from RoleMenu");
        ROLE_MENU_MAP.putAll(roleMenus.stream().collect(Collectors.toMap(x -> x.getRoleId() + x.getMenuId(), x -> x)));
    }

    @Autowired
    public void setRoleMenuBaseDao(BaseDao<RoleMenu> roleMenuBaseDao) {
        this.roleMenuBaseDao = roleMenuBaseDao;
    }

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }
}
