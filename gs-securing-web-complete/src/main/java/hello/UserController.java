package hello;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
	
	@RequestMapping(value = "/user_list", method = RequestMethod.GET)
    public @ResponseBody List<String> listOfUsers() throws SQLException {	
        return ob.listOfUsers();
    }
	
    
	//Dodawanie uzytkownika do bazy calendar, po przyjeciu zgloszenia przez director
    @RequestMapping(value = "/user_create", method = RequestMethod.POST)
    public  String showHelloPage(
    		@RequestParam("first_name") String first_name,
    		@RequestParam("username") String username,
    		@RequestParam("last_name") String last_name,
    		@RequestParam("password") String password,
    		@RequestParam("authority") String authority) throws SQLException {
    	
    	if(ob.userExists(username)){
    		return "redirect:/add_account?u=exist";
    	}
    	
    	User userDetails = new User(first_name, last_name, username, new BCryptPasswordEncoder().encode(password), new SimpleGrantedAuthority(authority));
    	
    	//WebSecurityConfig ob = new WebSecurityConfig();
    	ob.insertUserIntoDatabase(userDetails);
    	
        return "/director";
    }
    
    
    
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public  String showMainPage(){
    	
    	String auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().replace("[", "").replace("]", "");
    	if( auth.equals("Kierownik"))
    		return "redirect:/director";
    	else if (auth.equals("Księgowa"))
    		return "redirect:/accountant";
    	else if (auth.equals("Mechanik"))
    		return "redirect:/mechanic";
    	else if (auth.equals("Sekretarka"))
    		return "redirect:/secretary";
    	
        return "redirect:/not_found";
    }

}
