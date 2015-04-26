package hello;

import java.sql.SQLException;

import model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hello.WebSecurityConfig.*;

@Controller
@RequestMapping(value="/")
public class UserController {

	@Autowired
	WebSecurityConfig ob;
	

    @RequestMapping(value = "/user_create", method = RequestMethod.POST)
    public  String showHelloPage(
    		@RequestParam("first_name") String first_name,
    		@RequestParam("username") String username,
    		@RequestParam("last_name") String last_name,
    		@RequestParam("password") String password,
    		@RequestParam("authority") String authority) throws SQLException {
    	
    	System.out.println("jestem i odebrałem " + first_name);
    	System.out.println("jestem i odebrałem " + last_name);
    	System.out.println("jestem i odebrałem " + username);
    	System.out.println("jestem i odebrałem " + password);
    	System.out.println("jestem i odebrałem " + authority);
    	
    	User userDetails = new User(first_name, last_name, username, new BCryptPasswordEncoder().encode(password), new SimpleGrantedAuthority(authority));
    	
    	
    	//WebSecurityConfig ob = new WebSecurityConfig();
    	ob.insertUserIntoDatabase(userDetails);
    	
        return "/main";
    }

}