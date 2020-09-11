package com.lynknow.api.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "lynknow-resource-id";
    public static final String CLIENT_ID = "lynknow-client-id";
    public static final String CLIENT_SECRET = "lynknow-client-secret";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/authorize").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/api/oauth/authorize").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/check_token").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/api/oauth/check_token").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/user/register-sysadmin").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/password/**").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/test/check",
                        "/test/check-db",
                        "/roles/**",
                        "/public/**",
                        "/users/admin-register",
                        "/users/subs-register",
                        "/users/forgot-password",
                        "/users/check-token",
                        "/users/reset-password",
                        "/auth/login-facebook",
                        "/auth/login-google").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .access("isAuthenticated()")
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

}
