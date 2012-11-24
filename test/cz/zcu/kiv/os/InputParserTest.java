/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os;

import cz.zcu.kiv.os.core.InputParser;
import cz.zcu.kiv.os.terminal.ParseResult;
import java.io.BufferedReader;
import java.io.FileReader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author bydga
 */
public class InputParserTest {

	BufferedReader reader;

	public InputParserTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		this.reader = new BufferedReader(new FileReader("tests.txt"));
	}

	@After
	public void tearDown() throws Exception {

		this.reader.close();

	}

	protected void testCase(int lineNumber) throws Exception {
		InputParser parser = new InputParser();

		String command = null;
		String expectedResult = null;
		int counter = 0;
		boolean testDone = false;
		while ((command = reader.readLine()) != null) {

			if (counter == lineNumber) {
				expectedResult = reader.readLine();
				if (expectedResult == null) {
					fail("Missing expected output");
				}

				String result = parser.parse(command).toTestString();
				assertEquals("Test case on line " + lineNumber + " failed", expectedResult, result);
				testDone = true;
			}
			counter++;
		}

		if (!testDone) {
			fail("Test case on line " + lineNumber + " not found");
		}
	}

	/**
	 * Test of parse method, of class InputParser.
	 */
	@Test
	public void testParse0() throws Exception {
		this.testCase(0);
	}

	@Test
	public void testParse2() throws Exception {
		this.testCase(2);
	}

	@Test
	public void testParse4() throws Exception {
		this.testCase(4);
	}

	@Test
	public void testParse6() throws Exception {
		this.testCase(6);
	}

	@Test
	public void testParse8() throws Exception {
		this.testCase(8);
	}

	@Test
	public void testParse10() throws Exception {
		this.testCase(10);
	}

	@Test
	public void testParse12() throws Exception {
		this.testCase(12);
	}
	
	@Test
	public void testParse14() throws Exception {
		this.testCase(14);
	}
	
	@Test
	public void testParse16() throws Exception {
		this.testCase(16);
	}
	
}
