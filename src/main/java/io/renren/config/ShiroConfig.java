/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.config;

import io.renren.modules.sys.oauth2.OAuth2Filter;
import io.renren.modules.sys.oauth2.OAuth2Realm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class ShiroConfig {

    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oAuth2Realm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new OAuth2Filter());
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/webjars/**", "anon");
//        filterMap.put("/druid/**", "anon");
//        filterMap.put("/app/**", "anon");
//        filterMap.put("/sys/login", "anon");
//        filterMap.put("/swagger/**", "anon");
//        filterMap.put("/v2/api-docs", "anon");
//        filterMap.put("/swagger-ui.html", "anon");
//        filterMap.put("/swagger-resources/**", "anon");
//        filterMap.put("/captcha.jpg", "anon");
//        filterMap.put("/aaa.txt", "anon");
//        filterMap.put("/dm/**", "anon");
//        filterMap.put("/sys/**", "anon");
//        filterMap.put("/sys/logout", "oauth2");
        filterMap.put("/dm/query*", "anon");
        filterMap.put("/dm/**", "oauth2");

        filterMap.put("/yjdm/query*", "anon");
        filterMap.put("/yjdm/**", "oauth2");

        filterMap.put("/hydm/query*", "anon");
        filterMap.put("/hydm/**", "oauth2");

        filterMap.put("/video/query*", "anon");
        filterMap.put("/video/**", "oauth2");

        filterMap.put("/ydjcj/query*", "anon");
        filterMap.put("/ydjcj/**", "oauth2");

        filterMap.put("/xlgl/query*", "anon");
        filterMap.put("/xlgl/**", "oauth2");

        filterMap.put("/jj/query*", "anon");
        filterMap.put("/jj/**", "oauth2");

        filterMap.put("/cj/query*", "anon");
        filterMap.put("/cj/**", "oauth2");

        filterMap.put("/df/query*", "anon");
        filterMap.put("/df/**", "oauth2");

        filterMap.put("/mrtb/query*", "anon");
        filterMap.put("/mrtb/**", "oauth2");

        filterMap.put("/tzbwbz/query*", "anon");
        filterMap.put("/tzbwbz/**", "oauth2");

        filterMap.put("/mrtz/query*", "anon");
        filterMap.put("/mrtz/**", "oauth2");

        filterMap.put("/xscz/query*", "anon");
        filterMap.put("/xscz/**", "oauth2");

        filterMap.put("/yjdd/query*", "anon");
        filterMap.put("/yjdd/**", "oauth2");

        filterMap.put("/**", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
