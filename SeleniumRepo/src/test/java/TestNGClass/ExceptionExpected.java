package TestNGClass;

import org.testng.annotations.Test;

public class ExceptionExpected {

	
	
	
	

	@Test(expectedExceptions=ArithmeticException.class)
	public void loginText() {
		System.out.println("login Test");
		int i=9/0;
	}
	
	@Test(invocationCount=10)
	public void logText() {
		int i=9;
		System.out.println(i);
	}
	
	
}
