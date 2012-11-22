package cz.zcu.kiv.os.terminal;

import java.util.*;

/**
 * Handles parsing of the input command in shell and returns structure containing compatibile command definition.
 *
 * @author bydga
 */
public class InputParser {

	public static final char ESCAPE_SYMBOL = '\\';
	public static final Set<Character> QUOTATION_MARKS = new HashSet<Character>(Arrays.asList(new Character[]{'"', '\''}));
	public static final char BACKGROUND_OPERATOR = '&';
	public static final char PIPELINE_OPERATOR = '|';
	public static final char PARAMETER_DELIMITER = ' ';

	/**
	 * Parses the input and returns linked structure of the given command.
	 *
	 * @param input String representing the command input.
	 * @return ParseResult structure. Linked list connected by pipeline - each containig one process definition.
	 * @throws ParseException Is thrown when the input is not a valid input parser.
	 */
	public ParseResult parse(String input) throws ParseException {
		input = input.trim();
		int inputSize = input.length();
		char quotType = ' ';
		boolean inQuotation = false;
		boolean inStdIn = false;
		boolean inStdOut = false;
		boolean stdOutAppend = false;

		ParseResult result = new ParseResult();

		List<String> parameters = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < inputSize; i++) {

			char currentChar = input.charAt(i);

			//bg operator is the last one, dont care whats after it
			if (currentChar == InputParser.BACKGROUND_OPERATOR && !inQuotation) {
				result.isBackgroundTask = true;
				break; //breaking out of the main for-loop: & is the last character
			}

			if (currentChar == InputParser.PIPELINE_OPERATOR && !inQuotation) {
				result.pipeline = this.parse(input.substring(i + 1));
				result.isBackgroundTask = result.pipeline.isBackgroundTask;
				break;
			}

			buffer.append(currentChar);

			// " and ' symbols
			if (InputParser.QUOTATION_MARKS.contains(currentChar)) {

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
			if ((currentChar == InputParser.PARAMETER_DELIMITER && !inQuotation) || (i == inputSize - 1)) {

				if (currentChar == InputParser.PARAMETER_DELIMITER) {
					buffer.deleteCharAt(buffer.length() - 1);
				}
				if (inStdIn) {
					result.stdIn = buffer.toString();
				} else if (inStdOut) {
					result.stdOut = buffer.toString();
					result.stdOutAppend = stdOutAppend;
				} else {
					parameters.add(buffer.toString());
					//skip additional spaces if present
					while (i + 1 < inputSize) {
						if (input.charAt(i + 1) == InputParser.PARAMETER_DELIMITER) {
							i++;
						} else {
							break;
						}
					}
				}
				inStdIn = false;
				inStdOut = false;
				stdOutAppend = false;
				buffer.delete(0, buffer.length());
				continue;
			}

			if (!inQuotation && (currentChar == '>' || currentChar == '<')) {

				//store previous param
				if (inStdIn) {
					buffer.deleteCharAt(buffer.length() - 1);
					result.stdIn = buffer.toString();
				} else if (inStdOut) {
					buffer.deleteCharAt(buffer.length() - 1);
					result.stdOut = buffer.toString();
					result.stdOutAppend = stdOutAppend;
				} else {
					if (buffer.length() > 1) {
						buffer.deleteCharAt(buffer.length() - 1);
						parameters.add(buffer.toString());

					}
					//skip additional spaces if present
					while (i + 1 < inputSize) {
						if (input.charAt(i + 1) == InputParser.PARAMETER_DELIMITER) {
							i++;
						} else {
							break;
						}
					}
				}
				inStdIn = false;
				inStdOut = false;
				stdOutAppend = false;
				buffer.delete(0, buffer.length());

				//check if it is fwd parameter - need to do this before escaping as the information would be lost
				if (currentChar == '>') {
					inStdOut = true;
					if (i + 1 < inputSize && input.charAt(i + 1) == '>') {
						stdOutAppend = true;
						i++;
					}
				} else if (currentChar == '<') {
					inStdIn = true;
				}

				//skip additional spaces if present
				while (i + 1 < inputSize) {
					if (input.charAt(i + 1) == InputParser.PARAMETER_DELIMITER) {
						i++;
					} else {
						break;
					}
				}
				continue;
			}

			if (currentChar == InputParser.ESCAPE_SYMBOL) {
				buffer.deleteCharAt(buffer.length() - 1);
				//advance to the escaped character
				i++;
				if (i >= inputSize) {
					throw new ParseException("No symbol after escape sequence");
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
		} else if (buffer.length() > 0) {
			parameters.add(buffer.toString());
		}

		if (inQuotation) {
			throw new ParseException("Missing corresponding quotation mark");
		}
		result.args = parameters.toArray(new String[]{});
			return result;
	}
}
