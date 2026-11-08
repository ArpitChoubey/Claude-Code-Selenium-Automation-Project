package SeleniumSess;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ElementAttributeConcept {

	public static void main(String[] args) {
		
		
		WebDriver driver=  new ChromeDriver();
		driver.get("https://naveenautomationlabs.com/opencart/index.php?route=account/login");
		
		String ph = driver.findElement(By.id("input-email")).getAttribute("placeholder");
		System.out.println(ph);
		
		String f= driver.findElement(By.name("search")).getAttribute("placeholder");
		System.out.println(f);
		
		ElementUtil eleUtil = new ElementUtil(driver);
		By links = By.tagName("a");

		List<WebElement> linksList = eleUtil.getElements(links);
	System.out.println("total links ==>" + linksList.size());
		for(WebElement e : linksList) {
			String hrefVal = e.getAttribute("href");		
			String linkText = e.getText();
				System.out.println(hrefVal + " --->  "+ linkText);
		}
		
		
//		List<WebElement> imagesList = getElements(images);
//		System.out.println("total images: " + imagesList.size());
//		
//		for(WebElement e : imagesList) {
//			String srcVal = e.getAttribute("src");
//			System.out.println(srcVal);
//		}
		
	//	getElementAttributes(images, "src");
	//	getElementAttributes(links, "href");

		driver.quit();
	}

}
