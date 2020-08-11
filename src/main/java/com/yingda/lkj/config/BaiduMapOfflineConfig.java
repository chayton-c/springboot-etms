package com.yingda.lkj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ResourceBundle;

/**
 * @author hood  2020/7/23
 */
@Configuration
public class BaiduMapOfflineConfig implements WebMvcConfigurer {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("config");

    private String baiduMapOfflineTiles = bundle.getString("baidu_map_offline_tiles");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/baiduMapOfflineTiles/**").addResourceLocations("file:" + baiduMapOfflineTiles);
    }
}
