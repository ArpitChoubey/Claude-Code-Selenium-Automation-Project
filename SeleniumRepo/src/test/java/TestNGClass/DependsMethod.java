package TestNGClass;

import org.testng.annotations.Test;

public class DependsMethod {
	
	@Test
	public void setup() {
		System.out.println("Login Test");
	}
	
	@Test(dependsOnMethods = "setup")
	public void Homeup() {
		System.out.println("Login Page Test");
		
	}

}
