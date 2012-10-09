package cz.zcu.kiv.os;

import java.util.*;

/**
 *
 * @author bydga
 */
public class InputParser {

	private char escapeSymbol = '\\';
	private Set<Character> quotationMarks = new HashSet<Character>(Arrays.asList(new Character[]{'"', '\''}));

	public String[] parse(String input) {
		input = input.trim();
		int inputSize = input.length();
		char quotType = ' ';
		boolean inQuotation = false;
		
		List<String> output = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		
		for (int i = 0; i < inputSize; i++) {

			char currentChar = input.charAt(i);
			
			//escape symbol
			if (currentChar == this.escapeSymbol) {
				//advance to the escaped character
				i++;
				if (i >= inputSize) {
					throw new RuntimeException("No symbol after escape sequence");
				}

				buffer.append(input.charAt(i));
				//next character
				continue;
			}

			//end of parameter
			if (currentChar == ' ' && !inQuotation) {
				output.add(buffer.toString());
				buffer.delete(0, buffer.length());
				//skip additional spaces if present
				while (i + 1 < inputSize) {
					if (input.charAt(i + 1) == ' ') {
						i++;
					} else {
						break;
					}
				}

				continue;
			}

			// " and ' symbols
			if (this.quotationMarks.contains(currentChar)) {

				//already inside 
				if (inQuotation) {
					//same one - end of quotation block
					if (currentChar == quotType) {
						inQuotation = false;

					} else {
						// "'" or '"' case
						buffer.append(currentChar);

					}
				} else {
					//quotation beginning
					inQuotation = true;
					quotType = currentChar;
				}
				continue;
			}

			buffer.append(currentChar);

		}

		if (buffer.length() > 0) {
			output.add(buffer.toString());
		}
		if (inQuotation) {
			throw new RuntimeException("Missing corresponding quotation mark");
		}
		return output.toArray(
				new String[]{});
	}
}
