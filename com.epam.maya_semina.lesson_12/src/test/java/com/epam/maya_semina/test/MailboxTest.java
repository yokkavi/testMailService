package com.epam.maya_semina.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.epam.maya_semina.mail.bo.Persona;
import com.epam.maya_semina.mail.bo.letter.Letter;
import com.epam.maya_semina.mail.builder.BaseBuilder;
import com.epam.maya_semina.mail.builder.UserBuilder;
import com.epam.maya_semina.mailService.MailboxFunctions;
import com.epam.maya_semina.ui.pageObject.DraftsPage;
import com.epam.maya_semina.ui.pageObject.MailboxAuthorizationPage;
import com.epam.maya_semina.ui.pageObject.MainPage;
import com.epam.maya_semina.ui.pageObject.MessagePage;
import com.epam.maya_semina.ui.pageObject.NewLetterPage;
import com.epam.maya_semina.ui.pageObject.SentMessagePage;
import com.epam.maya_semina.utils.LetterDataProvider;

//@Listeners({ HTMLReporter.class }) // , ScreenShotListener.class})//,
//									// TestCaseListener.class }) //

public class MailboxTest extends BaseTest {

	private static final int TIMEOUT = 900;
	
	private static final String START_CLEAR_TEST_DATA = "start: 'clearTestData'";
	private static final String FINISH_CLEAR_TEST_DATA = "finish: 'clearTestData'";
	private static final String FINISH_VERYFY_SENT_LETTER_TEST = "finish: 'veryfySentLetterTest'";
	private static final String START_VERYFY_SENT_LETTER_TEST = "start: 'veryfySentLetterTest'";
	private static final String FINISH_SEND_DRAFT_TEST = "finish: 'sendDraftTest'";
	private static final String START_SEND_DRAFT_TEST = "start: 'sendDraftTest'";
	private static final String FINISH_WRITE_LETTER_TEST = "finish: 'writeLetterTest'";
	private static final String START_WRITE_LETTER_TEST = "start: 'writeLetterTest'";
	private static final String FINISH_NAVIGATE_TO_MAIL_PAGE_TEST = "finish: 'navigateToMailPageTest'";
	private static final String START_NAVIGATE_TO_MAIL_PAGE_TEST = "start: 'navigateToMailPageTest'";
	
	private static final String LETTER_IS_NOT_SAVED = "Letter is not saved";
	private static final String UNCORRECTED_LETTER = "Uncorrected letter";
	private static final String ELEMENT_BUTTON_CREATE_NEW_LETTER_IS_NOT_PRESENT = "Element button create new letter is not present: ";
	private static final String INCORRECT_TITLE = "Incorrect title: ";
	private static final String ELEMENT_BUTTON_SAVE_IS_NOT_PRESENT = "Element buttonSave is not present: ";
	private static final String ELEMENT_SAVE_MESSAGE_IS_NOT_PRESENT = "Element saveMessage is not present: ";
	
	private MailboxAuthorizationPage mailboxAuthorizationPage;
	private Persona user;
	private MainPage inboxEmailPage;
	private NewLetterPage newLetterPage;
	private DraftsPage draftsPage;
	private MessagePage sentMessagePage;
	private WebDriver driver;
	private Letter letter;

	// 1 Авторизоваться в системе пользователем Отображается на экране почта
	// пользователя

	@BeforeClass
	public void mailBoxTestSetUp() {
		this.driver = super.getDriver();
		this.inboxEmailPage = new MainPage(driver);
	}
	
	@Test(groups = "authorization")
	public void navigateToMailPageTest() {
		super.getLogger().info(START_NAVIGATE_TO_MAIL_PAGE_TEST);
		BaseBuilder builder = new UserBuilder();
		builder.buildPassword();
		builder.buildLogin();
		user = builder.getPersona();
		driver.get(super.getUrl());
		mailboxAuthorizationPage = new MailboxAuthorizationPage(driver);
		boolean isThatPage = new MailboxFunctions(driver).equalsAuthorisatiomPageTitle(driver.getTitle());
		if (isThatPage) {
			new MailboxFunctions(driver).performAuthorization(mailboxAuthorizationPage, user);
			inboxEmailPage = new MainPage(driver);
			isThatPage = new MailboxFunctions(driver).equalsAuthorisatiomPageTitle(driver.getTitle());
		}
		super.getLogger().info(FINISH_NAVIGATE_TO_MAIL_PAGE_TEST);
		Assert.assertTrue(isThatPage, ELEMENT_BUTTON_CREATE_NEW_LETTER_IS_NOT_PRESENT);
	}

	@Test(groups = "write_a_letter", dependsOnGroups = "authorization", dataProvider = "getData", dataProviderClass = LetterDataProvider.class)
	public void writeLetterTest(String to, String theme, String body) throws InterruptedException {

		super.getLogger().info(START_WRITE_LETTER_TEST);
		Thread.sleep(TIMEOUT);

		new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getButtonCreateNewLetter());
		inboxEmailPage.clickCreateNewLetterButton();
		newLetterPage = new NewLetterPage(driver);
		WebElement assertedElement = new MailboxFunctions(driver)
				.waitUtilElementToBeClicable(newLetterPage.getSaveButton());
		boolean isThatPage = new MailboxFunctions(driver).isPresent(assertedElement);
		String message = null;

		if (isThatPage) {
			letter = new Letter(to, theme, body);
			new MailboxFunctions(driver).writeNewLetter(letter);
			newLetterPage.clickSaveLetterButton();
			isThatPage = new MailboxFunctions(driver).isPresent(newLetterPage.getSaveLetterMessage()) && isThatPage;
			Assert.assertTrue(isThatPage, ELEMENT_SAVE_MESSAGE_IS_NOT_PRESENT);
			newLetterPage.clickMainPageLink();
			inboxEmailPage = new MainPage(driver);
			isThatPage = new MailboxFunctions(driver).isPresent(inboxEmailPage.getButtonCreateNewLetter())
					&& isThatPage;
			if (!isThatPage)
				message = ELEMENT_BUTTON_SAVE_IS_NOT_PRESENT;
		} else
			message = ELEMENT_BUTTON_CREATE_NEW_LETTER_IS_NOT_PRESENT;
		super.getLogger().info(FINISH_WRITE_LETTER_TEST);
		Assert.assertTrue(isThatPage, message);
	}

	@Test(groups = "send_a_letter", dependsOnGroups = "write_a_letter", dataProvider = "getData", dataProviderClass = LetterDataProvider.class)
	public void sendDraftTest(String to, String theme, String body) throws InterruptedException {
		super.getLogger().info(START_SEND_DRAFT_TEST);

		Thread.sleep(TIMEOUT);
		new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getDraftsLink());
		inboxEmailPage.clickDraftsLink();
		draftsPage = new DraftsPage(driver);
		driver = driver.switchTo().defaultContent();
		draftsPage.clickRecentLetterLink().clickRecentLetterLink();
		newLetterPage = new NewLetterPage(driver);

		Thread.sleep(TIMEOUT);
		boolean isThatPage = new MailboxFunctions(driver).equalsCreateNewLetterPageTitle(driver.getTitle());
		Assert.assertTrue(isThatPage, INCORRECT_TITLE);
		if (isThatPage) {
			new MailboxFunctions(driver).waitUtilElementToBeClicable(newLetterPage.getSendMessageButton());
			newLetterPage.clickSendMessageButton();
			isThatPage = new MailboxFunctions(driver).isPresent(newLetterPage.getSendMessageMessage());
		}
		super.getLogger().info(FINISH_SEND_DRAFT_TEST);
		Assert.assertTrue(isThatPage, LETTER_IS_NOT_SAVED);
	}

	@Test(groups = "verify_sent_letter", dependsOnGroups = "send_a_letter", dataProvider = "getData", dataProviderClass = LetterDataProvider.class, alwaysRun = true)
	public void veryfySentLetterTest(String to, String theme, String body) throws InterruptedException {
		super.getLogger().info(START_VERYFY_SENT_LETTER_TEST);
		boolean assertResult = false;

		inboxEmailPage = new MainPage(driver);

		new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getSentMessagesLink());
		Thread.sleep(TIMEOUT);

		inboxEmailPage.clickSentMessagesLink();
		sentMessagePage = new SentMessagePage(driver);
		sentMessagePage.clickRecentLetterLinkByText(theme, to).switchToMessageFrame();

		new MailboxFunctions(driver).waitUtilElementToBeClicable(sentMessagePage.getHeaderTo());
		boolean result = new MailboxFunctions(driver).equalsMessages(sentMessagePage, to, theme, body);
		assertResult = assertResult || result;
		driver.switchTo().defaultContent();

		super.getLogger().info(FINISH_VERYFY_SENT_LETTER_TEST);
		Assert.assertTrue(assertResult, UNCORRECTED_LETTER);
	}

	@AfterGroups("verify_sent_letter")
	public void clearTestData() throws InterruptedException {
		super.getLogger().info(START_CLEAR_TEST_DATA);
		inboxEmailPage = new MainPage(driver);
		inboxEmailPage.clickMainPageLink();
		inboxEmailPage = new MainPage(driver);
		new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getSentMessagesLink());
		Thread.sleep(TIMEOUT);
		inboxEmailPage.clickSentMessagesLink();
		sentMessagePage = new SentMessagePage(driver);
		sentMessagePage.dragAndDropRecentLetterByNumInTrash(4).dragAndDropRecentLetterInTrash().clickChoiseAllButton()
				.clickDeleteButton();
		super.getLogger().info(FINISH_CLEAR_TEST_DATA);
	}

}