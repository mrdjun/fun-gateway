package com.fun.gateway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 白名单不做鉴权直接放行
 * 静态资源可以做 nginx 的动静分离，白名单将 nginx 的静态资源路径配置在此即可
 *
 * @author MrDJun 2020/10/27
 */
public class WhiteUrlProperties {
    private static final String[] ENDPOINTS = {
            "/oauth/**",
            "/actuator/**",
            "/*/v2/api-docs",
            "/swagger/api-docs",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
    };

    private static final String[] staticResources = {
            "/",
            "/*.html",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
    };

    private final String[] httpUrls = {

    };

    /**
     * 设置认证后不需要判断具体权限的 url，所有登录的账号都能访问
     */
    private final String[] menusPaths = {};

    public String[] getUrls() {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(ENDPOINTS));
        list.addAll(Arrays.asList(httpUrls));
        list.addAll(Arrays.asList(staticResources));

        String[] urls = new String[list.size()];
        return list.toArray(urls);
    }
}
