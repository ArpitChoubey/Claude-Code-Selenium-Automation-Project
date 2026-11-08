package SeleniumSess;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.firefox.FirefoxDriver;

public class BrowserWindowHandle {

	public static void main(String[] args) throws InterruptedException {


		WebDriver driver=  new FirefoxDriver();
		driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
		
		Thread.sleep(4000);
		
		WebElement twEle = driver.findElement(By.xpath("//a[contains(@href,'twitter')]"));
		
		//driver.switchTo().newWindow(WindowType.TAB);
		
		//driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
		//System.out.println("window:" +driver.getTitle());
		
		
		//driver.switchTo().newWindow(WindowType.WINDOW);
		
		//driver.get("https://www.jiocinema.com/");
		
		//System.out.println("window:" +driver.getTitle());
		
		//driver.quit();
		
	
		
		twEle.click();
		
		Set<String> handles = driver.getWindowHandles();
		
		Iterator<String> it =handles.iterator();
		
		String parentwindow =it.next();
		System.out.println("parentwindow:" +parentwindow);
		
		String childwindow =it.next();
		System.out.println("childwindow:" +childwindow);
		
		String childwindow1 =it.next();
		System.out.println("childwindow:" +driver.getCurrentUrl());
		
		
		driver.close();
		
		driver.switchTo().window(parentwindow);
		System.out.println("parentwindow:" +driver.getCurrentUrl());
		
		driver.quit();
		
	}	

	}

