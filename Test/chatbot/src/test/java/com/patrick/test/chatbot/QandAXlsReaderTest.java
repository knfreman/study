package com.patrick.test.chatbot;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class QandAXlsReaderTest {

	@Test
	public void testReadString() throws Exception {
		List<List<Map<String, List<String>>>> questionsAndAnswersList = QandAXlsReader.STRING
				.read("QandAXlsReaderForStringTest.xls");

		Assert.assertEquals(4, questionsAndAnswersList.size());
		// use case 1
		// question 1
		verify(questionsAndAnswersList, 0, 0, "Hello!", new String[] { "Hi!", "Hello!", "Yes?" });
		// use case 2
		// question 1
		verify(questionsAndAnswersList, 1, 0, "Hello!", new String[] { "Hi!", "Hello!", "Yes?" });
		// use case 3
		verify(questionsAndAnswersList, 1, 2, "I don't remember.",
				new String[] { "Sorry, I can't help you to change address." });
		// use case 3
		// question 2
		verify(questionsAndAnswersList, 3, 1, "I would like to get my policy status.",
				new String[] { "May I have your email please?", "Can you tell me your email address?" });
	}

	@Test
	public void testReadKey() throws Exception {
		List<List<Map<String, List<String>>>> questionsAndAnswersList = QandAXlsReader.KEY
				.read("QandADemoForKeySpec.xls");

		Assert.assertEquals(2, questionsAndAnswersList.size());
		// use case 1
		// question 1
		verify(questionsAndAnswersList, 0, 0, "hello", new String[] { "MP00000" });
		// question 2
		verify(questionsAndAnswersList, 0, 1, "change address", new String[] { "MP00004", "MP00006" });
		// use case 2
		// question 1
		verify(questionsAndAnswersList, 1, 0, "hello", new String[] { "MP00000" });
		// question 2
		verify(questionsAndAnswersList, 1, 1, "I want to change my address", new String[] { "MP00004", "MP00006" });
		// question 3
		verify(questionsAndAnswersList, 1, 2, "change address", new String[] { "MP00006" });
	}

	private void verify(List<List<Map<String, List<String>>>> questionsAndAnswersList, int rowIndex, int columnIndex,
			String question, String[] answers) {
		List<Map<String, List<String>>> questionsAndAnswers = questionsAndAnswersList.get(rowIndex);
		Map<String, List<String>> questionAndAnswers = questionsAndAnswers.get(columnIndex);
		List<String> answersList = questionAndAnswers.get(question);
		Assert.assertEquals(answers.length, answersList.size());

		for (String answer : answers) {
			Assert.assertTrue(answersList.contains(answer));
		}
	}
}
