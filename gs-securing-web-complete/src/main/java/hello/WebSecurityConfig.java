package hello;


import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.User;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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
            .csrf().disable() //wyłączenie ochrony przed atakami CSFR
            .authorizeRequests()
                .antMatchers("/css/**","/js/**").permitAll()
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

    @Override
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .antMatchers("/css/**","/js/**"); //wylaczenie zabezpieczen dla css/js
    }
    
    @Autowired
    private DataSource datasource;

    
    
    public List<String> listOfUsers() throws SQLException{
    	List<String> users = new LinkedList<>();
    	String query = "select username from users";
      	
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
			users.add(rs.getString(1));
		stmt.close();
		System.out.println(users);
    	return users;
    }
    
    
    public boolean userExists(String username) throws SQLException{
    	
    	Statement stmt = null;
    	String query = "select * from users where username = '" + username + "'";
	       	 
    	boolean exists = false;
		stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		if(rs.next())
			exists = true;
		stmt.close();
		return exists;
    }
    
    //pobiera liste dni wolnych
    public List<Date> getCalendarOfUser(String username) throws SQLException{
    	List<Date> calendar = new LinkedList<>();
    	String query = "select day from calendar where work_day = false and username='"+username+"';";
    	
    	System.out.println(query);
    	Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
			calendar.add(rs.getDate(1));
        stmt.close();
    	return calendar;
    }
    
    public void insertCalendarIntoDatabase(String day, boolean work_day,  String username) throws SQLException{
    
    	String query = "insert into calendar values (' " + day + "',"+ work_day + ",'" + username+"'); " ;
    	
    	String fun = "select day,work_day from calendar where username='"+ username +"' AND day='" + day +"';";
    	Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(fun);
    	
    	if(rs.next()){
    		
    		query = "update calendar set work_day= "+(!rs.getBoolean(2))+ " where username='"+ username +"' AND day='" + day +"';";
    	}
    	stmt.close();
    	
    	System.out.println(query);
    	stmt = datasource.getConnection().createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    
    
    public void insertUserIntoDatabase(User user) throws SQLException{
    	
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
      	  System.out.println("Tworze tabele clients, tasks, calendar");
      	 
      	  Statement stmt = null;
      	    String query = "create table clients (  first_name varchar(50) not null, last_name varchar(50) not null, number_plate varchar(15) not null primary key); ";
      	    String query2 = "create table tasks ( id int primary key, created_by varchar(50) not null references users(username) ON DELETE CASCADE, number_plate varchar(15) not null references clients(number_plate) ON DELETE CASCADE, description text not null);";
      	    String calendar = "create table calendar ( day date, work_day boolean, username varchar(50) not null REFERENCES users(username) );";

      	    stmt = datasource.getConnection().createStatement();
      	    stmt.executeUpdate(query);
      	    stmt.close();
      	    stmt = datasource.getConnection().createStatement();
    	    stmt.executeUpdate(query2);
    	    stmt.close();
      	    stmt = datasource.getConnection().createStatement();
    	    stmt.executeUpdate(calendar);
    	    stmt.close();
    	    System.out.println("Utworzylem tabele clients, tasks, calendar");
      	}
      	else
      	{	
      		System.out.println("Tabela clients, task, calendar istnieje");
      	}

    	JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager();
    
        userDetailsService.setDataSource(datasource);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
 
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
        auth.jdbcAuthentication().dataSource(datasource);
 
        if(!userDetailsService.userExists("admin")) {

            User userDetails = new User("Jan", "Kowalski", "admin", encoder.encode("admin"), new SimpleGrantedAuthority("Kierownik"));
            insertUserIntoDatabase(userDetails);
            
          
        }
    }
}