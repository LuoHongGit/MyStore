package cn.lh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 配置跨域访问过滤器
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        //创建cors配置类
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //添加配置
        corsConfiguration.addAllowedOrigin("http://manage.mystore.com");
        corsConfiguration.addAllowedOrigin("http://www.mystore.com");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");

        //创建配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();

        //将配置注册到配置源对象
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);

        //创建cors过滤器对象并返回
        return new CorsFilter(configurationSource);
    }
}
