package TestNGClass;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class FirstLoginClass {
	
	
	@BeforeSuite
	public void StartDBConnection() {
		System.out.println("BS - StartDBConnection");	
	}
	@BeforeTest
	public void CreateUser() {
		System.out.println("BT - CreateUser");	
	}
	@BeforeClass
	public void LaunchBrowser() {
		System.out.println("BC - LaunchBrowser");	
	}
	@BeforeMethod
	public void LoginToApp() {
		System.out.println("BM - LoginToApp");	
	}
   
	@Test
	public void LoginPageTest() {
		System.out.println("LoginPageTest");
	}
	
	@Test
	public void PageTest() {
		System.out.println("PageTest");
	}
	
	@Test
	public void URLTest() {
		System.out.println("URLTest");
	}

	@AfterMethod
	public void StopDBConnection() {
		System.out.println("AM - StopDBConnection");	
	}
	@AfterClass
	public void DeleteUser() {
		System.out.println("AC - DeleteUser");	
	}
	@AfterTest
	public void CloseBrowser() {
		System.out.println("AT - CloseBrowser");	
	}
	@AfterSuite
	public void LogoutFromDBConnection() {
		System.out.println("AS - LogoutFromDBConnection");	
	}

}
