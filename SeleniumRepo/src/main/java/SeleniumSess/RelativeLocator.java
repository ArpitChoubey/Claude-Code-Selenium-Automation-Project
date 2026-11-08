package SeleniumSess;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

public class RelativeLocator {

	public static void main(String[] args) throws InterruptedException {
		
		WebDriver driver=  new FirefoxDriver();
		driver.get("https://www.aqi.in/dashboard/canada");
		
		Thread.sleep(4000);
		
		WebElement Ele = driver.findElement(By.linkText("Cold Lake, Canada"));
		
		String rightIndex = driver.findElement(with(By.tagName("p")).toRightOf(Ele)).getText();
		System.out.println(rightIndex);
		
		String leftRank = driver.findElement(with(By.tagName("p")).toLeftOf(Ele)).getText();
		System.out.println(leftRank);

		
		String aboveCity = driver.findElement(with(By.tagName("p")).above(Ele)).getText();
		System.out.println(aboveCity);
		
		
		String belowCity = driver.findElement(with(By.tagName("p")).below(Ele)).getText();
		System.out.println(belowCity);
		
		
		String nearCity = driver.findElement(with(By.tagName("a")).near(Ele)).getText();
		System.out.println(nearCity);
		
		

	}

}
