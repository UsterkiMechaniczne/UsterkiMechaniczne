package hello;


import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

import model.Client;
import model.Report;
import model.Task;
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
                .antMatchers("/css/**","/js/**","/fonts/**").permitAll()
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
           .antMatchers("/css/**","/js/**","/fonts/**"); //wylaczenie zabezpieczen dla css/js
    }
    
    @Autowired
    private DataSource datasource;

    
    public List<Client> listOfClients() throws SQLException{
    	List<Client> clients = new LinkedList<>();
    	String query = "select first_name, last_name, number_plate from clients";
      	
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			clients.add( new Client(rs.getString(1), rs.getString(2), rs.getString(3))  );
		
		}
		
		stmt.close();
		System.out.println(clients);
    	return clients;
    }
    
    public List<Task> listOfActiveTasksForMechanics(String mechanicUsername) throws SQLException{
    	List<Task> tasks = new LinkedList<>();
    	String query = "select id, number_plate, description, title, hours, date, parts_costs, repair_costs, is_done from tasks where mechanic = '"+mechanicUsername+"' and is_done = false";
      	
   
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			tasks.add(new Task(rs.getInt(1), rs.getString(2), mechanicUsername, rs.getString(3), 
					rs.getString(4), rs.getInt(5), rs.getString(6), rs.getDouble(7), rs.getDouble(8), false));		
		}
			
		stmt.close();
		System.out.println(tasks);
    	return tasks;
    }
    
    public List<Report> reportOfTransactions() throws SQLException{
    	List<Report> tasks = new LinkedList<>();
    	String query = "select id, tasks.number_plate, mechanic, description, title, hours, date, parts_costs, repair_costs, is_done, clients.first_name, clients.last_name from tasks join clients on tasks.number_plate = clients.number_plate";
      	
   
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			Task task = new Task(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), 
					rs.getString(5), rs.getInt(6), rs.getString(7), rs.getDouble(8), rs.getDouble(9), rs.getBoolean(10));	
			Client client =  new Client(rs.getString(11), rs.getString(12), rs.getString(2));
			Report report = new Report(client, task, rs.getDouble(8) +  rs.getDouble(9));
			tasks.add(report);
			
		}
			
		stmt.close();
		System.out.println(tasks);
    	return tasks;
    }
    
    
    
    public void updateTask(int taskid, double repairCost, double partsCosts, String description) throws SQLException{
        
    	String query = "update tasks set is_done=true, repair_costs = "+repairCost+", parts_costs = "+partsCosts+", description='"+description+"' where id="+taskid+"; " ;
    	System.out.println(query);
    	
    	
    	Statement stmt = datasource.getConnection().createStatement();
    	stmt = datasource.getConnection().createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    
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
    
    public List<String> listOfMechanics() throws SQLException{
    	List<String> mechanics = new LinkedList<>();
    	String query = "SELECT u.username FROM users u LEFT JOIN authorities a ON a.username = u.username WHERE a.authority = 'Mechanik';";
      	
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
			mechanics.add(rs.getString(1));
		stmt.close();
		System.out.println(mechanics);
    	return mechanics;
    }    
    
    public HashMap<String, Time> getUserWorkingHours(String username, String day) throws SQLException{
    	HashMap<String, Time> hours = new HashMap<String, Time>();
    	String query = "SELECT fromt, tot FROM calendar_hours WHERE username = '"+username+"' AND day='"+day+"';";
      	System.out.println(query);
    	
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()){
			hours.put("from", rs.getTime(1));
			hours.put("to", rs.getTime(2));
		}
		stmt.close();
		System.out.println(hours);
    	return hours;
    }  
    
    public ArrayList<Integer> listOfBusyHours(String username, String date) throws SQLException{
    	ArrayList<Integer> hours = new ArrayList<Integer>();
    	String query = "SELECT hours FROM tasks WHERE mechanic = '"+username+"' AND date = '"+date+"' ;";
      	
		Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
			hours.add(rs.getInt(1));
		stmt.close();
		System.out.println(hours);
		
    	return hours;
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
    
public boolean clientExists(String number_plate) throws SQLException{
    	
    	Statement stmt = null;
    	String query = "select * from clients where number_plate = '" + number_plate + "'";
	       	 
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
    	String query = "select day from calendar where work_day = true and username='"+username+"';";
    	
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
    		
    		query = "update calendar set work_day= "+ work_day+ " where username='"+ username +"' AND day='" + day +"';";
    	}
    	stmt.close();
    	
    	System.out.println(query);
    	stmt = datasource.getConnection().createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    public void insertCalendarHoursIntoDatabase(String from, String to, String day, String username) throws SQLException{
        
    	String query = "insert into calendar_hours values ('" + from + "','"+ to + "','"+ day + "','" + username+"'); " ;
    	
    	String fun = "select * from calendar_hours where username='"+ username +"' AND day='" + day +"';";
    	Statement stmt = datasource.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(fun);
    	
    	if(rs.next()){
    		
    		query = "update calendar_hours set fromt='"+ from +"', tot='" + to +"' where username='"+ username +"' AND day='" + day +"';";
    	}
    	stmt.close();
    	
    	System.out.println(query);
    	stmt = datasource.getConnection().createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    public void insertTaskToDb(String number_plate, String title, String description, String mechanic, String date, int hours) throws SQLException{
        
    	
    	String query = "insert into tasks (number_plate, title, description, date, hours, mechanic, is_done) values ('"+number_plate+"','"+title+"','"+description+"','"+date+"','"+hours+"','"+mechanic+"', false); " ;
    	Statement stmt = datasource.getConnection().createStatement();
    	
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
    

	public void insertClientIntoDatabase(String first_name, String last_name, String number_plate) throws SQLException {
		Statement stmt = null;
	    
		String query = "insert into clients values (' " + first_name + "', '"+ last_name + "' ,'" + number_plate+"'); " ;
	    
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
      	    String query2 = "create table tasks ( id SERIAL primary key, mechanic varchar(50) not null references users(username) ON DELETE CASCADE, number_plate varchar(15) not null references clients(number_plate) ON DELETE CASCADE, description text not null, title text not null, hours int not null, date date not null, parts_costs float, repair_costs float, is_done boolean );";
      	    String calendar = "create table calendar ( day date, work_day boolean, username varchar(50) not null REFERENCES users(username) );";
      	    String calendar_hours = "create table calendar_hours ( fromT Time not null, toT Time not  null, day date not null, username varchar(50) not null REFERENCES users(username) );";

      	    stmt = datasource.getConnection().createStatement();
      	    stmt.executeUpdate(query);
      	    stmt.close();
      	    stmt = datasource.getConnection().createStatement();
    	    stmt.executeUpdate(query2);
    	    stmt.close();
      	    stmt = datasource.getConnection().createStatement();
    	    stmt.executeUpdate(calendar);
    	    stmt.close();
    	    stmt = datasource.getConnection().createStatement();
    	    stmt.executeUpdate(calendar_hours);
    	    stmt.close();
    	    System.out.println("Utworzylem tabele clients, tasks, calendar, calendar_hours");
      	}
      	else
      	{	
      		System.out.println("Tabela clients, task, calendar, etc. istnieje");
      	}
    	
    	tables = dbm.getTables(null, null, "tasks", null);
    	
    	if (!tables.next()) {
      	  System.out.println("Tworze tabele clients, tasks, calendar");
      	 
      	  Statement stmt = null;
      	    String query = "create table tasks ( id serial primary key, mechanic varchar(50) not null references users(username) ON DELETE CASCADE, number_plate varchar(15) not null references clients(number_plate) ON DELETE CASCADE, description text not null, title text not null, hours int not null, date date not null);";

      	    stmt = datasource.getConnection().createStatement();
      	    stmt.executeUpdate(query);
      	    stmt.close();
    	    System.out.println("Utworzylem tabele tasks");
      	}
      	else
      	{	
      		System.out.println("Tabela task istnieje");
      	}

    	JdbcUserDetailsManager userDetailsService = new JdbcUserDetailsManager();
    
        userDetailsService.setDataSource(datasource);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
 
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
        auth.jdbcAuthentication().dataSource(datasource);
 
        if(!userDetailsService.userExists("admin")) {

            User userDetails = new User("Jan", "Kowalski", "admin", encoder.encode("admin"), new SimpleGrantedAuthority("Kierownik"));
            insertUserIntoDatabase(userDetails);
            
            insertUserIntoDatabase(new User("Janina", "Mistrzowska", "sekretarka", encoder.encode("12345Q"), new SimpleGrantedAuthority("Sekretarka")));
            insertUserIntoDatabase(new User("Paweł", "Pracowity", "mechanik", encoder.encode("12345Q"), new SimpleGrantedAuthority("Mechanik")));
            insertUserIntoDatabase(new User("Anna", "Pieniężna", "ksiegowa", encoder.encode("12345Q"), new SimpleGrantedAuthority("Ksiegowa")));
          
        }
    }

}