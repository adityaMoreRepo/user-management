package com.example.usermanagement.config;

import com.example.usermanagement.service.GroupUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)// Based on role user can access APIs
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private GroupUserDetailsService groupUserDetailsService;

    // == Authentication ==
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(groupUserDetailsService);
    }

   // == Authorization ==
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        /*
        CSRF stands for Cross-Site Request Forgery.
        It is an attack that forces an end user to execute
        unwanted actions on a web application in which they are
        currently authenticated.
         */
        //Allow permission for all the user roles for registration but for other apis enable authorization.
        http.authorizeRequests()
                .antMatchers("/v1/user/registration")
                .permitAll()
//                .antMatchers("/v1/admin/AllUsersByPage")
//                .hasAuthority("ROLE_ADMIN")
//                .anyRequest()
//                .authenticated()
                .and()
                .authorizeRequests()
                .antMatchers("/v1/**")
                .authenticated()
                .and()
                .httpBasic();
    }

    // == Beans ==
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // There is also another way to configure Authentication provider
//    @Bean
//    AuthenticationProver authenticationProver() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(groupUserDetailsService);
//        provider.setPasswordEncoder(new BCryptPasswordEncoder());
//        return provider;
//    }
}
