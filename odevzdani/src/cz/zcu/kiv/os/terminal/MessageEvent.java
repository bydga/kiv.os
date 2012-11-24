package cz.zcu.kiv.os.terminal;

import java.awt.AWTEvent;

/**
 * Event for sending messages to terminal window. The message should be
 * displayed at terminal's main window.
 *
 * @author Jakub Danek
 */
public class MessageEvent extends AWTEvent {

	public static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1;

	private final String message;
	private final boolean append;

	/**
	 * Creates event with given source (probably terminal frame) and message
	 *
	 * Message is appended to the current terminal content. (sets append flag to TRUE)
	 *
	 * @param source event source
	 * @param message payload
	 */
	public MessageEvent(Object source, String message) {
		this(source, message, true);
	}

	/**
	 * Creates event with given source (probably terminal frame), message and
	 * append parameter.
	 *
	 * If append is false, replaces whole terminal content with the message.
	 *
	 * @param source event source
	 * @param message payload
	 * @param append should append the message to the terminal content
	 */
	public MessageEvent(Object source, String message, boolean append) {
		super(source, EVENT_ID);
		this.message = message;
		this.append = append;
	}

	/**
	 *
	 * @return payload message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @return true if message should be appended to the terminal content
	 */
	public boolean isAppend() {
		return append;
	}

}
