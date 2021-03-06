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
	
	@RequestMapping(value = "/mechanics_list", method = RequestMethod.GET)
    public @ResponseBody List<String> listOfMechanics() throws SQLException {	
        return ob.listOfMechanics();
    }
    
	//Dodawanie uzytkownika do bazy calendar, po przyjeciu zgloszenia przez director
    @RequestMapping(value = "/user_create", method = RequestMethod.POST)
    public  String showHelloPage(
    		@RequestParam("first_name") String first_name,
    		@RequestParam("username") String username,
    		@RequestParam("last_name") String last_name,
    		@RequestParam("password") String password,
    		@RequestParam("authority") String authority) throws SQLException {
    	
    	if(username == "" || first_name=="" || last_name=="" || password=="")
    		return "redirect:/add_account?u=empty";
    	
    	if(authority == "") //to nie bedzie osiagalne dopoki jeste RequestParam... w sumie to nie ma sie co walic z taka walidacja bo uzytkownik tego nie moze wpisywac a jesli to zrobil to jest debilem... 
    		return "redirect:/add_account?u=authority";
    	
    	
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
    
    
    //nie wiem czy to ma byc w tym miejscu ale to tutaj zostawiam, inteligentnego prosze o przesuniecie i pouczenie gdzie to dawac ;o(tzw routing)
    @RequestMapping(value = "/director", method = RequestMethod.GET)
    public  String showDirectorPage(){
    	
    	String auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().replace("[", "").replace("]", "");
    	if (auth.equals("Księgowa"))
    		return "redirect:/accountant";
    	else if (auth.equals("Mechanik"))
    		return "redirect:/mechanic";
    	else if (auth.equals("Sekretarka"))
    		return "redirect:/secretary";
    	
        return null;
    }
    
    @RequestMapping(value = "/accountant", method = RequestMethod.GET)
    public  String showAccountantPage(){
    	
    	String auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().replace("[", "").replace("]", "");
    	if( auth.equals("Kierownik"))
    		return "redirect:/director";
    	else if (auth.equals("Mechanik"))
    		return "redirect:/mechanic";
    	else if (auth.equals("Sekretarka"))
    		return "redirect:/secretary";
    	
        return null;
    }

    @RequestMapping(value = "/mechanic", method = RequestMethod.GET)
    public  String showMechanicPage(){
    	
    	String auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().replace("[", "").replace("]", "");
    	if( auth.equals("Kierownik"))
    		return "redirect:/director";
    	else if (auth.equals("Księgowa"))
    		return "redirect:/accountant";
    	else if (auth.equals("Sekretarka"))
    		return "redirect:/secretary";
    	
        return null;
    }

    @RequestMapping(value = "/secretary", method = RequestMethod.GET)
    public  String showSecretaryPage(){
    	
    	String auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().replace("[", "").replace("]", "");
    	if( auth.equals("Kierownik"))
    		return "redirect:/director";
    	else if (auth.equals("Księgowa"))
    		return "redirect:/accountant";
    	else if (auth.equals("Mechanik"))
    		return "redirect:/mechanic";
    	
        return null;
    }


}
