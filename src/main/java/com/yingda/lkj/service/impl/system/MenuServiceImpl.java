package com.yingda.lkj.service.impl.system;

import com.yingda.lkj.annotation.CacheMethod;
import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.system.cache.CacheMap;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.system.MenuService;
import com.yingda.lkj.utils.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hood  2019/12/18
 */
@CacheMethod
@Service("menuSerivce")
public class MenuServiceImpl implements MenuService {

    /** key:id */
    private static final Map<String, Menu> MENU_MAP = new CacheMap<>();

    private BaseDao<Menu> menuBaseDao;

    @Autowired
    public void setMenuBaseDao(BaseDao<Menu> menuBaseDao) {
        this.menuBaseDao = menuBaseDao;
    }

    @Override
    public List<Menu> showDown() {
        init();
        return new ArrayList<>(MENU_MAP.values())
                .stream()
                .sorted(Comparator.comparingInt(Menu::getSeq))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> getVueMenus() {
        return showDown().stream().filter(x -> x.getType() == Menu.VUE).collect(Collectors.toList());
    }

    @Override
    public Menu getMenuByUrl(String url) {
        init();
        List<Menu> menus = showDown();
        return menus.stream()
                .filter(x -> x.getUrl().equals(url))
                .sorted(Comparator.comparingInt(Menu::getSeq))
                .reduce(null, (x, y) -> y);
    }



    @Override
    public Menu getMenuById(String id) {
        init();
        return Optional.ofNullable(id).map(MENU_MAP::get).orElseGet(Menu::new);
    }

    @Override
    public void saveOrUpdate(Menu menu) {
        init();
        menuBaseDao.saveOrUpdate(menu);
        MENU_MAP.put(menu.getId(), menu);
    }

    @Override
    public void delete(List<String> ids) {
        init();
        // 下级菜单也一起删除
        List<String> slaveIds = showDown().stream().filter(x -> ids.contains(x.getPid())).map(Menu::getId).collect(Collectors.toList());
        ids.addAll(slaveIds);

        ids.forEach(MENU_MAP::remove);
        menuBaseDao.executeHql("delete from Menu where id in (:ids)", Map.of("ids", ids));
    }

    @Override
    public List<Menu>  jsonified(List<Menu> menus) {
        List<Menu> parentMenus = menus.stream().filter(x -> Menu.ROOT_ID.equals(x.getPid())).collect(Collectors.toList());

        // 默认只有三级
        for (Menu parent : parentMenus) {
            parent.setMenuList(
                    menus.stream()
                            .filter(x -> x.getPid().equals(parent.getId()))
                            .sorted(Comparator.comparingInt(Menu::getSeq))
                            .collect(Collectors.toList())
            );
        }

        // 二级加入三级
        List<List<Menu>> list = StreamUtil.getList(parentMenus, Menu::getMenuList);
        for (List<Menu> menuList : list) {
            for (Menu menu : menuList) {
                menu.setMenuList(
                        menus.stream().filter(x -> x.getPid().equals(menu.getId()))
                        .sorted(Comparator.comparingInt(Menu::getSeq))
                        .collect(Collectors.toList())
                );
            }
        }

        return parentMenus.stream()
                .sorted(Comparator.comparingInt(Menu::getSeq))
                .collect(Collectors.toList());
    }

    private void init() {
        if (!MENU_MAP.isEmpty())
            return;

        List<Menu> menus = menuBaseDao.find("from Menu");

        MENU_MAP.putAll(menus.stream().collect(Collectors.toMap(Menu::getId, menu -> menu)));
    }


}
