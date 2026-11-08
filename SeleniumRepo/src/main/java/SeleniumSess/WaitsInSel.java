package SeleniumSess;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


public class WaitsInSel {

	public static void main(String[] args) {


		WebDriver driver=  new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		
		// driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS));
		
		
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
		By emailID = By.id("input-email");
		
		
		
		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
	    wait.until(ExpectedConditions.presenceOfElementLocated(emailID));
	    System.out.println(emailID);
	    wait.until(ExpectedConditions.visibilityOfElementLocated(emailID));
	    System.out.println(emailID);
	    
	    wait.until(ExpectedConditions.titleContains("Account Login"));
	    System.out.println(driver.getTitle());
	    driver.navigate().to("https://naveenautomationlabs.com/opencart/index.php?route=account/forgotten");
	    
	    wait.until(ExpectedConditions.urlToBe("https://naveenautomationlabs.com/opencart/index.php?route=account/forgotten"));
	   // wait.until(ExpectedConditions.numberOfWindowsToBe(2));
	    wait.until(ExpectedConditions.titleContains("Forgot Your Password"));
	   // wait.until(ExpectedConditions.elementToBeClickable(emailID)).click();
	    System.out.println(driver.getTitle());
	    
	    
	    Wait<WebDriver> waitt = new FluentWait<WebDriver>(driver);
	    //  waitt.withTimeout(Duration.ofSeconds(5));
	 //   .pollingEvery(Duration.ofSeconds(1))
	    		
	   // 		WebDriverWait waity = new WebDriverWait(driver,Duration.ofSeconds(5), Duration.ofSeconds(1)); with polling time 
	    
	    
	    
	    
		
		
		
		
		
		
		
		
		

	}

}
