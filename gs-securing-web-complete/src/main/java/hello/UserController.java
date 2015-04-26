package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping(value="/")
public class UserController {

    @RequestMapping(value = "/create_user", method = RequestMethod.GET)
    public  String showHelloPage(Model model) {

        return "/main";
    }

}