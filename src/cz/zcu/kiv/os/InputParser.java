package cz.zcu.kiv.os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bydga
 */
public class InputParser {

	private Set<Character> escapeCharacters = new HashSet<>(Arrays.asList(new Character[]{'\\', '"', '\'', '|', '&', '>', '<'}));
	private char escapeSymbol = '\\';

	public String[] parse(String input) {

		char quotType;
		boolean inQuotation = false;

		List<String> output = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();

		int inputSize = input.length();
		for (int i = 0; i < inputSize; i++) {

			char currentChar = input.charAt(i);

			//whitespaces at the beginning
			while (currentChar == ' ' && output.isEmpty()) {
				currentChar = input.charAt(++i);
			}

			if (currentChar == this.escapeSymbol) {

				
				buffer.append(input.charAt(++i));
				break;
			}

			if (currentChar == ' ') {
				output.add(buffer.toString());
				buffer.delete(0, buffer.length());
				//skip additional spaces if present
				while (currentChar == ' ')
				{
					currentChar = input.charAt(++i);
				}
			}

			buffer.append(currentChar);

		}




		return output.toArray(new String[]{});
	}
}
