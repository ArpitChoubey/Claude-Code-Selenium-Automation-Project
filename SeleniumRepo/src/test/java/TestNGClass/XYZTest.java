package TestNGClass;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class XYZTest {
	
	WebDriver driver;
	@BeforeMethod
	public void setup() {
		driver = DriverFactory.createDriver();
		driver.get("https://www.hotstar.com/in/home");
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		
	}
	@Test
    public void ATest() {
	driver.switchTo().newWindow(WindowType.WINDOW);
	driver.get("https://www.flipkart.com/");
	System.out.println("window:" +driver.getTitle());
		
	}

	@Test
    public void ACTest() {
	driver.switchTo().newWindow(WindowType.WINDOW);
	driver.get("https://www.google.co.in/");
	System.out.println("window:" +driver.getTitle());
		
	}
	@AfterMethod
	public void Teardown() {
		driver.quit();
		
	}

}
