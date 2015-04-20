package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/login").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/main")
                .and()
            .logout()
                .permitAll();
    }

    @Autowired
    private DataSource datasource;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager();
        userDetailsService.setDataSource(datasource);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
 
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
        auth.jdbcAuthentication().dataSource(datasource);
 
        if(!userDetailsService.userExists("admin")) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
            User userDetails = new User("admin", encoder.encode("admin"), authorities);
 
            userDetailsService.createUser(userDetails);
        }
    	//
    	//
    	//cos nie dziala ;o
    	//
    	//
    	auth
            .inMemoryAuthentication()
                .withUser("admin").password("admin").roles("ADMIN");
    	
    }
    
}