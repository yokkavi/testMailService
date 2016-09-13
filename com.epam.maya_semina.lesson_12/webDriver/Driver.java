package com.epam.maya_semina.ui.webDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.epam.maya_semina.exceptions.UnknownDriverTypeException;

public class Driver {

	private static final String UNKNOWN_DRIVER_TYPE_SPECIFIED = "Unknown driver type specified: ";
	private static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
	private static final String DRIVER_PATH = "\\src\\test\\resources\\chromedriver.exe";
	private static final String DEFAULT_WEB_DRIVER = "DEFAULT_WEB_DRIVER";
	private static DriverTypes defaultDriverType = DriverTypes.FIREFOX;

	private static HashMap<String, WebDriver> instances;

	static {
		instances = new HashMap<String, WebDriver>();
	}

	public static WebDriver getWebDriverInstances(String name, DriverTypes driverType) throws IOException {

		WebDriver driver;

		if (!instances.containsKey(name)) {
			switch (driverType) {
			case FIREFOX:
				driver = new FirefoxDriver();
				break;
			case IE:
				driver = new InternetExplorerDriver();
				break;
			case CHROME:
				String DriverCanonicalPath = new java.io.File( "." ).getCanonicalPath()+DRIVER_PATH;
				System.setProperty(WEBDRIVER_CHROME_DRIVER, DriverCanonicalPath);
				driver = new ChromeDriver();
				break;
			default: 
				throw new UnknownDriverTypeException(UNKNOWN_DRIVER_TYPE_SPECIFIED + driverType.getDriverName());
			}
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			instances.put(name, driver);
		}
		else driver = instances.get(name);
		
		return driver;
	}
	
	public static WebDriver getWebDriverInstances(String name) throws IOException {
		return getWebDriverInstances(name, defaultDriverType);
	}
	
	public static WebDriver getWebDriverInstances() throws IOException {
		return getWebDriverInstances(DEFAULT_WEB_DRIVER, defaultDriverType);
	}
}
