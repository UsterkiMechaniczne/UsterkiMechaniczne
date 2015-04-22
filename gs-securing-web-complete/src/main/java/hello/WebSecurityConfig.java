package hello;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	DatabaseMetaData dbm = datasource.getConnection().getMetaData();
    	ResultSet tables = dbm.getTables(null, null, "users", null);
    	if (!tables.next()) {
    	  System.out.println("Tworze tabele users");
    	 
    	  Statement stmt = null;
    	    String query = "create table users (    username varchar(50) not null primary key, password varchar(255) not null, enabled boolean not null); "
    	    		+ " create table authorities (  username varchar(50) not null,    authority varchar(50) not null,    foreign key (username) references users (username) ON DELETE CASCADE);";
    	       	    
    	     stmt = datasource.getConnection().createStatement();
    	     stmt.executeUpdate(query);
    	     stmt.close();
    	     System.out.println("Utworzylem tabele");
    	}
    	else
    	{	
    		System.out.println("Tabela users istnieje");
    	}
    	tables = dbm.getTables(null, null, "clients", null);
    	if (!tables.next()) {
      	  System.out.println("Tworze tabele clients oraz ??? xD");
      	 
      	  Statement stmt = null;
      	    String query = "create table clients (  first_name varchar(50) not null, last_name varchar(50) not null, number_plate varchar(15) not null primary key); ";
      	    String query2 = "create table tasks ( id int primary key, created_by varchar(50) not null references users(username) ON DELETE CASCADE, number_plate varchar(15) not null references clients(number_plate) ON DELETE CASCADE, description text not null);";
      	    stmt = datasource.getConnection().createStatement();
      	    stmt.executeUpdate(query);
      	    stmt.close();
      	    stmt = datasource.getConnection().createStatement();
    	    stmt.executeUpdate(query2);
    	    stmt.close();
      	    System.out.println("Utworzylem tabele clients i tasks");
      	}
      	else
      	{	
      		System.out.println("Tabela users istnieje");
      	}

    	JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager();
    
        userDetailsService.setDataSource(datasource);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
 
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
        auth.jdbcAuthentication().dataSource(datasource);
 
        if(!userDetailsService.userExists("admin")) {
            System.out.println("Tworze konto admin:admin");
        	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
            User userDetails = new User("admin", encoder.encode("admin"), authorities);
 
            userDetailsService.createUser(userDetails);
        }
    }
}