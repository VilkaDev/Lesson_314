package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private final SuccessUserHandler successUserHandler;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()   //защита от межсайтовой подделки запроса
                .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/user").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated()   //Все остальные страницы требуют аутентификации
                .and()
                //Настройка для входа в систему
                    .formLogin()
                    .successHandler(successUserHandler)
                    .permitAll()
                .and()
                    .logout()
                    .invalidateHttpSession(true)    // настраивает сеанс таким образом, чтобы он не становился недействительным при выходе из системы
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login") // перенарпавление на страницу после успешного входа
                    .permitAll();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    // преобразует наши пароли в bcrypt
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);    //предоставляет юзеров
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }


    // аутентификация inMemory
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()    //builder()
//                        .username("user")
//                        .password("user")
//                        .roles("USER")
//                        .build();
//
//        UserDetails admin =
//                User.builder()
//                        .username("admin")
//                        .password("admin")
//                        .roles("ADMIN", "USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    // jdbc Authentication
//    @Bean
//    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
//        // как положить юзеров
//        UserDetails user =
//                User.builder()
//                        .username("user")
//                        .password("user")
//                        .roles("USER")
//                        .build();
//
//        UserDetails admin =
//                User.builder()
//                        .username("admin")
//                        .password("admin")
//                        .roles("ADMIN", "USER")
//                        .build();
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(user.getUsername());
//        }
//        if (jdbcUserDetailsManager.userExists(admin.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(admin.getUsername());
//        }
//        jdbcUserDetailsManager.createUser(user);
//        jdbcUserDetailsManager.createUser(admin);
//
//        return jdbcUserDetailsManager;
//
//    }
}