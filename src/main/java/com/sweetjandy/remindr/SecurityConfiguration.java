package com.sweetjandy.remindr;

import com.sweetjandy.remindr.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter
{

    @Autowired
    private UserDetailsLoader userDetails;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/profile") // user's home page, it can be any URL
                .permitAll() // Anyone can go to the login page
                .and()
                .authorizeRequests()
                .antMatchers("/") // anyone can see the home and logout page
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout") // append a query string value
                .and()
                .authorizeRequests()
                .antMatchers("/contacts", "/contacts/{id}", "/contacts/add", "/contacts/{id}/edit", "/google/contacts", "/profile", "/profile/edit", "/remindrs", "/remindrs/{id}", "/remindrs/create", "/remindrs/{id}/edit", "/remindrs/{id}/confirm-delete", "/remindrs/{id}/delete") // only authenticated users can look at their contacts, add contacts, edit contacts, retrieve their google contacts, look at their profile, edit their profile, see their reminders, edit a reminder, and delete a reminder.
                .authenticated()
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
    }
}
