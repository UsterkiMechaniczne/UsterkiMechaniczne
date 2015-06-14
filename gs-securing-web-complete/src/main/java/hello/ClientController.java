package hello;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import model.Client;
import model.Log;
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
public class ClientController {

	@Autowired
	WebSecurityConfig ob;
	
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
    public @ResponseBody List<Log> logs() throws SQLException {	
        return ob.getAllLogs();
    }

	@RequestMapping(value = "/client_list", method = RequestMethod.GET)
    public @ResponseBody List<Client> listOfClients() throws SQLException {	
        return ob.listOfClients();
    }
    
	//Dodawanie klienta do bazy client, po przyjeciu zgloszenia przez secretary
    @RequestMapping(value = "/client_create", method = RequestMethod.POST)
    public  String showHelloPage(
    		@RequestParam("first_name") String first_name,
    		@RequestParam("last_name") String last_name,
    		@RequestParam("number_plate") String number_plate, Model model) throws SQLException {
    			  
    	if(first_name == "" || last_name == "" || number_plate == "")
    		return "redirect:/add_client?u=empty";
    	
    
    	if(ob.clientExists(number_plate)){
    		return "redirect:/add_client?u=exist";
    	}
    	
    	    	ob.insertClientIntoDatabase(first_name, last_name, number_plate);

    	        return "/secretary";
    	}
}
