package TestNGClass;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AmazonTest {
	
	WebDriver driver;
	@BeforeMethod
	public void setup() {
		driver = DriverFactory.createDriver();
		driver.get("https://www.hotstar.com/in/home");
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		
	}
      
	@Test(priority = 3)
	public void HomeTest() {
		driver.switchTo().newWindow(WindowType.WINDOW);
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		System.out.println("window:" +driver.getTitle());
		
	}
	@Test(priority = 1)
    public void ABCTest() {
	driver.switchTo().newWindow(WindowType.WINDOW);
	driver.get("https://www.amazon.in/");
	System.out.println("window:" +driver.getTitle());
		
	}
	@Test(priority = 2)
    public void NewTest() {
	driver.switchTo().newWindow(WindowType.WINDOW);
	driver.get("https://www.flipkart.com/");
	System.out.println("window:" +driver.getTitle());
	
}
	@AfterMethod
public void Teardown() {
	driver.quit();
	
}
	
	
	
}
