package cz.zcu.kiv.os.core.filesystem;

/**
 * Exception thrown when a file/directory path contains invalid elements.
 *
 * @author Jakub Danek
 */
public class InvalidPathCharactersException extends Exception {

	private static String charList = null;

	public InvalidPathCharactersException(Throwable cause) {
		super(cause);
	}

	public InvalidPathCharactersException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPathCharactersException(String message) {
		super(message);
	}

	public InvalidPathCharactersException() {
	}

	public static String invalidCharsList() {
		if(charList == null) {
			StringBuilder b = new StringBuilder();
			for(char c : FileManager.ILLEGAL_FILE_CHARS) {
				b.append('"');
				b.append(c);
				b.append("\" ");
			}
			charList = b.toString();
		}
		return charList;
	}

}
