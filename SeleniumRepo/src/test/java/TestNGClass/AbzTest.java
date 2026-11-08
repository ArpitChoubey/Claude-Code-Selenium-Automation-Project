package TestNGClass;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AbzTest {
	
	WebDriver driver;
	@BeforeMethod
	public void setup() {
		driver = DriverFactory.createDriver();
		driver.get("https://www.hotstar.com/in/home");
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		
	}
	@Test
    public void ABCTest() {
	driver.switchTo().newWindow(WindowType.WINDOW);
	driver.get("https://www.amazon.in/");
	System.out.println("window:" +driver.getTitle());
		
	}

	@Test
    public void ACTest() {
	driver.switchTo().newWindow(WindowType.WINDOW);
	driver.get("https://www.youtube.com/");
	System.out.println("window:" +driver.getTitle());
		
	}
	@AfterMethod
	public void Teardown() {
		driver.quit();
		
	}
	
}
