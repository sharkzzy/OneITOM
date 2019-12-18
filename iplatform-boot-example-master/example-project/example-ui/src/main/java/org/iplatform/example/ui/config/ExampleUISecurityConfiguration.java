package org.iplatform.example.ui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(102)
public class ExampleUISecurityConfiguration extends WebSecurityConfigurerAdapter {

	//设置认证不拦截规则
	@Override
	public void configure(WebSecurity web) throws Exception {
        //自定义跳过认证拦截的路径
        //web.ignoring().antMatchers("/**");		
	}
}
