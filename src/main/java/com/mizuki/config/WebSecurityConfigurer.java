package com.mizuki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
        userDetailsService.createUser(User.withUsername("aaa").password("{noop}aaa").roles("admin").build());
        return userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("自定义Authentication");
        auth.userDetailsService(userDetailsService());
    }

    @Override
    @Bean // 用于将自定义的 authenticationManager 在工厂中暴露 可以在任何位置注入
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .mvcMatchers("/login.html").permitAll()
                .mvcMatchers("/index").permitAll() // 放行资源必须放在所有认证请求之前
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html") // 修改默认登录界面
                .loginProcessingUrl("/doLogin") //
                .usernameParameter("uname") // 改变参数名
                .passwordParameter("pwd")
                //.successForwardUrl("/index") // 跳转，不能与defaultSuccessUrl一起使用
                //.defaultSuccessUrl("/index", true) // 相当于重定向，但是会重定向到上次保存的地址，参数加上true可实现successForwardUrl相同效果
                //.failureForwardUrl("/login.html") // 失败跳转 信息存在request域中
                //.failureUrl("/login.html") // 失败重定向 信息存在session域中
                .successHandler(new MyAuthenticationSuccessHandler()) // 前后端分离
                .failureHandler(new MyAuthenticationFailureHandler()) // 前后端分离
                .and()
                .logout()
                //.logoutUrl("/logout")
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/aa", "GET"),
                        new AntPathRequestMatcher("/bb", "POST")
                ))
                .logoutSuccessHandler(new MyLogoutSuccessHandler()) // 前后端离
                //.logoutSuccessUrl("/login.html")
                .and()
                .csrf().disable(); // 关闭 csrf 跨站请求保护
    }
}
