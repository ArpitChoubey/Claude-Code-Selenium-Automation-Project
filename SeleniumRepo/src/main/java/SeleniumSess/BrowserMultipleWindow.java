package SeleniumSess;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class BrowserMultipleWindow {

	public static void main(String[] args) throws InterruptedException {
		WebDriver driver=  new FirefoxDriver();
		driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
		
		Thread.sleep(4000);
		
		WebElement twEle = driver.findElement(By.xpath("//a[contains(@href,'twitter')]"));
		WebElement FBEle = driver.findElement(By.xpath("//a[contains(@href,'twitter')]"));
		WebElement YoEle = driver.findElement(By.xpath("//a[contains(@href,'twitter')]"));
		WebElement LiEle = driver.findElement(By.xpath("//a[contains(@href,'twitter')]"));
		
		
		
		twEle.click();
		FBEle.click();
		YoEle.click();
		LiEle.click();
		
         Set<String> handles = driver.getWindowHandles();
		
		Iterator<String> it =handles.iterator();
		
		while(it.hasNext()) {
			String window =it.next();
			driver.switchTo().window(window);
			System.out.println("driver.getCurrentUrl()");
			Thread.sleep(4000);
			driver.close();
			
		}

	}

}
