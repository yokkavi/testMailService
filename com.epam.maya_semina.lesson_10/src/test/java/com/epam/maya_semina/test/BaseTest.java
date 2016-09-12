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
import org.testng.annotations.Test;
import org.uncommons.reportng.HTMLReporter;

import com.epam.maya_semina.mail.bo.Persona;
import com.epam.maya_semina.mail.builder.BaseBuilder;
import com.epam.maya_semina.mail.builder.UserBuilder;
import com.epam.maya_semina.mailService.MailboxFunctions;
import com.epam.maya_semina.ui.pageObject.MailboxAuthorizationPage;
import com.epam.maya_semina.ui.pageObject.MainPage;
import com.epam.maya_semina.ui.webDriver.DriverSingletone;
import com.epam.maya_semina.utils.ScreenShot;

@Listeners({ HTMLReporter.class }) // , ScreenShotListener.class})//,
// TestCaseListener.class }) //

public class BaseTest {

	private static final String FINISH_CLEAR = "finish: 'clear'";
	private static final String START_CLEAR = "start: 'clear'";
	private static final String FINISH_NAVIGATE_TO_MAIL_PAGE_TEST = "finish: 'navigateToMailPageTest'";
	private static final String START_NAVIGATE_TO_MAIL_PAGE_TEST = "start: 'navigateToMailPageTest'";
	private static final String FINISH_SET_UP = "finish: setUp";
	private static final String START_SET_UP = "start: setUp";
	private static final int TIMEOUT = 900;
	private static final String FALSE = "false";
	
	private Persona user;
	private WebDriver driver;
	private static final Logger LOG = Logger.getLogger(BaseTest.class);
	private String url;
	private MailboxAuthorizationPage mailboxAuthorizationPage;
	private MainPage inboxEmailPage;
	private static final String ELEMENT_BUTTON_CREATE_NEW_LETTER_IS_NOT_PRESENT = "Element button create new letter is not present: ";
	private static final String INCORRECT_TITLE = "Incorrect title: ";

	static {
		ScreenShot.deleteAll();
		System.setProperty("org.uncommons.reportng.escape-output", FALSE);
	}

	@Parameters({ "url" })
	@BeforeClass
	public void setUp(@Optional("http://������.��") String url) throws IOException {
		LOG.warn(START_SET_UP);
		this.url = url;
//		 driver = Driver.getWebDriverInstances("Chrome", DriverTypes.CHROME);
		driver = DriverSingletone.getWebDriverInstance();
		LOG.warn(FINISH_SET_UP);
	}

	@Test(groups = "authorization")
	public void navigateToMailPageTest() {
		LOG.info(START_NAVIGATE_TO_MAIL_PAGE_TEST);
		BaseBuilder builder = new UserBuilder();
		builder.buildPassword();
		builder.buildLogin();
		user = builder.getPersona();
		driver.get(url);
		mailboxAuthorizationPage = new MailboxAuthorizationPage(driver);
		boolean isThatPage = new MailboxFunctions(driver).equalsAuthorisatiomPageTitle(driver.getTitle());
		if (isThatPage) {
			new MailboxFunctions(driver).performAuthorization(mailboxAuthorizationPage, user);
			inboxEmailPage = new MainPage(driver);
			isThatPage = new MailboxFunctions(driver).isPresent(inboxEmailPage.getButtonCreateNewLetter());
		}
		LOG.info(FINISH_NAVIGATE_TO_MAIL_PAGE_TEST);
		Assert.assertTrue(isThatPage, ELEMENT_BUTTON_CREATE_NEW_LETTER_IS_NOT_PRESENT);
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

	// 4 ����� �� ������� � ������� ������� ������/������ �� ��������
	// ��������� ���� ��� ����� ������ ��� ������.

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
