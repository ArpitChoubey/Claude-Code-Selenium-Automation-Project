package SeleniumSess;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FooterSection {

	public static void main(String[] args) {
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
		List<WebElement> Seclist =
				driver.findElements(By.xpath("//h5[text()='Information']/following-sibling :: ul//a"));
		
		
		System.out.println(Seclist.size());
		
		for(WebElement e : Seclist ) {
        	String Text = e.getText();
        	System.out.println(Text);
		}
		
		
                 driver.quit();
	}

}
