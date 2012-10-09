package cz.zcu.kiv.os;

import java.util.*;

/**
 *
 * @author bydga
 */
public class InputParser {

	private char escapeSymbol = '\\';
	private Set<Character> quotationMarks = new HashSet<Character>(Arrays.asList(new Character[]{'"', '\''}));

	public ParseResult parse(String input) {
		input = input.trim();
		int inputSize = input.length();
		char quotType = ' ';
		boolean inQuotation = false;
		boolean inStdIn = false;
		boolean inStdOut = false;
		boolean stdOutAppend = false;
		boolean inStdErr = false;
		boolean stdErrAppend = false;

		ParseResult result = new ParseResult();

		List<String> parameters = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < inputSize; i++) {

			char currentChar = input.charAt(i);

			//bg operator is the last one, dont care whats after it
			if (currentChar == '&' && !inQuotation) {
				result.isBackgroundTask = true;
				break; //breaking out of the main for-loop: & is the last character
			}

			buffer.append(currentChar);

			// " and ' symbols
			if (this.quotationMarks.contains(currentChar)) {

				//already inside 
				if (inQuotation) {
					//same one - end of quotation block
					if (currentChar == quotType) {
						inQuotation = false;
						buffer.deleteCharAt(buffer.length() - 1);

					}
				} else {
					//quotation beginning
					inQuotation = true;
					quotType = currentChar;
					buffer.deleteCharAt(buffer.length() - 1);
				}
			}

			//end of parameter/input
			if ((currentChar == ' ' && !inQuotation) || (i == inputSize - 1)) {

				if (currentChar == ' ') {
					buffer.deleteCharAt(buffer.length() - 1);
				}
				if (inStdIn) {
					result.stdIn = buffer.toString();
				} else if (inStdOut) {
					result.stdOut = buffer.toString();
					result.stdOutAppend = stdOutAppend;
				} else if (inStdErr) {
					result.stdErr = buffer.toString();
					result.stdErrAppend = stdErrAppend;
				} else {
					parameters.add(buffer.toString());
					//skip additional spaces if present
					while (i + 1 < inputSize) {
						if (input.charAt(i + 1) == ' ') {
							i++;
						} else {
							break;
						}
					}
				}
				inStdIn = false;
				inStdOut = false;
				inStdErr = false;
				stdOutAppend = false;
				stdErrAppend = false;
				buffer.delete(0, buffer.length());
				continue;
			} //check if it is fwd parameter - need to do this before escaping as the information would be lost

			if (buffer.length() == 1 && (currentChar == '>' || currentChar == '<' || currentChar == '2')) {

				buffer.deleteCharAt(buffer.length() - 1);
				if (currentChar == '>') {
					inStdOut = true;
					if (i + 1 < inputSize && input.charAt(i + 1) == '>') {
						stdOutAppend = true;
						i++;
					}
				} else if (currentChar == '<') {
					inStdIn = true;
				} else if (currentChar == '2') {
					if (i + 1 < inputSize && input.charAt(i + 1) == '>') {
						inStdErr = true;
						i++;
						if (i + 1 < inputSize && input.charAt(i + 1) == '>') {
							stdErrAppend = true;
							i++;
						}
					}
				}

				//skip additional spaces if present
				while (i + 1 < inputSize) {
					if (input.charAt(i + 1) == ' ') {
						i++;
					} else {
						break;
					}
				}
				continue;
			} //escape symbol
			if (currentChar == this.escapeSymbol) {
				buffer.deleteCharAt(buffer.length() - 1);
				//advance to the escaped character
				i++;
				if (i >= inputSize) {
					throw new RuntimeException("No symbol after escape sequence");
				}

				buffer.append(input.charAt(i));
				//next character
			}

		}

		if (inStdIn) {
			result.stdIn = buffer.toString();
		} else if (inStdOut) {
			result.stdOut = buffer.toString();
			result.stdOutAppend = stdOutAppend;
		} else if (inStdErr) {
			result.stdErr = buffer.toString();
			result.stdErrAppend = stdErrAppend;
		} else if (buffer.length() > 0) {
			parameters.add(buffer.toString());
		}

		if (inQuotation) {
			throw new RuntimeException("Missing corresponding quotation mark");
		}
		result.args = parameters.toArray(new String[]{});
		return result;
	}
}
