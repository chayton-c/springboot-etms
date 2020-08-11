package com.yingda.lkj.utils.layui;

import java.util.HashMap;
import java.util.List;

public class LayuiTreeUtil extends HashMap<String, Object> {
    public static LayuiTreeUtil data(Integer count, List<?> data) {

        LayuiTreeUtil layuiTreeUtil = new LayuiTreeUtil();
        layuiTreeUtil.put("code", 0);
        if (count == 0)
            layuiTreeUtil.put("msg", "无数据");
        layuiTreeUtil.put("msg", "");
        layuiTreeUtil.put("count", count);
        layuiTreeUtil.put("data", data);

        return layuiTreeUtil;
    }


    public static LayuiTreeUtil data(Object obj) {
        Object[] data = {obj};
        LayuiTreeUtil layuiTreeUtil = new LayuiTreeUtil();
        layuiTreeUtil.put("code", 0);
        layuiTreeUtil.put("msg", "");
        layuiTreeUtil.put("count", 1);
        layuiTreeUtil.put("data", data);

        return layuiTreeUtil;
    }

}
