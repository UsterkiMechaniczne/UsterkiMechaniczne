package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import hello.Application;
import hello.CalendarController;

public class Tests {

	/*@Test
	public void test() {
		fail("Not yet implemented");
	}*/
	
	@Test
	public void Waliduje_logowanie_bazy()
	{
		//abstract
		DataSource ds = new DataSource();

		Assert.assertNotNull(ds);
	    //Assert.
	    //assertEquals(), assertNotEquals(), assertNull(), assertNotNull(), Has.None.EqualTo() 
	}
	
	@Test
	public void Waliduje_nazwe_bazy()
	{
		DataSource ds = new DataSource();
		ds.setDriverClassName("name");
        Assert.assertNotNull(ds.getName());
	}
	
	@Test
	public void Waliduje_url_bazy()
	{
		DataSource ds = new DataSource();
        ds.setUrl("datebaseurl");
        Assert.assertNotNull(ds.getUrl());
	}
	
	@Test
	public void Waliduje_nazwe_uzytkownika_bazy()
	{
		DataSource ds = new DataSource();
        ds.setUsername("user");
        Assert.assertNotNull(ds.getUsername());
	}
	
	@Test
	public void Waliduje_haslo_uzytkownika_bazy()
	{
		DataSource ds = new DataSource();
        ds.setPassword("password");
        Assert.assertNotNull(ds.getPassword());
	}
	
	@Test
	public void Sprawdza_pobieranie_kalendarza_uzytkownika() throws SQLException
	{
		CalendarController cal = new CalendarController();
		String username = "admin";
		Assert.assertNotNull(cal.showHelloPage(username));
	    //showHelloPage is not empty
	}

}
