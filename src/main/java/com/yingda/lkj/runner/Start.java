package com.yingda.lkj.runner;

import com.yingda.lkj.beans.Constant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * @author hood  2020/4/7
 */
@Component
@Order(1) // 执行顺序
public class Start implements ApplicationRunner {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("config");

    private String debug = bundle.getString("debug");
    private String projectName = bundle.getString("projectName");

    @Override
    public void run(ApplicationArguments args) {
        Constant.DEBUG = Boolean.parseBoolean(debug);
        Constant.projectName = this.projectName;
    }
}
