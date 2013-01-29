package org.jenkinsci.testinprogress.runner;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.concurrent.Future;

import org.junit.Test;

import de.oschoen.junit.runner.ProgressAllTests;


public class ProgressBatchSuiteTest extends AbstractProgressSuiteTest {

	@Test
	public void testProgressBatchSuite() throws Exception {
		// Given

		// When
		Future<String[]> result = runProgressSuite(ProgressAllTests.class);

		// Then
		String[] messages = result.get();
		printTestMessages(messages);
		assertThat(messages[0], containsString("%TESTC  5 v2"));
		assertThat(messages[messages.length-1], containsString("%RUNTIME"));
	}	
	
}
