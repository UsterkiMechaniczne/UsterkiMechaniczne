package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    	registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/main").setViewName("main");
        registry.addViewController("/add_account").setViewName("add_account");
        registry.addViewController("/secretary").setViewName("secretary");
        registry.addViewController("/mechanic").setViewName("mechanic");
        registry.addViewController("/accountant").setViewName("accountant");
        registry.addViewController("/director").setViewName("director");
        registry.addViewController("/calendar").setViewName("calendar");
    }
}
