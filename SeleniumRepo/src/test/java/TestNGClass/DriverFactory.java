package TestNGClass;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates the WebDriver used by the TestNG tests.
 *
 * Configured with system properties (-Dbrowser=chrome) or environment variables (BROWSER=chrome):
 *   browser / BROWSER                         : firefox (default), chrome, edge
 *   headless / HEADLESS                       : true or false (default false)
 *   selenium.remote.url / SELENIUM_REMOTE_URL : Selenium Grid URL, e.g. http://selenium-grid:4444.
 *                                               When set, a RemoteWebDriver is used instead of a local browser.
 *
 * Defaults keep the original local behaviour (visible Firefox). The Docker image and
 * Kubernetes Job set BROWSER=chrome and HEADLESS=true.
 */
public final class DriverFactory {

	private DriverFactory() {
	}

	public static WebDriver createDriver() {
		String browser = setting("browser", "BROWSER", "firefox").toLowerCase().trim();
		boolean headless = Boolean.parseBoolean(setting("headless", "HEADLESS", "false"));
		String remoteUrl = setting("selenium.remote.url", "SELENIUM_REMOTE_URL", "");

		Capabilities options = buildOptions(browser, headless);

		if (!remoteUrl.isEmpty()) {
			try {
				return new RemoteWebDriver(new URL(remoteUrl), options);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Invalid SELENIUM_REMOTE_URL: " + remoteUrl, e);
			}
		}

		switch (browser) {
		case "chrome":
			return new ChromeDriver((ChromeOptions) options);
		case "edge":
			return new EdgeDriver((EdgeOptions) options);
		default:
			return new FirefoxDriver((FirefoxOptions) options);
		}
	}

	private static Capabilities buildOptions(String browser, boolean headless) {
		switch (browser) {
		case "chrome": {
			ChromeOptions options = new ChromeOptions();
			if (headless) {
				options.addArguments("--headless=new");
			}
			// Needed when Chrome runs as root inside a container
			options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
			return options;
		}
		case "edge": {
			EdgeOptions options = new EdgeOptions();
			if (headless) {
				options.addArguments("--headless=new");
			}
			return options;
		}
		case "firefox": {
			FirefoxOptions options = new FirefoxOptions();
			if (headless) {
				options.addArguments("-headless");
			}
			return options;
		}
		default:
			throw new IllegalArgumentException("Unsupported browser: " + browser);
		}
	}

	private static String setting(String sysProp, String envVar, String defaultValue) {
		String value = System.getProperty(sysProp);
		if (value == null || value.isEmpty()) {
			value = System.getenv(envVar);
		}
		return (value == null || value.isEmpty()) ? defaultValue : value;
	}
}
