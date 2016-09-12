package com.epam.maya_semina.test;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.epam.maya_semina.mail.bo.letter.LetterInterface;
import com.epam.maya_semina.mail.factoryLetters.ArrayLettersCreator;
import com.epam.maya_semina.mailService.MailboxFunctions;
import com.epam.maya_semina.ui.pageObject.DraftsPage;
import com.epam.maya_semina.ui.pageObject.MainPage;
import com.epam.maya_semina.ui.pageObject.MessagePage;
import com.epam.maya_semina.ui.pageObject.NewLetterPage;
import com.epam.maya_semina.ui.pageObject.SentMessagePage;

public class MailboxTestPatterns extends BaseTest {

	private static final String FINISH_CLEAR_TEST_DATA = "finish: 'clearTestData'";
	private static final String START_CLEAR_TEST_DATA = "start: 'clearTestData'";
	private static final String INCORRECT_LETTERS_SET = "Incorrect letters set";
	private static final String FINISH_VERYFY_SENT_LETTER_TEST = "finish: 'veryfySentLetterTest'";
	private static final String START_VERYFY_SENT_LETTER_TEST = "start: 'veryfySentLetterTest'";
	private static final String FINISH_SEND_DRAFT_TEST = "finish: 'sendDraftTest'";
	private static final String START_SEND_DRAFT_TEST = "start: 'sendDraftTest'";
	private static final String FINISH_WRITE_LETTER_TEST = "finish: 'writeLetterTest'";
	private static final String START_WRITE_LETTER_TEST = "start: 'writeLetterTest'";
	private static final int TIMEOUT = 900;
	private static final String LETTER_IS_NOT_SAVED = "Letter is not saved";

	private MainPage inboxEmailPage;
	private NewLetterPage newLetterPage;
	private DraftsPage draftsPage;
	private MessagePage sentMessagePage;
	private WebDriver driver;
	ArrayList<LetterInterface> letters;

	// 1 Авторизоваться в системе пользователем Отображается на экране почта
	// пользователя

	@BeforeClass
	public void mailBoxTestSetUp() {
		this.driver = super.getDriver();
		this.inboxEmailPage = new MainPage(driver);
		letters = ArrayLettersCreator.getArrayList();
	}

	@Test(groups = "write_a_letter")
	public void writeLetterTest() throws InterruptedException {

		super.getLogger().info(START_WRITE_LETTER_TEST);
		
		boolean isSentAll = true;

		for (LetterInterface letter : letters) {
			Thread.sleep(TIMEOUT);
			inboxEmailPage.clickCreateNewLetterButton();
			newLetterPage = new NewLetterPage(driver);
			new MailboxFunctions(driver).writeNewLetter(letter);
			newLetterPage.clickSaveLetterButton();
			boolean isSentLetter = new MailboxFunctions(driver).isPresent(newLetterPage.getSaveLetterMessage());
			isSentAll = isSentAll && isSentLetter;
			newLetterPage.clickMainPageLink();
			inboxEmailPage = new MainPage(driver);
		}
		super.getLogger().info(FINISH_WRITE_LETTER_TEST);
		Assert.assertTrue(isSentAll, "Messages not sent.");

	}

	@Test(groups = "send_a_letter", dependsOnGroups = "write_a_letter")
	public void sendDraftTest() throws InterruptedException {
		super.getLogger().info(START_SEND_DRAFT_TEST);
		boolean isThatPage = true;
		for (int i = 0; i < letters.size(); i++) {
			Thread.sleep(TIMEOUT);
			new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getDraftsLink());
			inboxEmailPage.clickDraftsLink();
			draftsPage = new DraftsPage(driver);
			driver = driver.switchTo().defaultContent();
			draftsPage.clickRecentLetterLink().clickRecentLetterLink();
			newLetterPage = new NewLetterPage(driver);

			Thread.sleep(TIMEOUT);
			isThatPage = new MailboxFunctions(driver).equalsCreateNewLetterPageTitle(driver.getTitle())&&isThatPage;
			
			if (isThatPage) {
				new MailboxFunctions(driver).waitUtilElementToBeClicable(newLetterPage.getSendMessageButton());
				newLetterPage.clickSendMessageButton();
				isThatPage = new MailboxFunctions(driver).isPresent(newLetterPage.getSendMessageMessage());
			}
		}
		super.getLogger().info(FINISH_SEND_DRAFT_TEST);
		Assert.assertTrue(isThatPage, LETTER_IS_NOT_SAVED);
	}

	@Test(groups = "verify_sent_letter", dependsOnGroups = "send_a_letter")
	public void veryfySentLetterTest() throws InterruptedException {
		super.getLogger().info(START_VERYFY_SENT_LETTER_TEST);

		inboxEmailPage = new MainPage(driver);

		boolean assertedResult = true;
		
		for (LetterInterface letter : letters) {
			new MailboxFunctions(driver).waitUtilElementToBeClicable(inboxEmailPage.getSentMessagesLink());
			Thread.sleep(TIMEOUT);
			inboxEmailPage.clickSentMessagesLink();
			sentMessagePage = new SentMessagePage(driver);
			sentMessagePage.clickRecentLetterLinkByText(letter.getTheme(), letter.getTo()).switchToMessageFrame();
			new MailboxFunctions(driver).waitUtilElementToBeClicable(sentMessagePage.getHeaderTo());
			boolean result = new MailboxFunctions(driver).equalsMessages(sentMessagePage, letter);
			driver.switchTo().defaultContent();
			assertedResult = assertedResult && result;
		}
		Assert.assertTrue(assertedResult, INCORRECT_LETTERS_SET);
		super.getLogger().info(FINISH_VERYFY_SENT_LETTER_TEST);
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
