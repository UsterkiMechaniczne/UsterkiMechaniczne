package hello;

import java.sql.Date;
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
public class CalendarController {

	@Autowired
	WebSecurityConfig ob;
	
	//Dodawanie uzytkownika do bazy calendar, po przyjeciu zgloszenia przez director
    
	@RequestMapping(value = "/calendar_create", method = RequestMethod.POST)
    public @ResponseBody String showHelloPage(
    		@RequestParam("day") String day,
    		@RequestParam("work_day") boolean work_day,
    		@RequestParam("username") String username) throws SQLException {
  
    	//WebSecurityConfig ob = new WebSecurityConfig();
    	ob.insertCalendarIntoDatabase(day, work_day, username);
    	
        return "OK";
    }
	
	@RequestMapping(value = "/calendar_list", method = RequestMethod.GET)
    public @ResponseBody List<Date> showHelloPage(
    		@RequestParam("username") String username) throws SQLException {
		return ob.getCalendarOfUser(username);
    }
	
}
