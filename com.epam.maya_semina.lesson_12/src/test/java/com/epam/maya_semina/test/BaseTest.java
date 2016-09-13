package com.epam.maya_semina.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.uncommons.reportng.HTMLReporter;

import com.epam.maya_semina.mailService.MailboxFunctions;
import com.epam.maya_semina.ui.pageObject.MainPage;
import com.epam.maya_semina.ui.webDriver.DriverSingletone;
import com.epam.maya_semina.utils.ScreenShot;

@Listeners({ HTMLReporter.class }) // , ScreenShotListener.class})//,
// TestCaseListener.class }) //

public class BaseTest {

	private static final String FINISH_CLEAR = "finish: 'clear'";
	private static final String START_CLEAR = "start: 'clear'";
	
	private static final String FINISH_SET_UP = "finish: setUp";
	private static final String START_SET_UP = "start: setUp";
	private static final int TIMEOUT = 900;
	private static final String FALSE = "false";
	
	
	private WebDriver driver;
	private static final Logger LOG = Logger.getLogger(BaseTest.class);
	private String url;
	
	private MainPage inboxEmailPage;
	
	private static final String INCORRECT_TITLE = "Incorrect title: ";

	static {
		ScreenShot.deleteAll();
		System.setProperty("org.uncommons.reportng.escape-output", FALSE);
	}

	@Parameters({ "url" })
	@BeforeClass
	public void setUp(@Optional("http://Письмо.рф") String url) throws IOException {
		LOG.warn(START_SET_UP);
		this.url = url;
//		 driver = Driver.getWebDriverInstances("Chrome", DriverTypes.CHROME);
		driver = DriverSingletone.getWebDriverInstance();
		LOG.warn(FINISH_SET_UP);
	}

	

	@AfterGroups("authorization")
	public void clear() throws InterruptedException {
		LOG.info(START_CLEAR);
		Thread.sleep(TIMEOUT);
		inboxEmailPage.clickChoiseAllButton().clickDeleteButton().clickDraftsLink();
		Thread.sleep(TIMEOUT);
		inboxEmailPage = new MainPage(driver);
		inboxEmailPage.clickChoiseAllButton().clickDeleteButton().clickMainPageLink();
		Thread.sleep(TIMEOUT);
		inboxEmailPage.clickSentMessagesLink();
		inboxEmailPage = new MainPage(driver);
		Thread.sleep(TIMEOUT);
		inboxEmailPage.clickChoiseAllButton();
		inboxEmailPage.clickDeleteButton();
		inboxEmailPage.clickRcmlInboxLink();
		LOG.info(FINISH_CLEAR);
	}

	// 4 Выход из системы с помощью нажатия «выход»/«Выйти» На странице
	// появилось поле для ввода логина или пароля.

	@AfterClass (alwaysRun = true)//(dependsOnGroups = "verify_sent_letter", alwaysRun = true)
	public void exitTest() throws InterruptedException {
		LOG.info("start: 'exitTest'");
		inboxEmailPage.clickMainPageLink();
		new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getExitButton());
		Thread.sleep(TIMEOUT);
		inboxEmailPage.clickExitButton();
		Thread.sleep(TIMEOUT);
		System.out.println(driver.getTitle());
		boolean isThatPage = new MailboxFunctions(driver).equalsLogoutPageTitle(driver.getTitle());
		Assert.assertTrue(isThatPage, INCORRECT_TITLE);
		LOG.info("finish: 'exitTest'");
	}

	@AfterSuite (alwaysRun = true)
	public void close() {
		LOG.warn("start: close");
		driver.close();
		LOG.warn("finish: close");
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	public Logger getLogger() {
		return BaseTest.LOG;
	}

	public String getUrl() {
		return this.url;
	}

	public MainPage getInboxEmailPage() {
		return this.inboxEmailPage;
	}
}
