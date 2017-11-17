package com.sweetjandy.remindr;

import com.sweetjandy.remindr.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
//@EnableWebSecurity
public class SecurityConfiguration
//        extends WebSecurityConfigurerAdapter
{

    @Autowired
    private UserDetailsLoader userDetails;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/index") // user's home page, it can be any URL
//                .permitAll() // Anyone can go to the login page
//                .and()
//                .authorizeRequests()
//                .antMatchers("/") // anyone can see the home and logout page
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/login?logout") // append a query string value
//                .and()
//                .authorizeRequests()
//                .antMatchers("") // only authenticated users can create ads
//                .authenticated()
//        ;
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
//    }
}
