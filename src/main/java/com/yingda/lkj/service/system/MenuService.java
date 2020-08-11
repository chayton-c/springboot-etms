package com.yingda.lkj.service.system;

import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.utils.pojo.CollectUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <span>菜单操作</span>
 * <span>注意：这里默认只有两级菜单</span>
 *
 * @author hood  2019/12/18
 */
public interface MenuService {

    // getAllObjects
    List<Menu> showDown();

    List<Menu> getVueMenus();

    Menu getMenuByUrl(String url);

    Menu getMenuById(String id);

    void saveOrUpdate(Menu menu);

    void delete(List<String> ids);

    /**
     * 把一组menu通过pid转为menuTree,默认只有三级
     */
    List<Menu> jsonified(List<Menu> menus);

}
