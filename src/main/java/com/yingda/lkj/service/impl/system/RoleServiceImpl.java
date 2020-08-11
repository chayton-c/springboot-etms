package com.yingda.lkj.service.impl.system;

import com.yingda.lkj.annotation.CacheMethod;
import com.yingda.lkj.beans.entity.system.Role;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.beans.system.cache.CacheMap;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hood  2019/12/18
 */
@CacheMethod
@Service("roleService")
public class RoleServiceImpl implements RoleService {

    private static final Map<String, Role> ROLE_MAP = new CacheMap<>();

    private BaseDao<Role> roleBaseDao;

    @Autowired
    private void setRoleBaseDao(BaseDao<Role> roleBaseDao) {
        this.roleBaseDao = roleBaseDao;
    }

    @Override
    public List<Role> showDown() {
        init();
        return new ArrayList<>(ROLE_MAP.values());
    }

    @Override
    public Role getRole(String id) {
        init();
        return Optional.ofNullable(id).map(ROLE_MAP::get).orElseGet(Role::new);
    }

    @Override
    public Role getRole(User user) {
        String roleId = user.getRoleId();
        return getRole(roleId);
    }

    @Override
    public void saveOrUpdate(Role role) {
        init();
        roleBaseDao.saveOrUpdate(role);
        ROLE_MAP.put(role.getId(), role);
    }


    @Override
    public Role getRoleByName(String name) throws CustomException {
        init();

        List<Role> roles = showDown().stream().filter(x -> name.equals(x.getRole())).collect(Collectors.toList());
        if (roles.isEmpty())
            throw new CustomException(new Json(JsonMessage.PARAM_INVALID, "找不到名称(role.role字段)为'" + name + "'的角色"));
        return roles.get(0);
    }

    private void init() {
        if (!ROLE_MAP.isEmpty())
            return;

        List<Role> roles = roleBaseDao.find("from Role");

        ROLE_MAP.putAll(roles.stream().collect(Collectors.toMap(Role::getId, role -> role)));
    }
}
