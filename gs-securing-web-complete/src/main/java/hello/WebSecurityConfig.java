package hello;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.User;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    
    private void insertUserIntoDatabase(User user) throws SQLException{
    	
  	  Statement stmt = null;
	    String query = "insert into users (first_name, last_name, username, password, enabled) values ('"+user.getFirstName()+"','"+user.getLastName()+"','"+user.getUsername()+"','"+user.getPassword()+"','"+user.isEnabled()+"'); "
	    		+ " insert into authorities (username, authority) values ('"+user.getUsername()+"','"+user.getAuthority()+"')";
	       	    
	    
	 System.out.println(query);
     stmt = datasource.getConnection().createStatement();
     stmt.executeUpdate(query);
     stmt.close();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	DatabaseMetaData dbm = datasource.getConnection().getMetaData();
    	ResultSet tables = dbm.getTables(null, null, "users", null);
    	if (!tables.next()) {
    	  System.out.println("Tworze tabele users");
    	 
    	  Statement stmt = null;
    	    String query = "create table users ( first_name varchar(50) not null, last_name varchar(50) not null, username varchar(50) not null primary key, password varchar(255) not null, enabled boolean not null); "
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
      	  System.out.println("Tworze tabele clients oraz task");
      	 
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
      		System.out.println("Tabela clients i task istnieje");
      	}

    	JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager();
    
        userDetailsService.setDataSource(datasource);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
 
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
        auth.jdbcAuthentication().dataSource(datasource);
 
        if(!userDetailsService.userExists("admin")) {
            //System.out.println("Tworze konto admin:admin");

            User userDetails = new User("jan", "kowalski", "admin", encoder.encode("admin"), new SimpleGrantedAuthority("MISZTSZ"));
            insertUserIntoDatabase(userDetails);
            
            //insertUserIntoDatabase(new User("paweł", "beznazwiskowy", "klient", encoder.encode("klient"), new SimpleGrantedAuthority("BRAK")));
        }
    }
}