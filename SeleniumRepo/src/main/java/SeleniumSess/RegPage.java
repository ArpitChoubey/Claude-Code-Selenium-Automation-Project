package SeleniumSess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegPage {

	public static void main(String[] args) {
		
		
		String browserName = "Chrome";
		
		SeleniumUtil obk = new SeleniumUtil();
		WebDriver driver = obk.initDriver(browserName);
		
		obk.LaunchURL("https://naveenautomationlabs.com/opencart/index.php?route=account/register");
		
		By FirstName = By.id("input-firstname");
		By lastName = By.id("input-lastname");
		By email = By.id("input-email");
		By telephone = By.id("input-telephone");
		By Password = By.id("input-password");
		By ConfirmPassword = By.id("input-confirm");
		
		ElementUtil jk = new ElementUtil(driver);
		jk.doSendKeys(FirstName,"Amannn");
		jk.doSendKeys(lastName,"Choubey");
		jk.doSendKeys(email,"choubeyarp963@gmail.com" );
		jk.doSendKeys(telephone,"9110462871" );
		jk.doSendKeys(Password,"Arpt@123");
		jk.doSendKeys(ConfirmPassword ,"Arpt@123");
		// driver.findElement(By.name("agree")).click();
		// driver.findElement(By.xpath("//*[@id=\"content\"]/form/div/div/input[2]")).click();
		
		
		// driver.findElement(By.linkText("My Account")).click();
		// driver.findElement(By.xpath("//*[@id=\"input-email\"]")).sendKeys("choubeyarpit963@gmail.com");
		// driver.findElement(By.cssSelector("#input-password")).sendKeys("Arpit@123");
		
		
		// driver.findElement(By.className("\"form-control\"")).sendKeys("choubeyarpit963@gmail.com");
		// driver.findElement(By.name("password")).sendKeys("Arpit@123");
		// driver.findElement(By.linkText("Forgotten")).click();
		
		// String Head = driver.findElement(By.tagName("h2")).getText();
		// System.out.println(Head);
		
		boolean fg = driver.findElement(By.className("img-responsive")).isDisplayed();
		System.out.println(fg);
		
		
		
		
		
		
		// driver.quit();
		
		
		

	}

}
