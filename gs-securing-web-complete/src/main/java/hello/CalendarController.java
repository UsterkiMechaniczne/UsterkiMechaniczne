package hello;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;

import model.Client;
import model.Report;
import model.Task;
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
	
	@RequestMapping(value = "/calendar_hours_create", method = RequestMethod.POST)
    public @ResponseBody String showHelloPage(
    		@RequestParam("from") String from,
    		@RequestParam("to") String to,
    		@RequestParam("day") String day,
    		@RequestParam("username") String username) throws SQLException {
  
    	//WebSecurityConfig ob = new WebSecurityConfig();
    	ob.insertCalendarHoursIntoDatabase(from, to, day, username);
    	
        return "OK";
    }	
	
	@RequestMapping(value = "/task_create", method = RequestMethod.POST)
    public String taskCreate(
    		@RequestParam("client") String client,
    		@RequestParam("title") String title,
    		@RequestParam("description") String description,
    		@RequestParam("mechanic") String mechanic,
    		@RequestParam("date") String date,
    		@RequestParam("hours") int hours
    		) throws SQLException {
  
    	//WebSecurityConfig ob = new WebSecurityConfig();
    	ob.insertTaskToDb(client, title, description, mechanic, date, hours);
    	
        return "/secretary";
    }	
	
	@RequestMapping(value = "/task_update", method = RequestMethod.POST)
    public String taskCreate(
    		@RequestParam("taskid") Integer taskid,
    		@RequestParam("repairs_costs") Double repairCost,
    		@RequestParam("parts_costs") Double partsCosts,
    		@RequestParam("description") String description
    		) throws SQLException {
  
    	ob.updateTask(taskid, repairCost, partsCosts, description);
    	
        return "/mechanic";
    }	
	
	
	@RequestMapping(value = "/tasks_list", method = RequestMethod.GET)
    public @ResponseBody List<Task> listOfTasks(@RequestParam("mechanic") String mechanic) throws SQLException {	
        return ob.listOfActiveTasksForMechanics(mechanic);
    }
    
	@RequestMapping(value = "/report_full", method = RequestMethod.GET)
    public @ResponseBody List<Report> listOfTasks() throws SQLException {	
        return ob.reportOfTransactions();
    }
	
	
	@RequestMapping(value = "/calendar_list", method = RequestMethod.GET)
    public @ResponseBody List<Date> showHelloPage(
    		@RequestParam("username") String username) throws SQLException {
		return ob.getCalendarOfUser(username);
    }
	
	@RequestMapping(value = "/calendar_working_hours", method = RequestMethod.GET)
    public @ResponseBody HashMap<String, Time> getUserWorkingHours(
    		@RequestParam("username") String username,
    		@RequestParam("day") String day
    		) throws SQLException {   	
		
        return ob.getUserWorkingHours(username, day);
    }	
	
	@RequestMapping(value = "/mechanic_hours_left", method = RequestMethod.GET)
    public @ResponseBody int getMechanicHoursLeft(
    		@RequestParam("username") String username,
    		@RequestParam("date") String day
    		) throws SQLException {   	
		
		HashMap<String, Time> dates = ob.getUserWorkingHours(username, day);
		
		System.out.println("========================================");
		int from = dates.get("from").getHours();
		int to = dates.get("to").getHours();
		int left = to - from;
		//System.out.println(left);
		
		ArrayList<Integer> busy = ob.listOfBusyHours(username, day);
		
		for(int i=0; i<busy.size(); i++){
			left -= busy.get(i);
		}
		System.out.println(left);
		if(left < 0)
			left = 0;
        return left;
    }
}
