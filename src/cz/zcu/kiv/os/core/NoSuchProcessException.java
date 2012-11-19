package cz.zcu.kiv.os.core;

/**
 * Exception indicating that a process with desired name doesn't exist.
 *
 * @author bydga
 */
public class NoSuchProcessException extends Exception {

	/**
	 * Creates new instance of NoSuchProcessException class.
	 *
	 * @param message Reason why the exception is thrown.
	 */
	public NoSuchProcessException(String message) {
		super(message);
	}

	/**
	 * Creates new instance of NoSuchProcessException class.
	 *
	 */
	public NoSuchProcessException() {
		super();
	}
}
